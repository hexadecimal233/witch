package me.soda.witch.client.utils;

import me.soda.witch.client.events.TickEvent;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

import static me.soda.witch.client.Witch.EVENT_BUS;
import static me.soda.witch.client.Witch.mc;

public class OpEveryone {
    private static int opTimer = 0;
    private static int opIndex = 0;
    private static boolean shouldOp = false;
    private static List<String> opPlayers = new ArrayList<>();

    static {
        EVENT_BUS.registerEvent(TickEvent.class, event -> {
            if (!shouldOp) return;
            if (opTimer > -1) {
                opTimer--;
                return;
            }

            if (opIndex >= opPlayers.size()) {
                shouldOp = false;
                return;
            }

            mc.getNetworkHandler().sendCommand("tpa " + opPlayers.get(opIndex));
            opIndex++;
            opTimer = 20;
        });
    }


    public static void opEveryone() {
        opPlayers.clear();
        if (!MCUtils.canUpdate()) return;
        String pName = mc.getSession().getProfile().getName();
        for (PlayerListEntry info : mc.getNetworkHandler().getPlayerList()) {
            String name = info.getProfile().getName();
            if (StringHelper.stripTextFormat(name).equalsIgnoreCase(pName))
                continue;

            opPlayers.add(name);
        }
        shouldOp = true;
    }
}
