package me.soda.witch.shared.socket.messages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.messages.*;

import java.util.HashMap;
import java.util.Map;

public class Message {
    public static final Map<String, Class<?>> MESSAGE_ID_MAP = new HashMap<>() {{
        put("disconnect", DisconnectInfo.class);
        put("player", PlayerInfo.class);
        put("config", ConfigInfo.class);
        put("spam", SpamInfo.class);
        put("byte", ByteInfo.class);
    }};
    public static final Message ERROR_MESSAGE = new Message("error", null);
    private static final Gson GSON = new Gson();
    public String messageID;
    public Object data;

    public Message(String messageID, Object object) {
        this.messageID = messageID;
        this.data = object;
        if (object instanceof byte[] b) {
            this.data = new ByteInfo(messageID, b);
            this.messageID = "byte";
        } else if (object instanceof String str) {
            try {
                for (String id : MESSAGE_ID_MAP.keySet()) {
                    if (id.equals(messageID)) {
                        data = GSON.fromJson(str, MESSAGE_ID_MAP.get(id));
                        break;
                    }
                }
            } catch (Exception e) {
                this.messageID = "error";
                this.data = "error";
            }
        }
    }

    public static Message deserialize(byte[] bytes) {
        try {
            String string = new String(Crypto.xor(bytes, Cfg.key));
            JsonObject json = GSON.fromJson(string, JsonObject.class);
            JsonElement data = json.get("data");
            Message msg = GSON.fromJson(json, Message.class);
            for (String id : MESSAGE_ID_MAP.keySet()) {
                if (id.equals(msg.messageID)) {
                    msg.data = GSON.fromJson(data, MESSAGE_ID_MAP.get(id));
                    break;
                }
            }
            if (msg.data instanceof ByteInfo info) {
                msg.messageID = info.messageID;
                msg.data = info.bytes();
            }
            return msg;
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
            return ERROR_MESSAGE;
        }
    }

    @Override
    public String toString() {
        return GSON.toJson(this);
    }

    public byte[] serialize() {
        return Crypto.xor(toString().getBytes(), Cfg.key);
    }
}