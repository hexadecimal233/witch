package me.soda.witch.shared.socket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Message {
    private static final Gson GSON = new Gson();
    public String messageID;
    public Object data;

    public Message(String messageID, Object object) {
        this.messageID = messageID;
        this.data = object;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageType='" + messageID + '\'' +
                ", data=" + data +
                '}';
    }

    public String serialize() {
        return GSON.toJson(this);
    }

    public static Message deserialize(String string) {
        JsonObject json = GSON.fromJson(string, JsonObject.class);
        JsonElement data = json.get("data");
        Message msg = GSON.fromJson(json, Message.class);
        switch (msg.messageID) {
            case "disconnect" -> msg.data = GSON.fromJson(data, DisconnectInfo.class);
            case "player" -> msg.data = GSON.fromJson(data, PlayerInfo.class);
        }
        return msg;
    }
}