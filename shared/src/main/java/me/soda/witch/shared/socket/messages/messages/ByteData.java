package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

import java.util.Base64;

public class ByteData implements Data {
    public String id;
    public String data;

    public ByteData(String id, byte[] data) {
        this.id = id;
        this.data = new String(Base64.getEncoder().encode(data));
    }

    public byte[] bytes() {
        return Base64.getDecoder().decode(data);
    }
}
