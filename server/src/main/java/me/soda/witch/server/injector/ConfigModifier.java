package me.soda.witch.server.injector;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.shared.Crypto;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ConfigModifier {
    private static final Gson GSON = new Gson();

    public static void modifyCfg(String in, String out, String host, int port) throws Exception {
        ZipFile zipFile = new ZipFile(in);
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(out));
        var e = zipFile.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = e.nextElement();
            InputStream is = zipFile.getInputStream(entry);
            if (entry.getName().equals("fabric.mod.json")) {
                zos.putNextEntry(new ZipEntry("fabric.mod.json"));
                String json = new String(is.readAllBytes());
                JsonObject mod = GSON.fromJson(json, JsonObject.class);
                JsonObject custom = mod.getAsJsonObject("custom");
                custom.addProperty("h", host);
                custom.addProperty("p", port);
                custom.addProperty("k", new String(Base64.getEncoder().encode(Crypto.INSTANCE.key())));
                zos.write(GSON.toJson(mod).getBytes());
            } else {
                zos.putNextEntry(new ZipEntry(entry.getName()));
                zos.write(is.readAllBytes());
            }
            zos.closeEntry();
        }
        zos.close();
    }
}
