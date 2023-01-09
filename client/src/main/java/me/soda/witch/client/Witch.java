package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.connection.MessageHandler;
import me.soda.witch.client.events.MessageReceiveEvent;
import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.EventBus;
import me.soda.witch.shared.LogUtil;
import net.minecraft.client.MinecraftClient;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final String server = Cfg.server();
    public static Client client;

    public static void init() {
        ChatCommandLogging.init();
        EventBus.INSTANCE.registerEvent(MessageReceiveEvent.class, event -> MessageHandler.handleMessage(event.message));
        try {
            client = new Client(server);
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }
}
