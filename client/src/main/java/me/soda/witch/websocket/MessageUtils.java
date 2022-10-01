package me.soda.witch.websocket;

import me.soda.witch.Witch;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MessageUtils {
    public static boolean encrypt = true;

    public static void sendMessage(String messageType, String string) {
        try {
            sendMessage(messageType, string.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage(String messageType, String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = Base64.getEncoder().encodeToString(strings[i].getBytes(StandardCharsets.UTF_8));
        }
        sendMessage(messageType, "str " + String.join(" ", strings));
    }

    public static void sendMessage(String messageType, byte[] bytes) {
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String text = messageType + " " + base64;
        if (encrypt) Witch.client.send(XOR.encrypt(text));
        else Witch.client.send(text);
    }
}

