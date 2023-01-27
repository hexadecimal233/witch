package net.minecraft.internal.mixin;

import me.soda.witch.client.utils.ChatUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
    private void isInvisibleTo(CallbackInfoReturnable<Boolean> info) {
        if ((Entity) (Object) this instanceof PlayerEntity player) {
            info.setReturnValue(ChatUtils.invisiblePlayer(player.getEntityName()));
        }
    }
}
