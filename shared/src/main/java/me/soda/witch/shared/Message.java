package me.soda.witch.shared;

import java.io.*;

public class Message implements Serializable {
    public final String messageType;
    public final Object message;

    public Message(String messageType, Object object) {
        this.messageType = messageType;
        this.message = object;
    }

    public static byte[] serialize(Message message) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(message);
        byte[] bytes = b.toByteArray();
        b.close();
        o.close();
        return bytes;
    }

    public static Message deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream b = new ByteArrayInputStream(bytes);
        ObjectInputStream o = new ObjectInputStream(b);
        Message message = (Message) o.readObject();
        b.close();
        o.close();
        return message;
    }
}