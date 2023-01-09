package me.soda.witch.shared.socket;

import java.io.Serial;
import java.io.Serializable;

public class DisconnectInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6572141426520703801L;

    public final Reason reason;
    public final String message;

    public DisconnectInfo(Reason reason, String message) {
        this.reason = reason;
        this.message = message;
    }

    public enum Reason {
        RECONNECT,
        NO_RECONNECT,
        NORMAL,
        EXCEPTION
    }
}
