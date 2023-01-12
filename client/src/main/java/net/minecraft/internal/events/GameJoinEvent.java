package net.minecraft.internal.events;

public class GameJoinEvent {
    public static final GameJoinEvent INSTANCE = new GameJoinEvent();

    public static GameJoinEvent get() {
        return INSTANCE;
    }
}
