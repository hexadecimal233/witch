package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.ChatScreenChatEvent;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Shadow
    private String chatLastMessage;

    @Inject(method = "sendMessage", at = @At("HEAD"), cancellable = true)
    private void onSendMessage(String chatText, boolean addToHistory, CallbackInfoReturnable<Boolean> info) {
        if (Witch.EVENT_BUS.post(ChatScreenChatEvent.get(chatLastMessage)).isCancelled()) info.setReturnValue(false);
    }
}
