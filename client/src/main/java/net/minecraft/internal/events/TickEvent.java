package net.minecraft.internal.events;

public class TickEvent {
    public static final TickEvent INSTANCE = new TickEvent();

    public static TickEvent get() {
        return INSTANCE;
    }
}
