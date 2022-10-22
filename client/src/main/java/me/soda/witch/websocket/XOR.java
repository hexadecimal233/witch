package me.soda.witch.websocket;

import java.nio.charset.StandardCharsets;

public class XOR {
    private final byte[] key;

    public XOR(String key) {
        this.key = key.getBytes(StandardCharsets.UTF_8);
    }

    public XOR(byte[] key) {
        this.key = key;
    }

    public byte[] encrypt(String data) {
        return xor(data.getBytes(StandardCharsets.UTF_8));
    }

    public String decrypt(byte[] data) {
        return new String(xor(data), StandardCharsets.UTF_8);
    }

    private byte[] xor(byte[] data) {
        int len = data.length;
        int lenKey = key.length;
        int i = 0;
        int j = 0;
        while (i < len) {
            if (j >= lenKey) {
                j = 0;
            }
            data[i] = (byte) (data[i] ^ key[j]);
            i++;
            j++;
        }
        return data;
    }
}
