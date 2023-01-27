package net.minecraft.internal.mixin;

import me.soda.witch.client.utils.ChatUtils;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "method_18144", at = @At("HEAD"), cancellable = true, remap = false)
    private static void onTargetedEntityCanHit(Entity entity, CallbackInfoReturnable<Boolean> info) {
        if (entity instanceof PlayerEntity && ChatUtils.invisiblePlayer(entity.getEntityName()))
            info.setReturnValue(false);
    }
}
