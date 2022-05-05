package com.cooldesktop.app.jarprocessmanager.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    public static String getWorkPath() {
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "work");
            if (!Files.exists(path)) Files.createDirectories(path);
            return path.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String createTempFile(int jid, String className) {
        return (Paths.get(getWorkPath(), jid + "_" + className + ".tmp")).toString();
    }
}
