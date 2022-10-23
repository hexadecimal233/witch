package me.soda.witch.server.server;

public class Message {
    public final String messageType;
    public final String message;

    public Message(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }
}
