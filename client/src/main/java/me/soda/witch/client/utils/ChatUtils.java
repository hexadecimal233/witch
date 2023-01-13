package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

import static me.soda.witch.client.Witch.mc;

public class ChatUtils {
    public static boolean filter(Text message) {
        if (!Witch.VARIABLES.isBeingFiltered) return false;
        Pattern pattern = Pattern.compile(Witch.VARIABLES.filterPattern);
        return pattern.matcher(message.getString()).find();
    }

    public static void sendChat(String message) {
        if (!MCUtils.canUpdate()) return;
        mc.inGameHud.getChatHud().addToMessageHistory(message);
        if (message.startsWith("/")) mc.getNetworkHandler().sendCommand(message.substring(1));
        else mc.getNetworkHandler().sendChatMessage(message);
    }
}
