package me.soda.witch.server;

import com.google.gson.JsonObject;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends WebSocketServer {
    private final Logger LOGGER = LoggerFactory.getLogger("Server");
    public ConcurrentHashMap<WebSocket, JsonObject> clientMap = new ConcurrentHashMap<>();
    public static XOR defaultXOR;
    public static XOR xor;
    private static int clientIndex = 0;

    private String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        Random random=new Random();
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<length;i++){
            int number=random.nextInt(63);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public Server(int port, String key) {
        super(new InetSocketAddress(port));
        defaultXOR = new XOR(key);
        xor = new XOR(getRandomString(16));
    }

    public void log(String string) {
        LOGGER.info("{}: {}", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), string);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(clientIndex);
        clientIndex++;
        int cIndex = conn.<Integer>getAttachment();
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + cIndex);
        clientMap.put(conn, new JsonObject());
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
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer bytes) {
        int cIndex = conn.<Integer>getAttachment();
        String message = defaultXOR.decrypt(bytes.array());
        log("* Received message: " + message + " From ID " + cIndex);
        //String[] msgArr = message.split(" ");
        //if (msgArr.length == 0) return;
        //log("* Received message: " + msgArr[0] + " From ID " + cIndex);
        //MessageHandler.handle(msgArr, conn, this);
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }
}
