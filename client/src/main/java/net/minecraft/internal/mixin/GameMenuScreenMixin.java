package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin {
    @Inject(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/report/AbuseReportContext;tryShowDraftScreen(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/gui/screen/Screen;Ljava/lang/Runnable;Z)V"), cancellable = true)
    private void onExitButton(ButtonWidget button, CallbackInfo info) {
        if (!Witch.CONFIG_INFO.canQuitServerOrCloseWindow) info.cancel();
    }
}
