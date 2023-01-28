package me.soda.witch.server.gui;

import me.soda.witch.server.server.Server;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GUI extends JFrame {
    public Server server;

    public GUI() {
        JDialog generateWindow = Generate.dialog(this);

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
                        openURL(url);
                    }
                });
            }
        }, "About Witch", JOptionPane.DEFAULT_OPTION));

        themeMenu.add(build);
        themeMenu.add(about);

        menuBar.add(themeMenu);
        setJMenuBar(menuBar);
        setContentPane(new AdminPanel());
        setTitle("Witch server control");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);

        try {
            server = new Server();
        } catch (IOException e) {
            JOptionPane.showConfirmDialog(this, e.getMessage(), "Error:Failed to start server", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    server.stop();
                } catch (IOException ex) {
                    JOptionPane.showConfirmDialog(GUI.this, ex.getMessage(), "Failed to stop server", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            }
        });
    }

    public static void openURL(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        Runtime rt = Runtime.getRuntime();

        try {
            if (os.contains("linux") || os.contains("unix")) {
                rt.exec(new String[]{"xdg-open", url});
            } else if (os.contains("mac")) {
                rt.exec(new String[]{"open", url});
            } else if (os.contains("win")) {
                rt.exec(new String[]{"rundll32", "url.dll,FileProtocolHandler", url});
            }
        } catch (IOException ignored) {
        }
    }
}
