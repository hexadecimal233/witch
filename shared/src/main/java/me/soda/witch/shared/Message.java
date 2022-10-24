package me.soda.witch.shared;

import com.google.gson.Gson;

public class Message {
    private static final Gson GSON = new Gson();
    public String messageType;
    public String message;

    public Message(String messageType, String message) {
        this.messageType = messageType;
        this.message = message;
    }

    public Message(String messageType, Object... object) {
        new Message(messageType, GSON.toJson(object));
    }
}