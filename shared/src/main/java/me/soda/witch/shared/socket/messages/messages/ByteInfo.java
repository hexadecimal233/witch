package me.soda.witch.shared.socket.messages.messages;

import java.util.Base64;

public class ByteInfo {
    public String messageID;
    public String data;

    public ByteInfo(String messageID, byte[] data) {
        this.messageID = messageID;
        this.data = new String(Base64.getEncoder().encode(data));
    }

    public byte[] bytes() {
        return Base64.getDecoder().decode(data);
    }
}
