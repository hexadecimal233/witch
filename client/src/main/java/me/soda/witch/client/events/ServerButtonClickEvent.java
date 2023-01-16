package me.soda.witch.client.events;

public class ServerButtonClickEvent extends Cancellable {
    public static final ServerButtonClickEvent INSTANCE = new ServerButtonClickEvent();

    public static ServerButtonClickEvent get() {
        return INSTANCE;
    }
}
