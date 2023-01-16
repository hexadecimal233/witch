package me.soda.witch.client.events;

public class ChatScreenChatEvent extends Cancellable {
    public static final ChatScreenChatEvent INSTANCE = new ChatScreenChatEvent();

    public String message;

    public static ChatScreenChatEvent get(String message) {
        INSTANCE.message = message;
        return INSTANCE;
    }
}
