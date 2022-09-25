package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.config.Config;
import me.soda.witch.features.Screenshot;
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
                Witch.sendMessage("screenshot", Screenshot.takeScreenshot());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
