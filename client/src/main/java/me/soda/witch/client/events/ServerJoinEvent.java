package me.soda.witch.client.events;

import me.soda.witch.shared.events.Cancellable;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

public class ServerJoinEvent extends Cancellable {
    public static final ServerJoinEvent INSTANCE = new ServerJoinEvent();

    public ServerInfo info;
    public ServerAddress address;

    public static ServerJoinEvent get(ServerAddress address, ServerInfo info) {
        INSTANCE.setCancelled(false);
        INSTANCE.address = address;
        INSTANCE.info = info;
        return INSTANCE;
    }
}
