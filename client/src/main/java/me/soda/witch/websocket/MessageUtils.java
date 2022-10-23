package me.soda.witch.websocket;

import com.google.gson.Gson;

import static me.soda.witch.Witch.client;

public class MessageUtils {
    private static final Gson GSON = new Gson();
    public XOR defaultXOR;
    public XOR xor;

    public void send(String messageType, Object... object) {
        String json = GSON.toJson(object);
        client.send(encrypt(new Message(messageType, json)));
    }

    public Message decrypt(byte[] bytes) {
        String message = xor != null ? xor.decrypt(bytes) : defaultXOR.decrypt(bytes);
        return GSON.fromJson(message, Message.class);
    }

    public byte[] encrypt(Message message) {
        String msg = GSON.toJson(message);
        return xor != null ? xor.encrypt(msg) : defaultXOR.encrypt(msg);
    }
}
