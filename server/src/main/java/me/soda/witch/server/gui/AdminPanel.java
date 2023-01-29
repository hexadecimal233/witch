package me.soda.witch.server.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminPanel extends JPanel {
    public final JTextArea console;
    public final JTable table;

    public AdminPanel() {
        setLayout(new MigLayout("insets 5"));

        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"ID", "IP", "Player"}) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });

        JScrollPane scrollTbl = new JScrollPane(table);

        add(scrollTbl, "dock center, wrap");

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
        add(new JButton("Clear") {{
            addActionListener(e -> console.setText("Console cleared\n"));
        }}, "wrap");
        add(scrollTxt, "dock south");
    }
}
