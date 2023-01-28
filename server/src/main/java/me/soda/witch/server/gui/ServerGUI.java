package me.soda.witch.server.gui;

import me.soda.witch.server.data.ConnectionInfo;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import me.soda.witch.shared.socket.messages.messages.PlayerData;
import me.soda.witch.shared.socket.messages.messages.StringsData;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ServerGUI extends Server {
    private final GUI gui;

    public ServerGUI() throws IOException {
        super();
        gui = new GUI();

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
    }

    @Override
    public void onOpen(Connection conn) {
        super.onOpen(conn);
        ConnectionInfo info = clientMap.get(conn);
        gui.adminPanel.connTableModel.addRow(new Object[]{info.id, "", ""});
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        ConnectionInfo info = clientMap.get(conn);
        changeRow(info, true);
        super.onClose(conn, disconnectData);
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        super.onMessage(conn, message);
        ConnectionInfo info = clientMap.get(conn);
        try {
            if (message.data instanceof StringsData data) {
                if (data.data().length == 1) {
                    if (data.id().equals("ip")) {
                        changeRow(info, false);
                    }
                }
            } else if (message.data instanceof PlayerData) {
                changeRow(info, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeRow(ConnectionInfo info, boolean delete) {
        DefaultTableModel connTableModel = gui.adminPanel.connTableModel;
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
