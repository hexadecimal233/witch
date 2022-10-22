package me.soda.witch.features;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.DefaultSkinHelper;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static me.soda.witch.Witch.mc;

public class PlayerSkin {
    private static final Gson GSON = new Gson();

    public static byte[] getPlayerSkin() {
        try {
            String PROFILE_REQUEST_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s";

            JsonObject object = new NetUtil.JsonRequest(String.format(PROFILE_REQUEST_URL, mc.getSession().getProfile().getId())).send(JsonObject.class);
            JsonArray array = object.getAsJsonArray("properties");
            JsonObject property = array.get(0).getAsJsonObject();
            String base64String = property.get("value").getAsString();
            byte[] bs = Base64.decodeBase64(base64String);
            String secondResponse = new String(bs, StandardCharsets.UTF_8);
            JsonObject finalResponseObject = GSON.fromJson(secondResponse, JsonObject.class);
            JsonObject texturesObject = finalResponseObject.getAsJsonObject("textures");
            JsonObject skinObj = texturesObject.getAsJsonObject("SKIN");
            String skinURL = skinObj.get("url").getAsString();

            InputStream in = new BufferedInputStream(new URL(skinURL).openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            return out.toByteArray();
        } catch (Exception e) {
            try {
                InputStream in = mc.getResourceManager().getResourceOrThrow(DefaultSkinHelper.getTexture(Objects.requireNonNull(mc.getSession().getUuidOrNull()))).getInputStream();
                byte[] bytes = NativeImage.read(in).getBytes();
                in.close();
                return bytes;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
