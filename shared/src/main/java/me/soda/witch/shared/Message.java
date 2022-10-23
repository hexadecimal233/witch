package me.soda.witch.shared;

public class Message {
    public final String messageType;
    public final String message;

    public Message(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }
}