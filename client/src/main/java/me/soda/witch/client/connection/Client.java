package me.soda.witch.client.connection;

import me.soda.witch.client.Witch;
import me.soda.witch.client.utils.NetUtil;
import me.soda.witch.shared.socket.TcpClient;

public class Client extends TcpClient {
    public int reconnections = 0;

    public Client(String address) throws Exception {
        super(address);
    }

    @Override
    public void onOpen() {
        Witch.println("Connection initialized");
        NetUtil.send("key");
    }

    @Override
    public void onMessage(byte[] bytes) {
        me.soda.witch.client.connection.MessageHandler.handle(bytes);
    }

    @Override
    public void onClose() {
        boolean tooMany = reconnections > 10;
        Witch.messageUtils.acceptXOR = false;
        int code = 0; //todo
        if (code == 1 || !tooMany) {
            Witch.tryReconnect(this::reconnect);
            reconnections++;
        } else {
            Witch.println("Witch end because of manual shutdown or too many reconnections");
        }
    }
}