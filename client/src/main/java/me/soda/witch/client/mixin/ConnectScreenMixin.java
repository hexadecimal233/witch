package me.soda.witch.client.mixin;

import me.soda.witch.client.features.Variables;
import me.soda.witch.client.utils.ServerUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ServerAddress;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {
    @Final
    @Shadow
    Screen parent;

    @Inject(at = @At("HEAD"), method = "connect(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/network/ServerAddress;)V", cancellable = true)
    private void connect(final MinecraftClient client, final ServerAddress address, CallbackInfo info) {
        if (!Variables.canJoinServer) {
            ServerUtil.showDisconnectScreen(client, parent);
            info.cancel();
        }
    }
}
