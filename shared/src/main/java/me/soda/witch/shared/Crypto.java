package me.soda.witch.shared;

public class Crypto {
    public static Crypto INSTANCE;

    private final byte[] key;

    public Crypto(byte[] key) {
        this.key = key;
    }

    public byte[] xor(byte[] data) {
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
