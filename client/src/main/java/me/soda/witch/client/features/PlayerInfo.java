package me.soda.witch.client.features;

import me.soda.witch.shared.ProgramUtil;
import net.minecraft.client.network.ClientPlayerEntity;

import static me.soda.witch.client.Witch.mc;

public class PlayerInfo {
    public String playerName, uuid, server;
    public boolean isOp, inGame, isWin;
    public double x, y, z;

    public PlayerInfo() {
        playerName = mc.getSession().getUsername();
        uuid = mc.getSession().getUuid();
        if (mc.getCurrentServerEntry() != null) {
            server = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;
        }
        ClientPlayerEntity player = mc.player;
        inGame = player != null;
        isWin = ProgramUtil.isWin();
        if (inGame) {
            isOp = player.hasPermissionLevel(4);
            x = player.getX();
            y = player.getY();
            z = player.getZ();
        }
    }
}
