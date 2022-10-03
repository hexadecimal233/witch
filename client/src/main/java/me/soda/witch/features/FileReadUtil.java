package me.soda.witch.features;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class FileReadUtil {
    public static String read(String file) {
        try {
            FileInputStream in = new FileInputStream(file);
            String data = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            in.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
