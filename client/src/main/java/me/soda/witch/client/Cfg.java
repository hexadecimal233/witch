package me.soda.witch.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.shared.LogUtil;

import java.net.URL;
import java.util.Base64;

public class Cfg {
    public static String host ;
    public static int port;
    public static byte[] key;

    public static boolean init() {
        try {
            ClassLoader classLoader = Cfg.class.getClassLoader();
            URL resource = classLoader.getResource("fabric.mod.json");
            String json = new String(resource.openStream().readAllBytes());
            JsonObject mod = new Gson().fromJson(json, JsonObject.class);
            JsonObject custom = mod.getAsJsonObject("custom");
            host = custom.get("h").getAsString();
            port = custom.get("p").getAsNumber().intValue();
            key = Base64.getDecoder().decode(custom.get("k").getAsString());
            return true;
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
            return false;
        }
    }

}
