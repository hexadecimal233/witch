package me.soda.witch.client.events.events;

import me.soda.witch.shared.AbstractEvent;
import me.soda.witch.shared.Message;

public class MessageReceiveEvent extends AbstractEvent<MessageReceiveEvent.Callback> {
    public static final MessageReceiveEvent INSTANCE = new MessageReceiveEvent();

    @Override
    public void run(int id, Callback callback, Object... args) {
        callback.run(id, (Message) args[0]);
    }

    public interface Callback {
        void run(int id, Message message);
    }
}
