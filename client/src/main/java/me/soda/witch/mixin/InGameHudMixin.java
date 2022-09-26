package me.soda.witch.mixin;

import me.soda.witch.config.Config;
import me.soda.witch.features.Screenshot;
import me.soda.witch.websocket.MessageUtils;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (Config.takeScreenshot) {
            try {
                Config.takeScreenshot = false;
                MessageUtils.sendMessage("screenshot", Screenshot.takeScreenshot());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
