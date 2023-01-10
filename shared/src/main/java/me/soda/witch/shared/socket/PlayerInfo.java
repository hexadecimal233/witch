package me.soda.witch.shared.socket;

import java.io.Serial;
import java.io.Serializable;

public class PlayerInfo implements Serializable {
    @Serial
    private static final long serialVersionUID = 6572141426520703802L;

    public String playerName, uuid, server, token;
    public boolean isOp, inGame, isWin;
    public double x, y, z;
}
