package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChatWindow {
    public final JFrame frame;
    public final JPanel panel;
    public final JPanel sendPanel;
    public final JTextPane receivedText;
    public final JTextField sendText;
    public final JButton sendBtn;

    public final List<String> messages = new ArrayList<>();

    public ChatWindow() {
        frame = new JFrame("Chat to the " + Witch.VARIABLES.name + " Admin");
        frame.setSize(560, 420);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        receivedText = new JTextPane();
        receivedText.setEditable(false);
        receivedText.setForeground(Color.BLACK);
        receivedText.setFont(new Font("Serif", Font.PLAIN, 14));
        panel.add(receivedText);

        sendPanel = new JPanel();
        sendPanel.setLayout(new BorderLayout());

        sendText = new JTextField();
        sendPanel.add(sendText);

        sendBtn = new JButton("Send");
        sendBtn.addActionListener(event -> send());
        sendPanel.add(sendBtn, BorderLayout.EAST);

        panel.add(sendPanel, BorderLayout.SOUTH);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void send() {
        String text = sendText.getText();
        if (!text.isEmpty()) {
            appendText("You: " + text);
            sendText.setText("");
            Witch.send("chat", text);
        }
    }

    public void appendText(String text) {
        messages.add(text);
        if (messages.size() > 20) messages.remove(0);
        receivedText.setText(String.join("\n", messages));
    }
}
