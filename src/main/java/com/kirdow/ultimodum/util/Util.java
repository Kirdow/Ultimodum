package com.kirdow.ultimodum.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Util {

    public static final Util get() { return util; }
    private static final Util util = new Util();
    private Util(){}

    public static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(util);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        ThreadUtil.get().tick();
    }



}
