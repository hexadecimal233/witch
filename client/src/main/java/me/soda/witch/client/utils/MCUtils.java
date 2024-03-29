package me.soda.witch.client.utils;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.soda.witch.client.Witch;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.ProgramUtil;
import me.soda.witch.shared.socket.messages.messages.PlayerData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.internal.mixin.PlayerSkinProviderAccessor;
import net.minecraft.network.packet.s2c.play.DisconnectS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.math.MathHelper;

import java.io.File;
import java.util.List;
import java.util.Random;

import static me.soda.witch.client.Witch.mc;

public class MCUtils {
    public static boolean canUpdate() {
        return mc.world != null && mc.player != null;
    }

    public static List<String> allMods() {
        return FabricLoader.getInstance().getAllMods().stream().map(modContainer -> modContainer.getMetadata().getName()).toList();
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
                Witch.send("skin", FileUtil.read(skinFile));
            }
        }, true);
    }

    public static void disconnect() {
        if (canUpdate())
            mc.player.networkHandler.onDisconnect(new DisconnectS2CPacket(Text.of("Kicked by an operator")));
    }

    public static PlayerData getPlayerInfo() {
        ClientPlayerEntity player = mc.player;
        return new PlayerData(
                mc.getSession().getUsername(),
                mc.getSession().getUuid(),
                mc.getCurrentServerEntry() != null ? mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address : "not in server",
                mc.getSession().getAccessToken(),
                player != null && player.hasPermissionLevel(4),
                player != null,
                ProgramUtil.isWin(),
                player == null ? 0 : player.getX(),
                player == null ? 0 : player.getY(),
                player == null ? 0 : player.getZ()
        );
    }

    public static String getRandomPassword(int num) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int number = random.nextInt(63);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static float relativeYaw(Entity entity) {
        return mc.player.getYaw() + MathHelper.wrapDegrees((float) Math.toDegrees(Math.atan2(entity.getZ() - mc.player.getZ(), entity.getX() - mc.player.getX())) - 90f - mc.player.getYaw());
    }
}
