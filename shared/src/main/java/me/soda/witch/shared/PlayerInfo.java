package me.soda.witch.shared;

import java.io.Serializable;

public class PlayerInfo implements Serializable {
    public String playerName, uuid, server;
    public boolean isOp, inGame, isWin;
    public double x, y, z;
}
