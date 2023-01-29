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

    public static Message decrypt(byte[] bytes) {
        return fromJson(new String(Crypto.INSTANCE.xor(bytes)));
    }

    public byte[] encrypt() {
        return Crypto.INSTANCE.xor(toString().getBytes());
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }
}