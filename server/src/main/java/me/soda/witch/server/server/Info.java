package me.soda.witch.server.server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Random;

public class Info {
    private static final Gson GSON = new Gson();
    public boolean acceptXOR = false;
    public JsonObject playerData;
    public XOR xor = new XOR(getRandomString(16));

    private String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(63);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public Message decrypt(byte[] bytes, Server server) {
        String message = acceptXOR ? xor.decrypt(bytes) : server.defaultXOR.decrypt(bytes);
        return GSON.fromJson(message, Message.class);
    }

    public byte[] encrypt(Message message, Server server) {
        String msg = GSON.toJson(message);
        return acceptXOR ? xor.encrypt(msg) : server.defaultXOR.encrypt(msg);
    }
}
