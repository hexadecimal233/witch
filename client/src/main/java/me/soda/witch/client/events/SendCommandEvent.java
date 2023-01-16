package me.soda.witch.client.events;

public class SendCommandEvent extends Cancellable {
    public static final SendCommandEvent INSTANCE = new SendCommandEvent();

    public String command;

    public static SendCommandEvent get(String message) {
        INSTANCE.command = message;
        return INSTANCE;
    }
}
