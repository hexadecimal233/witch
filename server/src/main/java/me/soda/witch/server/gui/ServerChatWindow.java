package me.soda.witch.server.gui;


import me.soda.witch.shared.socket.Connection;

import javax.swing.*;
import java.awt.*;

public class ServerChatWindow extends JDialog {
    public final JTextArea receivedText;
    public final Connection connection;
    private final JTextField sendText;

    public ServerChatWindow(Connection connection) {
        this.connection = connection;
        setSize(560, 420);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

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
        setVisible(true);
    }

    private void send() {
        String text = sendText.getText();
        if (!text.isEmpty()) {
            receivedText.append("You: " + text);
            sendText.setText("");
        }
    }
}
