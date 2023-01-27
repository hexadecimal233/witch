package me.soda.witch.client.modules;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.TickEvent;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.shared.socket.messages.messages.FollowData;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import static me.soda.witch.client.Witch.mc;

public class Follower {
    public static final Follower INSTANCE = new Follower();

    FollowData data;

    @EventHandler
    private void onTick(TickEvent event) {
        if (data.stop()) Witch.EVENT_BUS.unsubscribe(this);
        if (!MCUtils.canUpdate()) return;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player && player != mc.player && player.getEntityName().equalsIgnoreCase(data.playerName())) {
                if (mc.player.horizontalCollision && mc.player.isOnGround())
                    mc.player.jump();

                if (mc.player.isTouchingWater() && mc.player.getY() < player.getY())
                    mc.player.setVelocity(mc.player.getVelocity().add(0, 0.04, 0));

                if (!mc.player.isOnGround() && mc.player.getAbilities().flying
                        && mc.player.squaredDistanceTo(player.getX(), mc.player.getY(),
                        player.getZ()) <= mc.player.squaredDistanceTo(
                        mc.player.getX(), player.getY(), mc.player.getZ())) {
                    if (mc.player.getY() > player.getY() + 1D)
                        mc.options.sneakKey.setPressed(true);
                    else if (mc.player.getY() < player.getY() - 1D)
                        mc.options.jumpKey.setPressed(true);
                } else {
                    mc.options.sneakKey.setPressed(false);
                    mc.options.jumpKey.setPressed(false);
                }

                mc.player.setYaw(MCUtils.relativeYaw(player));
                mc.options.forwardKey.setPressed(mc.player.distanceTo(player) > data.distance());
                break;
            }
        }
    }

    public void follow(FollowData data) {
        this.data = data;
        Witch.EVENT_BUS.subscribe(this);
    }
}
