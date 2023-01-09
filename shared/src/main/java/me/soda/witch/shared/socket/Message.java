package me.soda.witch.shared.socket;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    @Serial
    private static final long serialVersionUID = 6572141426520703800L;

    public final String messageType;
    public final Object data;

    public Message(String messageType, Object object) {
        this.messageType = messageType;
        this.data = object;
    }
}