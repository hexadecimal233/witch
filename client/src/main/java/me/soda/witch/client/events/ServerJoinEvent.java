package me.soda.witch.client.events;

import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class ServerJoinEvent extends Cancellable {
    public static final ServerJoinEvent INSTANCE = new ServerJoinEvent();

    public ServerInfo info;
    public ServerAddress address;

    public static ServerJoinEvent get(ServerAddress address, ServerInfo info) {
        INSTANCE.address = address;
        INSTANCE.info = info;
        return INSTANCE;
    }
}
