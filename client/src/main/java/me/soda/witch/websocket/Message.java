package me.soda.witch.websocket;

import com.google.gson.Gson;
import me.soda.witch.Witch;

public record Message(String messageType, String message) {
    public static final XOR xor = new XOR("鸡你太美");
    private static final Gson GSON = new Gson();

    public static void send(String messageType, Object... object) {
        String json = GSON.toJson(object);
        new Message(messageType, json).send();
    }

    void send() {
        Witch.client.send(xor.encrypt(GSON.toJson(this)));
    }
}
