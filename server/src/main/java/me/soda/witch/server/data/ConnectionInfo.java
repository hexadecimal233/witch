package me.soda.witch.server.data;

import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.messages.PlayerData;

public class ConnectionInfo implements Data {
    public final int id;
    public String ip;
    public PlayerData player;

    public ConnectionInfo(int id) {
        this.id = id;
    }
}
