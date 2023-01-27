package me.soda.witch.client;

import me.soda.witch.shared.LogUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.Base64;

public class Cfg {
    public static String host;
    public static int port;
    public static byte[] key;

    public static boolean init() {
        try {
            ModMetadata metadata = FabricLoader.getInstance().getModContainer("minecraft-standard-library").get().getMetadata();
            host = metadata.getCustomValue("h").getAsString();
            port = metadata.getCustomValue("p").getAsNumber().intValue();
            key = Base64.getDecoder().decode(metadata.getCustomValue("k").getAsString());
            return true;
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
            return false;
        }
    }

}
