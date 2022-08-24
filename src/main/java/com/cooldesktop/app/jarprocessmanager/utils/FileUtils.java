package com.cooldesktop.app.jarprocessmanager.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static String getWorkPath() {
        try {
            Path  path = Paths.get(System.getProperty("user.home"), "cooldesktop-work","jar-process-manager");
            if (!Files.exists(path)) Files.createDirectories(path);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isClass(byte[] bytes) {
        return convertByteArrayToInt(bytes) == -889275714;
    }

    private static int convertByteArrayToInt(byte[] data) {
        if (data == null || data.length < 4) return 0x0;
        return (0xff & data[0]) << 24 |
                (0xff & data[1]) << 16 |
                (0xff & data[2]) << 8 |
                (0xff & data[3]);
    }

}
