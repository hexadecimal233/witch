package me.soda.witch.client.websocket;

import com.google.gson.Gson;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.Message;
import me.soda.witch.shared.XOR;

import static me.soda.witch.client.Witch.client;

public class MessageUtils extends Info {
    private static final Gson GSON = new Gson();

    public MessageUtils(XOR defaultXOR) {
        super(defaultXOR);
    }

    public void send(String messageType, Object... object) {
        String json = GSON.toJson(object);
        client.send(encrypt(new Message(messageType, json)));
    }
}
