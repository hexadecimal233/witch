package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public class PlayerData implements Data {
    public String playerName, uuid, server, token;
    public boolean isOp, inGame, isWin;
    public double x, y, z;
}
