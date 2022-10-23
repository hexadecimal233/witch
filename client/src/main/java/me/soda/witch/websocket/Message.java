package me.soda.witch.websocket;

import com.google.gson.Gson;
import me.soda.witch.Witch;

public record Message(String messageType, String message) {
    private static final Gson GSON = new Gson();
    public static XOR defaultXOR;
    private static XOR xor = null;

    public static void send(String messageType, Object... object) {
        String json = GSON.toJson(object);
        new Message(messageType, json).send();
    }

    public static void setKey(String key) {
        xor = new XOR(key);
    }

    public static String decrypt(byte[] bytes) {
        if (xor != null)
            return xor.decrypt(bytes);
        else
            return defaultXOR.decrypt(bytes);
    }

    void send() {
        if (xor != null)
            Witch.client.send(xor.encrypt(GSON.toJson(this)));
        else
            Witch.client.send(defaultXOR.encrypt(GSON.toJson(this)));
    }
}
