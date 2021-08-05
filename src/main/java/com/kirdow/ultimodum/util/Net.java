package com.kirdow.ultimodum.util;

import java.io.*;
import java.util.function.Consumer;

public class Net {

    public static final void close(Closeable c) {
        try {
            if (c != null)
                c.close();
        } catch (Throwable ignored) {
        }
    }

    public static final void flush(Flushable f) {
        try {
            if (f != null)
                f.flush();
        } catch (Throwable ignored) {
        }
    }

    public static final BufferedWriter writerFromFile(File file) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (Throwable ignored) {
        }
        return writer;
    }

    public static final BufferedReader readerFromFile(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Throwable ignored) {
        }
        return reader;
    }

    public static final boolean useWriterForFile(File file, IOConsumer<BufferedWriter> consumer) {
        BufferedWriter writer = writerFromFile(file);
        if (writer == null)
            return false;

        try {
            if (consumer != null) {
                consumer.accept(writer);
                return true;
            }
        } catch (Throwable ignored) {
        } finally {
            flush(writer);
            close(writer);
        }

        return false;
    }

    public static final boolean useReaderForFile(File file, IOConsumer<BufferedReader> consumer) {
        BufferedReader reader = readerFromFile(file);
        if (reader == null)
            return false;

        try {
            if (consumer != null) {
                consumer.accept(reader);
                return true;
            }
        } catch (Throwable ignored) {
        } finally {
            close(reader);
        }

        return false;
    }

    public interface IOConsumer<T> {
        void accept(T value) throws IOException;
    }

}
