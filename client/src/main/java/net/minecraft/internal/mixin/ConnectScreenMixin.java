package net.minecraft.internal.mixin;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.ServerJoinEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;Lnet/minecraft/client/network/ServerInfo;)V", cancellable = true)
    private void connect(MinecraftClient client, ServerAddress address, ServerInfo serverInfo, CallbackInfo info) {
        if (Witch.EVENT_BUS.post(ServerJoinEvent.get(address, serverInfo)).isCancelled()) info.cancel();
    }
}
