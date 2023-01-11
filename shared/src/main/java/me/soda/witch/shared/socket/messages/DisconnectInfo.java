package me.soda.witch.shared.socket.messages;

public record DisconnectInfo(Reason reason, String message) {
    public enum Reason {
        RECONNECT,
        NO_RECONNECT,
        NORMAL,
        EXCEPTION
    }
}
