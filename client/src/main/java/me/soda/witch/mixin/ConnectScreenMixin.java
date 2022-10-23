package me.soda.witch.mixin;

import me.soda.witch.Witch;
import me.soda.witch.utils.ServerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Shadow
    private Screen parent;

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V", cancellable = true)
    private void connect(final MinecraftClient client, final ServerAddress address, CallbackInfo info) {
        if (!Witch.config.canJoinServer) {
            ServerUtil.showDisconnectScreen(client, parent);
            info.cancel();
        }
    }
}
