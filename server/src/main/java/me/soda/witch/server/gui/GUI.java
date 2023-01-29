package me.soda.witch.server.gui;

import me.soda.witch.shared.ProgramUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GUI extends JFrame {
    public GUI(Container panel) {
        JDialog generateWindow = GenerateWindow.dialog(this);

        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Server");

        JMenuItem build = new JMenuItem("Build");
        build.addActionListener(e -> generateWindow.setVisible(true));

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showConfirmDialog(this, new JLabel() {
            {
                String url = "https://github.com/ThebestkillerTBK/witch";
                setText("<html>Witch by Soda5601\n<a href=\"" + url + "\">Github</a></html>");
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ProgramUtil.openURL(url);
                    }
                });
            }
        }, "About Witch", JOptionPane.DEFAULT_OPTION));

        themeMenu.add(build);
        themeMenu.add(about);

        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
        setContentPane(panel);
        setTitle("Witch server control");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }
}
