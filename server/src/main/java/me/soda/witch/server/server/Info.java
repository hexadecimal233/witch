package me.soda.witch.server.server;

import me.soda.witch.shared.socket.messages.messages.PlayerData;

public class Info {
    public final int index;
    public PlayerData playerData;
    public String ip;

    public Info(int index) {
        this.index = index;
    }
}
