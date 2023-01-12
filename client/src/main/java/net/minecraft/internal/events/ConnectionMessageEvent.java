package net.minecraft.internal.events;

import me.soda.witch.shared.socket.messages.Message;

public class ConnectionMessageEvent {
    public static final ConnectionMessageEvent INSTANCE = new ConnectionMessageEvent();

    public Message message;

    public static ConnectionMessageEvent get(Message message) {
        INSTANCE.message = message;
        return INSTANCE;
    }
}
