package me.soda.witch.server.gui;

import me.soda.witch.server.utils.Utils;
import me.soda.witch.shared.ProgramUtil;
import net.miginfocom.swing.MigLayout;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class GUI extends JFrame {
    public GUI(Container panel) {
        JDialog generateWindow = Utils.dialog(this, "Generate client jars", new GenerateWindow());
        JDialog obfuscateWindow = Utils.dialog(this, "Obfuscator", new ObfuscateWindow());

        JMenuBar menuBar = new JMenuBar();
        JMenu themeMenu = new JMenu("Server");

        JMenuItem build = new JMenuItem("Build");
        build.addActionListener(e -> generateWindow.setVisible(true));

        JMenuItem obf = new JMenuItem("Obfuscator");
        obf.addActionListener(e -> obfuscateWindow.setVisible(true));

        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showConfirmDialog(this, new JPanel() {{
            setLayout(new MigLayout());
            try {
                ImageIcon icon = new ImageIcon(ImageIO.read(GUI.class.getClassLoader().getResourceAsStream("icon.png")));
                Image scaleImage = icon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                icon.setImage(scaleImage);
                JLabel img = new JLabel(icon);
                add(img, "center, wrap");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            add(new JLabel() {
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
            });
        }}, "About Witch", JOptionPane.DEFAULT_OPTION));

        themeMenu.add(build);
        themeMenu.add(obf);
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
