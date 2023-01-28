package me.soda.witch.server.gui;

import me.soda.witch.server.server.Server;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class AdminPanel extends JPanel {
    public final DefaultTableModel connTableModel;
    public final JTextArea console;
    private final List<Integer> selectedConns = new ArrayList<>();
    private final Server server;
    private final JPopupMenu menu = new JPopupMenu() {{
        JMenuItem disconnect = new JMenuItem("Disconnect");
        disconnect.addActionListener(e -> getConns().forEach(connection -> connection.close(DisconnectData.Reason.NOREC)));

        JMenuItem reconnect = new JMenuItem("Reconnect");
        reconnect.addActionListener(e -> getConns().forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT)));

        add(disconnect);
        add(reconnect);
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

        JTable table = new JTable(connTableModel);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    selectedConns.clear();
                    int[] i = table.getSelectedRows();
                    for (int i1 : i) {
                        selectedConns.add((Integer) table.getValueAt(i1, 0));
                    }
                    menu.show(table, e.getX(), e.getY());
                }
            }
        });

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
