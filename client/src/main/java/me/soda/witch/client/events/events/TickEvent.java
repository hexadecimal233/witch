package me.soda.witch.client.events.events;

import me.soda.witch.shared.AbstractEvent;

public class TickEvent extends AbstractEvent<TickEvent.Callback> {
    public static final TickEvent INSTANCE = new TickEvent();

    @Override
    public void run(int id, Callback callback, Object... args) {
        callback.run(id);
    }

    public interface Callback {
        void run(int id);
    }
}
