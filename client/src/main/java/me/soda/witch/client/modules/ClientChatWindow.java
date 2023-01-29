package me.soda.witch.client.modules;

import me.soda.witch.client.Witch;

import javax.swing.*;
import java.awt.*;

public class ClientChatWindow extends JFrame {
    public final JTextArea receivedText;
    private final JTextField sendText;

    public ClientChatWindow() {
        setSize(560, 420);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setAlwaysOnTop(true);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        receivedText = new JTextArea() {
            @Override
            public void append(String str) {
                str += "\n";
                super.append(str);
            }
        };
        receivedText.setEditable(false);
        receivedText.setForeground(Color.BLACK);
        panel.add(receivedText);

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        sendText = new JTextField();
        sendPanel.add(sendText);

        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(event -> send());
        sendPanel.add(sendBtn, BorderLayout.EAST);

        panel.add(sendPanel, BorderLayout.SOUTH);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void send() {
        String text = sendText.getText();
        if (!text.isEmpty()) {
            receivedText.append("You: " + text);
            sendText.setText("");
            Witch.send("chat", text);
        }
    }

    public void visible(boolean b) {
        if (b) setTitle("Chat to the " + Witch.CONFIG_INFO.name + " Admin");
        super.setVisible(b);
    }
}
