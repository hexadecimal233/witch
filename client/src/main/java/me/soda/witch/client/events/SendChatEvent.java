package me.soda.witch.client.events;

import me.soda.witch.shared.events.Cancellable;

public class SendChatEvent extends Cancellable {
    public static class Message extends SendChatEvent {
        public static final Message INSTANCE = new Message();

        public String message;

        public static Message get(String message) {
            INSTANCE.message = message;
            return INSTANCE;
        }
    }

    public static class Command extends SendChatEvent {
        public static final Command INSTANCE = new Command();

        public String command;

        public static Command get(String message) {
            INSTANCE.command = message;
            return INSTANCE;
        }
    }
}
