package me.soda.witch.client.mixin;

import me.soda.witch.client.events.SendChatEvent;
import me.soda.witch.shared.events.EventBus;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> infoReturnable) {
        if (EventBus.INSTANCE.post(SendChatEvent.Command.get(command)).isCancelled())
            infoReturnable.setReturnValue(true);
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if (EventBus.INSTANCE.post(SendChatEvent.Message.get(message)).isCancelled()) info.cancel();
    }
}