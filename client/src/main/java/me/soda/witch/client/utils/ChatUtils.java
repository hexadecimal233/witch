package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.AddMessageEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

import static me.soda.witch.client.Witch.mc;

public class ChatUtils {
    public static boolean nextMsgInvisible = false;

    public static boolean invisiblePlayer(String text) {
        for (String name : Witch.CONFIG_INFO.invisiblePlayers) {
            if (text.contains(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean filter(Text text) {
        String message = text.getString();
        if (Witch.CONFIG_INFO.isBeingFiltered) {
            Pattern pattern = Pattern.compile(Witch.CONFIG_INFO.filterPattern);
            return pattern.matcher(message).find();
        }
        if (nextMsgInvisible) {
            nextMsgInvisible = false;
            return true;
        }
        return invisiblePlayer(message);
    }

    public static void sendChat(String message) {
        sendChat(message, false);
    }

    public static void sendChat(String message, boolean invisible) {
        if (!MCUtils.canUpdate()) return;
        if (!invisible) {
            mc.inGameHud.getChatHud().addToMessageHistory(message);
        } else nextMsgInvisible = true;
        if (message.startsWith("/")) mc.getNetworkHandler().sendCommand(message.substring(1));
        else mc.getNetworkHandler().sendChatMessage(message);
    }

    @EventHandler
    private static void onAddMessage(AddMessageEvent event) {
        if (Witch.CONFIG_INFO.logChatAndCommand) LoopThread.addToList(event.message.getString());
        if (ChatUtils.filter(event.message)) event.cancel();
    }
}
