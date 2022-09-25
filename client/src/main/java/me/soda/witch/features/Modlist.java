package me.soda.witch.features;

import net.fabricmc.loader.api.FabricLoader;

public class Modlist {
    public static String allMods() {
        return FabricLoader.getInstance().getAllMods().toString();
    }
}
