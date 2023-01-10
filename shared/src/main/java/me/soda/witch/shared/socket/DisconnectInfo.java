package me.soda.witch.shared.socket;

import java.io.Serial;
import java.io.Serializable;

public record DisconnectInfo(Reason reason, String message) implements Serializable {
    @Serial
    private static final long serialVersionUID = 6572141426520703801L;

    public enum Reason {
        RECONNECT,
        NO_RECONNECT,
        NORMAL,
        EXCEPTION
    }
}
