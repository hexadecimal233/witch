package me.soda.witch.shared;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Random;

public class Info {
    private static final Gson GSON = new Gson();
    public boolean acceptXOR = false;
    public JsonObject playerData;
    public JsonObject ip;
    public String key;
    private final XOR defaultXOR;
    private XOR xor;

    public Info(XOR defaultXOR) {
        this.defaultXOR = defaultXOR;
    }

    private String getPassword() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 37; i++) {
            int number = random.nextInt(63);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public String randomXOR() {
        key = getPassword();
        xor = new XOR(key);
        return key;
    }

    public void setXOR(String key) {
        xor = new XOR(key);
    }

    public Message decrypt(byte[] bytes) {
        String message = acceptXOR ? xor.decrypt(bytes) : defaultXOR.decrypt(bytes);
        return GSON.fromJson(message, Message.class);
    }

    public byte[] encrypt(Message message) {
        String msg = GSON.toJson(message);
        return acceptXOR ? xor.encrypt(msg) : defaultXOR.encrypt(msg);
    }
}
