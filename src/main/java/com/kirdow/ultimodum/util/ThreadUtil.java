package com.kirdow.ultimodum.util;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtil {

    public static ThreadUtil get() { return util; }
    private static final ThreadUtil util = new ThreadUtil();
    private ThreadUtil() {}

    void tick() {
        try {
            runs();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private static List<Locker> runOnMainThread = new ArrayList<>();
    private static final Object mutex = new Object();

    public static void runOnMainThread(final Runnable runnable) {
        if (Minecraft.getInstance().isSameThread()) {
            runnable.run();
            return;
        }

        Locker locker = new Locker(runnable);
        synchronized (mutex) {
            runOnMainThread.add(locker);
        }

        locker.join();
    }

    private void runs() {
        Locker[] lockers = null;

        synchronized (mutex) {
            lockers = runOnMainThread.toArray(new Locker[0]);
            runOnMainThread.clear();
        }

        for (Locker locker : lockers) {
            locker.run();
        }
    }

    private static class Locker {
        private Runnable runnable;
        private final Object mutex = new Object();

        private boolean locked = true;

        public Locker(Runnable run) {
            runnable = run;
        }

        public void run() {
            try {
                if (runnable != null)
                    runnable.run();
            } finally {
                unlock();
            }
        }

        public void join() {
            while(locked()) {
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException ignored) {
                }
            }
        }

        private boolean locked() {
            synchronized (mutex) {
                return locked;
            }
        }

        private void unlock() {
            synchronized (mutex) {
                locked = false;
            }
        }
    }



}
