package me.soda.witch.shared.socket.packet;

import java.io.Serializable;

public class DisconnectPacket implements Serializable {
    public final Reason reason;
    public final String message;

    public DisconnectPacket(Reason reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public enum Reason {
        RECONNECT,
        NO_RECONNECT,
        NORMAL,
    }
}
