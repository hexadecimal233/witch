package me.soda.witch.client.events;

import me.soda.witch.shared.Message;

public class MessageReceiveEvent {
    public static final MessageReceiveEvent INSTANCE = new MessageReceiveEvent();

    public Message message;

    public static MessageReceiveEvent get(Message message) {
        INSTANCE.message = message;
        return INSTANCE;
    }
}
