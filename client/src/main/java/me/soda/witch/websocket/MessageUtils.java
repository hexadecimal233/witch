package me.soda.witch.websocket;

import me.soda.witch.Witch;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MessageUtils {
    public static boolean encrypt = true;
    public static XOR xor = new XOR("am0gus谁是内鬼");

    public static void sendMessage(String messageType, String string) {
        try {
            sendMessage(messageType, string.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            Witch.printStackTrace(e);
        }
    }

    public static void sendMessage(String messageType, byte[] bytes) {
        if (bytes == null) throw new UnsupportedOperationException("message is null");
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String text = messageType + " " + base64;
        if (encrypt) Witch.client.send(xor.encrypt(text));
        else Witch.client.send(text);
    }
}

