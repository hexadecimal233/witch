package me.soda.witch.client.modules;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.TickEvent;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.MCUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.List;

import static me.soda.witch.client.Witch.mc;

public enum OpEveryone {
    INSTANCE;
    private final List<String> opPlayers = new ArrayList<>();
    private boolean deop = false;
    private int timer = 0;
    private int index = 0;

    @EventHandler
    private void onTick(TickEvent event) {
        if (!MCUtils.canUpdate() || index >= opPlayers.size()) {
            Witch.EVENT_BUS.unsubscribe(this);
        } else if (timer <= -1) {
            ChatUtils.sendChat(deop ? "/deop " : "/op " + opPlayers.get(index));
            index++;
            timer = 20;
        } else {
            timer--;
        }
    }

    public void opEveryone(boolean deop) {
        opPlayers.clear();
        this.deop = deop;
        timer = 0;
        index = 0;
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
