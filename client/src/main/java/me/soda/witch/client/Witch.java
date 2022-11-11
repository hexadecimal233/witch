package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Variables;
import me.soda.witch.shared.Cfg;
import net.minecraft.client.MinecraftClient;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Variables variables = new Variables();
    private static final boolean print = Boolean.getBoolean("fabric.development");
    //config
    private static final String server = Cfg.server();
    //variables
    public static Client client;

    public static void init() {
        System.setProperty("java.awt.headless", "false");
        ChatCommandLogging.init();
        try {
            client = new Client(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void printStackTrace(Exception e) {
        if (Witch.print) e.printStackTrace();
    }

    public static void println(Object o) {
        if (Witch.print) System.out.println("[WITCH] " + o);
    }
}
