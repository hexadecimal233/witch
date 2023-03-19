package me.soda.witch.client.modules;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.TickEvent;
import me.soda.witch.client.utils.MCUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import static me.soda.witch.client.Witch.mc;

public enum Lick {
    INSTANCE;

    float pitch = 0;
    boolean down = false;

    @EventHandler
    private void onTick(TickEvent event) {
        if (!MCUtils.canUpdate()) return;
        pitch = pitch + (down ? -7 : 7);
        if (pitch >= 50) {
            down = true;
        } else if (pitch <= -50) {
            down = false;
        }

        PlayerEntity target = null;
        for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof PlayerEntity player && player != mc.player && player.distanceTo(mc.player) < 1.25) {
                target = player;
                break;
            }
        }

        mc.options.sneakKey.setPressed(target != null);
        if (target != null) {
            mc.player.setYaw(MCUtils.relativeYaw(target));
            mc.player.setPitch(pitch);
        }
    }

    public void lick(boolean lick) {
        pitch = 0;
        if (lick)
            Witch.EVENT_BUS.subscribe(this);
        else
            Witch.EVENT_BUS.unsubscribe(this);
    }
}
