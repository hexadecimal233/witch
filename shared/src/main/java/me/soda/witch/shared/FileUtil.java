package me.soda.witch.shared;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtil {
    public static String read(File file) {
        try {
            return Files.readString(file.toPath(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
            return "";
        }
    }

    public static byte[] readBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
            return new byte[0];
        }
    }

    public static void write(File file, String data) {
        try {
            Files.createDirectories(file.toPath().getParent());
            Files.writeString(file.toPath(), data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
    }

    public static void writeBytes(File file, byte[] data) {
        try {
            Files.createDirectories(file.toPath().getParent());
            Files.write(file.toPath(), data);
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
        }
    }
}
