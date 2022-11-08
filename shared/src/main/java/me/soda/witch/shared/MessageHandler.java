package me.soda.witch.shared;

public class MessageHandler {
    private final Info info;

    public MessageHandler(Info info) {
        this.info = info;
    }

    public void handle(Message message, Callback success) {
        String msgType = message.messageType;
        Object msg = message.message;
        success.handle(msgType, msg);
    }

    public void handle(byte[] bytes, Callback success) {
        try {
            handle(info.decrypt(bytes), success);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface Callback {
        void handle(String msgType, Object msg);
    }
}
