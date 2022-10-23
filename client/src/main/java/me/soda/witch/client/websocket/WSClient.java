package me.soda.witch.client.websocket;

import me.soda.witch.client.Witch;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;

public class WSClient extends WebSocketClient {
    public int reconnections = 0;
    private boolean reconnect = true;

    public WSClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        reconnections = 0;
        Witch.println("Connection initialized");
        Witch.messageUtils.send("xor");
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
        if (reconnect || !tooMany) {
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

    public void close(boolean reconnect) {
        this.reconnect = reconnect;
        this.close();
    }
}