package me.soda.witch.server.server;

import me.soda.witch.server.handlers.MessageHandler;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.XOR;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends WebSocketServer {
    private static int clientIndex = 0;
    public final String name;
    public final XOR defaultXOR;
    public ConcurrentHashMap<WebSocket, Info> clientMap = new ConcurrentHashMap<>();
    public SendUtil sendUtil = new SendUtil();
    public boolean stopped = false;

    public Server(int port, String key, String name) {
        super(new InetSocketAddress(port));
        this.defaultXOR = new XOR(key);
        this.name = name;
    }

    public void log(String string) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + string);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + clientIndex);
        clientMap.put(conn, new Info(clientIndex, defaultXOR));
        clientIndex++;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        log("Client disconnected: ID: " + clientMap.get(conn).index);
        try {
            clientMap.remove(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        MessageHandler.handleRaw(bytes.array(), conn, this);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }
}
