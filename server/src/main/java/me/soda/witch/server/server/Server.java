package me.soda.witch.server.server;

import me.soda.witch.server.handlers.MessageHandler;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.XOR;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static int clientIndex = 0;
    public final String name;
    public final XOR defaultXOR;
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    public final SendUtil sendUtil = new SendUtil();
    public boolean stopped = false;

    public Server(int port, String key, String name) throws Exception {
        super(port);
        this.defaultXOR = new XOR(key);
        this.name = name;
    }

    public void log(String string) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + string);
    }

    @Override
    public void onOpen(Connection conn) {
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + clientIndex);
        clientMap.put(conn, new Info(clientIndex, defaultXOR));
        clientIndex++;
    }

    @Override
    public void onClose(Connection conn) {
        log("Client disconnected: ID: " + clientMap.get(conn).index);
        try {
            clientMap.remove(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Connection conn, byte[] bytes) {
        MessageHandler.handle(bytes, conn, this);
    }
}
