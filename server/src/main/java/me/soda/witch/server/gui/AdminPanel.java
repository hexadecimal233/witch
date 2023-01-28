package me.soda.witch.server.gui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout(new MigLayout());

        JTextArea console = new JTextArea(20, 40);
        console.setEditable(false);

        add(console);
    }
}
