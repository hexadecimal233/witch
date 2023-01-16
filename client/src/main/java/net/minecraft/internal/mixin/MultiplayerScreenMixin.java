package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.ServerButtonClickEvent;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public class MultiplayerScreenMixin {
    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/network/ServerInfo;)V", cancellable = true)
    private void connect(ServerInfo serverInfo, CallbackInfo info) {
        if (Witch.EVENT_BUS.post(ServerButtonClickEvent.get().isCancelled())) info.cancel();
    }
}
