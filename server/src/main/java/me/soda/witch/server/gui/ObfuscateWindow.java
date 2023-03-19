package me.soda.witch.server.gui;

import me.soda.witch.server.utils.Utils;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class ObfuscateWindow extends JPanel {
    public ObfuscateWindow() {
        setLayout(new MigLayout("insets 10"));

        JTextField inputFileText = new JTextField(50);
        JTextField pkg = new JTextField("net/minecraft/internal", 50);
        JTextField outputFileText = new JTextField(50);

        JButton inputSelectBtn = new JButton("📁");
        JButton outputSelectBtn = new JButton("📁");

        JButton generateBtn = new JButton("WIP");

        generateBtn.addActionListener(e -> {
            try {
                //Obfuscator.obfuscate(inputFileText.getText(), outputFileText.getText(), pkg.getText());
                JOptionPane.showMessageDialog(this, "Operation completed");
            } catch (Exception ex) {
                JOptionPane.showConfirmDialog(this, ex.getMessage(), "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        });

        inputSelectBtn.addActionListener(e -> inputFileText.setText(Utils.chooseFile(false, this)));
        outputSelectBtn.addActionListener(e -> outputFileText.setText(Utils.chooseFile(true, this)));

        add(new JLabel("Input file"));
        add(inputFileText);
        add(inputSelectBtn, "wrap, pushx");

        add(new JLabel("Output file"));
        add(outputFileText);
        add(outputSelectBtn, "wrap, pushx");

        add(new JLabel("Package"));
        add(pkg, "wrap, pushx");

        add(generateBtn, "gapleft 160, gapright 160, dock south");
    }
}
