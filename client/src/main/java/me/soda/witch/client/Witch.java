package me.soda.witch.client;

import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Config;
import me.soda.witch.client.websocket.MessageUtils;
import me.soda.witch.client.websocket.WSClient;
import me.soda.witch.shared.XOR;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final ChatCommandLogging chatCommandLogging = new ChatCommandLogging();
    public static final MessageUtils messageUtils = new MessageUtils(new XOR(getValue("key")));
    private static final boolean print = Boolean.getBoolean("fabric.development");
    //config
    private static final String server = getValue("server");

    //variables
    public static WSClient client;
    public static Config config = new Config();

    public static void init() {
        chatCommandLogging.sendLogThread.start();
        try {
            client = new WSClient(new URI(server));
            client.connect();
        } catch (Exception e) {
            tryReconnect(client::reconnect);
        }
    }

    public static void tryReconnect(Runnable reconnect) {
        Witch.println("Connection closed");
        try {
            Thread.sleep(30 * 1000);
        } catch (Exception e) {
            Witch.printStackTrace(e);
        } finally {
            new Thread(reconnect).start();
        }
    }

    private static String getValue(String key) {
        return new String(Base64.getDecoder().decode(FabricLoader.getInstance()
                .getModContainer("witch")
                .get().getMetadata()
                .getCustomValue(key)
                .getAsString()), StandardCharsets.UTF_8);
    }

    public static void printStackTrace(Exception e) {
        if (Witch.print) e.printStackTrace();
    }

    public static void println(Object o) {
        if (Witch.print) System.out.println("[WITCH] " + o);
    }
}
