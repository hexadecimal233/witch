package me.soda.witch.client.events;

import me.soda.witch.shared.events.Cancellable;

public class SendMessageEvent extends Cancellable {
    public static class Message extends SendMessageEvent {
        public static final Message INSTANCE = new Message();

        public String message;

        public static Message get(String message) {
            INSTANCE.message = message;
            return INSTANCE;
        }
    }

    public static class Command extends SendMessageEvent {
        public static final Command INSTANCE = new Command();

        public String command;

        public static Command get(String message) {
            INSTANCE.command = message;
            return INSTANCE;
        }
    }
}
