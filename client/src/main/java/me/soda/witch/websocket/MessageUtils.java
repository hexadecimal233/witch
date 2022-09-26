package me.soda.witch.websocket;

import me.soda.witch.Witch;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

public class MessageUtils {
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
        Witch.client.send(messageType + " " + base64);
    }
}

