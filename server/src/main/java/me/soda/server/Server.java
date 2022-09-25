package me.soda.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;

public class Server extends WebSocketServer {

    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Server");

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        LOGGER.info("客户端已连接: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LOGGER.info("客户端已断开: " + conn.getRemoteSocketAddress());
    }
    private static final File FILE = new File("screenshot.png");
    @Override
    public void onMessage(WebSocket conn, String message) {
        String[] msgArr = message.split(" ");
        if (msgArr[0].equals("screenshot")) {
            try {
                FILE.createNewFile();
                FileOutputStream file = new FileOutputStream(FILE);
                file.write(Base64.getDecoder().decode(msgArr[1]));
                file.close();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            LOGGER.info("收到消息: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {
    }
}
