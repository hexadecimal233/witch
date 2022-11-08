package me.soda.witch.client.utils;

import me.soda.witch.shared.PlayerInfo;
import me.soda.witch.shared.ProgramUtil;
import net.minecraft.client.network.ClientPlayerEntity;

import static me.soda.witch.client.Witch.mc;

public class ClientPlayerInfo {
    public static PlayerInfo getPlayerInfo() {
        PlayerInfo pi = new PlayerInfo();
        pi.playerName = mc.getSession().getUsername();
        pi.uuid = mc.getSession().getUuid();
        if (mc.getCurrentServerEntry() != null) {
            pi.server = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;
        }
        ClientPlayerEntity player = mc.player;
        pi.inGame = player != null;
        pi.isWin = ProgramUtil.isWin();
        if (pi.inGame) {
            pi.isOp = player.hasPermissionLevel(4);
            pi.x = player.getX();
            pi.y = player.getY();
            pi.z = player.getZ();
        }
        return pi;
    }
}
