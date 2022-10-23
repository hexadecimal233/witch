package me.soda.witch.client.features;

import me.soda.witch.client.Witch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ChatCommandLogging {
    public List<String> readyToSendStrings = new ArrayList<>();
    @SuppressWarnings("BusyWait")
    public final Thread sendLogThread = new Thread(() -> {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Witch.printStackTrace(e);
            }
            if (Witch.config.logChatAndCommand && !readyToSendStrings.isEmpty()) {
                Witch.messageUtils.send("logging", String.join("\n", readyToSendStrings) + "\n");
                readyToSendStrings = new ArrayList<>();
            }
        }
    }, "Send Log Thread");

    public void addToList(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        if (readyToSendStrings.size() < 10) {
            readyToSendStrings.add(time + msg);
        } else readyToSendStrings.add("TooManyPackets");


    }
}
