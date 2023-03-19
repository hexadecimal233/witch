package me.soda.witch.shared;

public record Crypto(byte[] key) {
    public static Crypto INSTANCE;

    public byte[] encrypt(byte[] data) {
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

    public byte[] decrypt(byte[] data) {
        return encrypt(data);
    }
}

