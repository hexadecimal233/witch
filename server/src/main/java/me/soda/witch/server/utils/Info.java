package me.soda.witch.server.utils;

import me.soda.witch.shared.socket.messages.messages.PlayerData;

public class Info {
    public final int id;
    public String ip;
    public PlayerData player;

    public Info(int id) {
        this.id = id;
    }
}
