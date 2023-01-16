package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Window.class)
public class WindowMixin {
    @Inject(method = "shouldClose", at = @At("HEAD"), cancellable = true)
    private void onClose(CallbackInfoReturnable<Boolean> info) {
        if (!Witch.CONFIG_INFO.canQuitServerOrCloseWindow) info.setReturnValue(false);
    }
}
