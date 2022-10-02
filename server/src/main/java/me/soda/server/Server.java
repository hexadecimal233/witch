package me.soda.server;

import me.soda.server.handlers.MessageHandler;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends WebSocketServer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Server");
    public static ConcurrentHashMap<WebSocket, Client> clientMap = new ConcurrentHashMap<>();
    private static int clientIndex = 0;

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    public static void log(String string) {
        LOGGER.info("{}: {}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), string);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(clientIndex);
        clientIndex++;
        int cIndex = conn.<Integer>getAttachment();
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + cIndex);
        clientMap.put(conn, new Client());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        int cIndex = conn.<Integer>getAttachment();
        log("Client disconnected: ID: " + cIndex);
        try {
            clientMap.remove(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        log("Unsafe message");
        int cIndex = conn.<Integer>getAttachment();
        String[] msgArr = message.split(" ");
        if (msgArr.length == 0) return;
        log("* Received message: " + msgArr[0] + " From ID " + cIndex);
        MessageHandler.handle(msgArr, conn);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        int cIndex = conn.<Integer>getAttachment();
        String message = XOR.decrypt(bytes.array());
        String[] msgArr = message.split(" ");
        if (msgArr.length == 0) return;
        log("* Received message: " + msgArr[0] + " From ID " + cIndex);
        MessageHandler.handle(msgArr, conn);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }
}
