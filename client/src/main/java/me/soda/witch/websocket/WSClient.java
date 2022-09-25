package me.soda.witch.websocket;

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
        System.out.println("Connection closed");
        try {
            Thread.sleep(30 * 1000);
            new Thread(this::reconnect).start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
    }

}