package me.soda.witch.server.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtil {
    public static String read(String file) {
        return new String(Objects.requireNonNull(read(new File(file))), StandardCharsets.UTF_8);
    }

    public static byte[] read(File file) {
        try (FileInputStream is = new FileInputStream(file)) {
            return is.readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void write(File file, String data) {
        write(file, data.getBytes(StandardCharsets.UTF_8));
    }

    private void write(File file, byte[] data) {
        try {
            new File(file.getParent()).mkdir();
            file.createNewFile();
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
