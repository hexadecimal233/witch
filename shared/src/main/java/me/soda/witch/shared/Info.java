package me.soda.witch.shared;

import com.google.gson.JsonObject;

import java.util.Random;

public class Info {
    public final int index;
    private final XOR defaultXOR;
    public boolean acceptXOR = false;
    public JsonObject playerData;
    public JsonObject ip;
    public String key;
    private XOR xor;

    public Info(int index, XOR defaultXOR) {
        this.defaultXOR = defaultXOR;
        this.index = index;
    }

    private String getPassword() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++) {
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

    public Message decrypt(byte[] bytes) throws Exception {
        return Message.deserialize(acceptXOR ? xor.xor(bytes) : defaultXOR.xor(bytes));
    }

    public byte[] encrypt(Message message) throws Exception {
        byte[] bytes = Message.serialize(message);
        return acceptXOR ? xor.xor(bytes) : defaultXOR.xor(bytes);
    }
}
