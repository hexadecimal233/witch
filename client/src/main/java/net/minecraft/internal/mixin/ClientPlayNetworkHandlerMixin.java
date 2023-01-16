package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.GameJoinEvent;
import me.soda.witch.client.events.SendCommandEvent;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void onGameJoin(CallbackInfo info) {
        Witch.EVENT_BUS.post(GameJoinEvent.get());
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> info) {
        if (Witch.EVENT_BUS.post(SendCommandEvent.get(command)).isCancelled()) info.setReturnValue(true);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if (Witch.EVENT_BUS.post(SendCommandEvent.get(message)).isCancelled()) info.cancel();
    }
}