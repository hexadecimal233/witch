package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(method = "pollDebugCrash", at = @At("HEAD"), cancellable = true)
    private void onDebugCrash(CallbackInfo info) {
        if (!Witch.CONFIG_INFO.canQuitServerOrCloseWindow) info.cancel();
    }
}
