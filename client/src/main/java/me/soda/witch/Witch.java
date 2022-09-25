package me.soda.witch;

import me.soda.witch.websocket.WSClient;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.util.Base64;

public class Witch {
    public static final String server = "ws://127.0.0.1:11451";
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static WSClient client;

    public static void init() {
        try {
            client = new WSClient(new URI(server));
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String messageType, String string) {
        try {
            sendMessage(messageType, string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String messageType, String[] strings) {
        sendMessage(messageType, "[" + StringUtils.join(strings, ", ") + "]");
    }

    public static void sendMessage(String messageType, byte[] bytes) {
        String base64 = Base64.getEncoder().encodeToString(bytes);
        client.send(messageType + " " + base64);
    }
}
