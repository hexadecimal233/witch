package me.soda.server;


import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static List<WebSocket> connCollection;

    public static void main(String[] args) throws IOException {
        Server server = new Server(11451);
        server.start();
        System.out.println("端口: " + server.getPort());
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
                                System.out.println("----CONNECTIONS----");
                                server.getConnections().forEach(conn -> {
                                    int index = conn.<Integer>getAttachment();
                                    String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                                    System.out.printf("IP: %s, ID: %s%n", address, index);
                                });
                            } else {
                                connCollection = new ArrayList<>();
                                switch (msgArr[1]) {
                                    case "sel":
                                        if (msgArr.length == 3) {
                                            server.getConnections().stream().filter(conn -> conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2])).forEach(conn -> connCollection.add(conn));
                                            System.out.println("Selected client!");
                                        }
                                    default:
                                        break;
                                }
                            }
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