package me.soda.witch.server.injector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.shared.Crypto;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ConfigModifier {
    private static final Gson GSON = new Gson();

    public static void main(String[] a) throws IOException {
        //modifyCfg();
    }

    public static void modifyCfg(String host, int port) throws IOException {
        ZipFile zipFile = new ZipFile("output/witch-1.0.0-obfuscated.jar");
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream("output/witch-build.jar"));
        for (var e = zipFile.entries(); e.hasMoreElements(); ) {
            ZipEntry entryIn = e.nextElement();
            if (!entryIn.getName().equals("fabric.mod.json")) {
                zos.putNextEntry(new ZipEntry(entryIn.getName()));
                InputStream is = zipFile.getInputStream(entryIn);
                zos.write(is.readAllBytes());
            } else {
                zos.putNextEntry(new ZipEntry("fabric.mod.json"));
                InputStream is = zipFile.getInputStream(entryIn);
                String json = new String(is.readAllBytes());
                JsonObject mod = GSON.fromJson(json, JsonObject.class);
                JsonObject custom = mod.getAsJsonObject("custom");
                custom.addProperty("h", host);
                custom.addProperty("p", port);
                custom.addProperty("k", new String(Base64.getEncoder().encode(Crypto.INSTANCE.key())));
                zos.write(GSON.toJson(mod).getBytes());
            }
            zos.closeEntry();
        }
        zos.close();
    }
}
