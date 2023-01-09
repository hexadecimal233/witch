package me.soda.witch.client.events;

public class TickEvent {
    public static final TickEvent INSTANCE = new TickEvent();

    public static TickEvent get() {
        return INSTANCE;
    }
}
