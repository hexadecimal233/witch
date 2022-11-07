package me.soda.witch.shared;

public class HandleMessage {
    public static void handle(Message message, Success success) {
        String msgType = message.messageType;
        Object[] msg = message.message;
        success.handle(msgType, msg);
    }

    public interface Success {
        void handle(String msgType, Object[] msg);
    }
}
