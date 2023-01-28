package me.soda.witch.server.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JPanel {
    public final DefaultTableModel connTableModel;
    public final JTextArea console;

    public AdminPanel() {
        setLayout(new MigLayout());

        String[] columnNames = new String[]{"ID", "IP", "Player"};
        connTableModel = new DefaultTableModel(new Object[][]{}, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JScrollPane scrollTbl = new JScrollPane(new JTable(connTableModel));

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
}
