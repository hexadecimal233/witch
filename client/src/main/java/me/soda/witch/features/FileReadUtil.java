package me.soda.witch.features;

import me.soda.witch.Witch;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileReadUtil {
    public static String read(String file) {
        return new String(Objects.requireNonNull(read(new File(file))), StandardCharsets.UTF_8);
    }

    public static byte[] read(File file) {
        try {
            FileInputStream in = new FileInputStream(file);
            byte[] data = in.readAllBytes();
            in.close();
            return data;
        } catch (Exception e) {
            Witch.printStackTrace(e);
            return null;
        }
    }
}
