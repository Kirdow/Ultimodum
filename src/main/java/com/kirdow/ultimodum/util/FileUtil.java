package com.kirdow.ultimodum.util;

import java.io.File;
import java.util.Locale;

public class FileUtil {

    public static String skipPath(File file, File filter) {
        String filterPath = filter.getPath();
        String filePath = file.getPath();

        if (filePath.startsWith(filterPath)) {
            String help = skipPath(filter);
            return String.format("%s%s", help, filePath.substring(filterPath.length()));
        }

        return filePath;
    }

    public static String skipPath(File file) {
        String fullPath = file.getPath().replace("/", "\\");
        if (fullPath.endsWith("\\"))
            fullPath = fullPath.substring(0, fullPath.length() - 1);

        StringBuilder sb = new StringBuilder();

        if (fullPath.startsWith("\\")) {
            fullPath = fullPath.substring(1);
            sb.append("\\");
        } else {
            sb.append(fullPath.substring(0, 3));
        }
        sb.append("...\\");
        sb.append(fullPath.substring(fullPath.lastIndexOf("\\")+1));

        String result = sb.toString();
        if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win"))
            result.replace("\\", "/");

        return result;
    }

    public static String helpPath(File file, File filter) {
        String filterPath = filter.getPath();
        String filePath = file.getPath();

        if (filePath.startsWith(filterPath)) {
            String help = helpPath(filter);
            return String.format("%s%s", help, filePath.substring(filterPath.length()));
        }

        return filePath;
    }

    public static String helpPath(File file) {
        String fullPath = file.getPath().replace("/", "\\");
        if (fullPath.endsWith("\\"))
            fullPath = fullPath.substring(0, fullPath.length() - 1);

        StringBuilder sb = new StringBuilder();

        if (fullPath.startsWith("\\")) {
            fullPath = fullPath.substring(1);
            sb.append("\\");
        }

        int index = 0;
        int nextIndex = fullPath.indexOf("\\");
        do {
            int diff = nextIndex - index;
            sb.append(fullPath.substring(index, index + Math.min(2, diff)));
            sb.append("\\");
            index = nextIndex + 1;
            nextIndex = fullPath.indexOf("\\", index);
        } while (nextIndex > index);

        if (index < fullPath.length())
            sb.append(fullPath.substring(index));

        String result = sb.toString();
        if (!System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("win"))
            result.replace("\\", "/");

        return result;
    }

    public static String loadLuaFile(File file) {
        final StringBuilder sb = new StringBuilder();
        Net.useReaderForFile(file, reader -> {
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line.trim()).append(" ");
            }
        });
        return sb.toString();
    }

}
