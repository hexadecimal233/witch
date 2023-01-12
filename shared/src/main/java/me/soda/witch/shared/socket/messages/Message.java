package me.soda.witch.shared.socket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Base64;

public class Message {
    private static final Gson GSON = new Gson();
    public String messageID;
    public boolean byteData;
    public Object data;

    public Message(String messageID, Object object) {
        this.messageID = messageID;
        this.data = object;
        byteData = object instanceof byte[];
    }

    public static Message deserialize(String string) {
        JsonObject json = GSON.fromJson(string, JsonObject.class);
        JsonElement data = json.get("data");
        Message msg = GSON.fromJson(json, Message.class);
        switch (msg.messageID) {
            case "disconnect" -> msg.data = GSON.fromJson(data, DisconnectInfo.class);
            case "player" -> msg.data = GSON.fromJson(data, PlayerInfo.class);
            case "config" -> msg.data = GSON.fromJson(data, Variables.class);
        }
        if (msg.byteData && msg.data instanceof String str) msg.data = Base64.getDecoder().decode(str);
        return msg;
    }

    @Override
    public String toString() {
        return "Message" + serialize();
    }

    public String serialize() {
        if (byteData) this.data = new String(Base64.getEncoder().encode((byte[]) data));
        return GSON.toJson(this);
    }
}