package me.soda.witch.shared.socket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.socket.messages.messages.*;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public static final Map<Integer, Class<? extends Data>> MESSAGE_ID_MAP = new HashMap<>() {{
        put(0, ErrorData.class);
        put(1, DisconnectData.class);
        put(2, PlayerData.class);
        put(3, ConfigData.class);
        put(4, SpamData.class);
        put(5, ByteData.class);
        put(6, StringData.class);
        put(7, OKData.class);
        put(8, SingleStringData.class);
        put(9, BooleanData.class);
    }};
    private static final Gson GSON = new Gson();
    private final int messageID;
    public Object data;

    public Message(Data object) {
        for (int id : MESSAGE_ID_MAP.keySet()) {
            Class<? extends Data> clazz = MESSAGE_ID_MAP.get(id);
            if (object.getClass() == clazz) {
                messageID = id;
                data = object;
                return;
            }
        }
        throw new UnsupportedOperationException("Unknown Message");
    }

    public static Message fromJson(int messageID, String json) {
        for (int id : MESSAGE_ID_MAP.keySet()) {
            if (id == messageID) {
                return new Message(GSON.fromJson(json, MESSAGE_ID_MAP.get(id)));
            }
        }
        throw new UnsupportedOperationException("Unknown Message");
    }

    public static Message fromString(String messageID, String data) {
        return new Message(new StringData(messageID, data));
    }

    public static Message fromString(String messageID) {
        return new Message(new SingleStringData(messageID));
    }

    public static Message fromBytes(String messageID, byte[] data) {
        return new Message(new ByteData(messageID, data));
    }

    public static Message fromBoolean(String messageID, boolean data) {
        return new Message(new BooleanData(messageID, data));
    }

    public static Message deserialize(byte[] bytes) {
        String string = new String(Crypto.xor(bytes, Cfg.key));
        JsonObject json = GSON.fromJson(string, JsonObject.class);
        Message msg = GSON.fromJson(json, Message.class);
        for (int id : MESSAGE_ID_MAP.keySet()) {
            if (id == msg.messageID) {
                msg.data = GSON.fromJson(json.getAsJsonObject("data"), MESSAGE_ID_MAP.get(id));
                break;
            }
        }
        return msg;
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public byte[] serialize() {
        return Crypto.xor(toString().getBytes(), Cfg.key);
    }
}