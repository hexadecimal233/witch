package me.soda.witch.websocket;

import java.nio.charset.StandardCharsets;

public class XOR {
    private static final byte[] key = "谁才是反派".getBytes(StandardCharsets.UTF_8);

    public static byte[] encrypt(String data) {
        return xor(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String decrypt(byte[] data) {
        return new String(xor(data), StandardCharsets.UTF_8);
    }

    private static byte[] xor(byte[] data) {
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
