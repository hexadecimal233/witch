package me.soda.witch.shared.socket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.socket.messages.messages.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {
    private static final Map<Integer, Class<? extends Data>> MESSAGE_ID_MAP = new HashMap<>() {{
        put(1, DisconnectData.class);
        put(2, PlayerData.class);
        put(3, ClientConfigData.class);
        put(4, SpamData.class);
        put(5, ByteData.class);
        put(7, OKData.class);
        put(10, StringsData.class);
        put(12, BooleanData.class);
        put(13, FollowData.class);
        put(14, MessageList.class);
        put(15, IntData.class);
    }};
    private static final Gson GSON = new Gson();
    public final Object data;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final int id;

    public Message(Data object) {
        for (int id : MESSAGE_ID_MAP.keySet()) {
            if (object.getClass() == MESSAGE_ID_MAP.get(id)) {
                this.id = id;
                data = object;
                return;
            }
        }
        throw new UnsupportedOperationException("Unknown Message");
    }

    public static void registerMessage(int id, Class<? extends Data> message) {
        if (MESSAGE_ID_MAP.containsKey(id)) throw new UnsupportedOperationException("Duplicate message");
        MESSAGE_ID_MAP.put(id, message);
    }

    public static Message fromJson(String string) {
        return fromJsonObj(GSON.fromJson(string, JsonObject.class));
    }

    public static Message fromList(String id, List<Message> string) {
        return new Message(new MessageList<>(id, string));
    }

    private static Message fromJsonObj(JsonObject json) {
        for (int id : MESSAGE_ID_MAP.keySet()) {
            if (id == json.get("id").getAsInt()) {
                if (MESSAGE_ID_MAP.get(id) == MessageList.class) {
                    List<Message> l = new ArrayList<>();
                    JsonObject data = json.getAsJsonObject("data");
                    data.getAsJsonArray("data").forEach(jsonElement -> l.add(fromJsonObj(jsonElement.getAsJsonObject())));
                    return new Message(new MessageList<>(data.get("id").getAsString(), l));
                } else {
                    return new Message(GSON.fromJson(json.getAsJsonObject("data"), MESSAGE_ID_MAP.get(id)));
                }
            }
        }
        throw new UnsupportedOperationException("Unknown Message");
    }

    public static Message fromString(String id, String data) {
        return new Message(new StringsData(id, new String[]{data}));
    }

    public static Message fromInt(String id, int data) {
        return new Message(new IntData(id, data));
    }

    public static Message fromString(String id) {
        return new Message(new StringsData(id, new String[]{}));
    }

    public static Message fromBytes(String id, byte[] data) {
        return new Message(new ByteData(id, data));
    }

    public static Message fromBoolean(String id, boolean data) {
        return new Message(new BooleanData(id, data));
    }

    public static Message decrypt(byte[] bytes) {
        return fromJson(new String(Crypto.INSTANCE.xor(bytes)));
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public byte[] encrypt() {
        return Crypto.INSTANCE.xor(toString().getBytes());
    }
}