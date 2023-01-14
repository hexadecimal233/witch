package me.soda.witch.server.server;

import me.soda.witch.shared.socket.messages.messages.PlayerInfo;

public class Info {
    public final int index;
    public PlayerInfo playerData;
    public String ip;

    public Info(int index) {
        this.index = index;
    }
}
