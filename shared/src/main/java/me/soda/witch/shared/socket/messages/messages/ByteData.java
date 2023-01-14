package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

import java.util.Base64;

public class ByteData implements Data {
    public String messageID;
    public String data;

    public ByteData(String messageID, byte[] data) {
        this.messageID = messageID;
        this.data = new String(Base64.getEncoder().encode(data));
    }

    public byte[] bytes() {
        return Base64.getDecoder().decode(data);
    }
}
