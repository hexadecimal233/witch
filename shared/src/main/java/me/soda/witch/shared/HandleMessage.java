package me.soda.witch.shared;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public class HandleMessage {
    private static final Gson GSON = new Gson();
    public static void handle(Message message, HandleMessageCallback callback) {
        String msgType = message.messageType;
        JsonArray jsonArray = GSON.fromJson(message.message, JsonArray.class);
        String msg = jsonArray.size() > 0
                ? jsonArray.get(0).isJsonPrimitive()
                ? jsonArray.get(0).getAsJsonPrimitive().isString()
                ? jsonArray.get(0).getAsString()
                : jsonArray.get(0).toString()
                : jsonArray.get(0).toString()
                : "";
        callback.handle(msgType, msg);
    }

    public interface HandleMessageCallback {
        void handle(String msgType, String msg);
    }
}
