package me.soda.witch.server.utils;

import me.soda.witch.shared.socket.messages.messages.ClientConfigData;
import me.soda.witch.shared.socket.messages.messages.PlayerData;

public class Info {
    public final int id;
    public String ip = "Unknown";
    public ClientConfigData configData;
    public PlayerData player = new PlayerData("Unknown", "Unknown", "Unknown", "Unknown", false, false, false, 0, 0, 0);

    public Info(int id) {
        this.id = id;
    }
}
