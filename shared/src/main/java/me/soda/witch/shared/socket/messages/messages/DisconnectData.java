package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record DisconnectData(Reason reason, String message) implements Data {
    public enum Reason {
        RECONNECT,
        NO_RECONNECT,
        NORMAL,
        EXCEPTION
    }
}
