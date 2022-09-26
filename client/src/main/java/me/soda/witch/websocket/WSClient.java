package me.soda.witch.websocket;

import me.soda.witch.Witch;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WSClient extends WebSocketClient {

    public WSClient c;

    public WSClient(URI serverURI) {
        super(serverURI);
    }

    @Override
    public void onOpen(ServerHandshake handshakeData) {
        System.out.println("Connection initialized");
    }

    @Override
    public void onMessage(String message) {
        MessageHandler.handle(message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Witch.tryReconnect(this::reconnect);
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}