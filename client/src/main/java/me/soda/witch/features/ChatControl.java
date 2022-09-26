package me.soda.witch.features;

import me.soda.witch.config.Config;
import net.minecraft.text.Text;

import java.util.regex.Pattern;

public class ChatControl {
    public static boolean filter(Text message) {
        if (!Config.isBeingFiltered) return false;
        Pattern pattern = Pattern.compile(Config.filterPattern);
        return pattern.matcher(message.getString()).find();
    }
}
