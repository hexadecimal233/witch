package me.soda.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Server extends WebSocketServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static int clientIndex = 0;

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    public static void log(String string) {
        LOGGER.info("{}: {}", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")), string);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(clientIndex);
        clientIndex++;
        int cIndex = conn.<Integer>getAttachment();
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + cIndex);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        int cIndex = conn.<Integer>getAttachment();
        log("Client disconnected: ID: " + cIndex);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        int cIndex = conn.<Integer>getAttachment();
        String[] msgArr = message.split(" ");
        if (msgArr.length == 0) return;
        log("* Received message: " + msgArr[0] + " From ID " + cIndex);
        MessageHandler.handle(msgArr);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }
}
