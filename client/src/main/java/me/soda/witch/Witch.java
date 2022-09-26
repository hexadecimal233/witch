package me.soda.witch;

import me.soda.witch.websocket.WSClient;
import net.minecraft.client.MinecraftClient;

import java.net.URI;

public class Witch {
    public static final String server = "ws://127.0.0.1:11451";
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static WSClient client;

    public static void init() {
        try {
            client = new WSClient(new URI(server));
            client.connect();
        } catch (Exception e) {
            tryReconnect(client::reconnect);
            e.printStackTrace();
        }
    }

    public static void tryReconnect(Runnable reconnect) {
        System.out.println("Connection closed");
        try {
            Thread.sleep(30 * 1000);
            new Thread(reconnect).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
