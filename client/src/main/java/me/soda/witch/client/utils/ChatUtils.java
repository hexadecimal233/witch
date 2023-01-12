package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import me.soda.witch.shared.socket.messages.Variables;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static me.soda.witch.client.Witch.mc;

public class ChatUtils {
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

    public static boolean tryChatBack(String message) {
        if (firstTime || !message.startsWith("@w ")) return false;
        String chatText = message.substring(3);
        chat(Text.of(chatText), true);
        Witch.send("chat", chatText);
        return true;
    }
}
