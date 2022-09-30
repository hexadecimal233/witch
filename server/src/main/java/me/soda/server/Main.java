package me.soda.server;


import org.java_websocket.WebSocket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import static me.soda.server.Server.LOGGER;

public class Main {

    static Collection<WebSocket> connCollection;

    public static void main(String[] args) throws IOException {
        Server server = new Server(11451);
        server.start();
        LOGGER.info("端口: " + server.getPort());
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
                            connCollection = null;
                            System.out.println("----CONNECTIONS----");
                            if (msgArr.length == 1) {
                                server.getConnections().forEach(conn -> {
                                    int index = conn.<Integer>getAttachment();
                                    String addr = conn.getRemoteSocketAddress().getAddress().getHostAddress();
                                    System.out.println(String.format("IP: %s, ID: %s", addr, index));
                                });
                            } else if (msgArr.length == 2) {
                                if (msgArr[1].equals("sel")) {
                                    if (msgArr.length == 3) {
                                        server.getConnections().stream().filter(conn -> conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2])).forEach(conn -> connCollection.add(conn));
                                    }
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