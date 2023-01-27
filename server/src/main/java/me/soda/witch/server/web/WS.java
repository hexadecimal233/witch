package me.soda.witch.server.web;

import me.soda.witch.server.data.ConnectionEvent;
import me.soda.witch.server.data.ConnectionEventData;
import me.soda.witch.server.data.IndexedMessageData;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import me.soda.witch.shared.socket.messages.messages.PlayerData;
import me.soda.witch.shared.socket.messages.messages.StringsData;

import java.io.IOException;
import java.util.List;

public class WS extends Server {
    private final WSServer wsServer;

    public WS() throws IOException {
        super();
        wsServer = new WSServer(this.config.wsPort, this);
        wsServer.start();
    }

    @Override
    public void onMessage(Connection conn, Message message) {
        super.onMessage(conn, message);
        if (message.data instanceof StringsData data) {
            if (data.id().equals("ip")) {
                event(ConnectionEvent.CHANGE, conn);
            }
        } else if (message.data instanceof PlayerData) {
            event(ConnectionEvent.CHANGE, conn);
        }
        trustedSend(new Message(new IndexedMessageData(clientMap.get(conn).id, message)));
    }

    @Override
    public void onClose(Connection conn, DisconnectData disconnectData) {
        event(ConnectionEvent.REMOVE, conn);
        super.onClose(conn, disconnectData);
    }

    @Override
    public void onOpen(Connection conn) {
        super.onOpen(conn);
        event(ConnectionEvent.ADD, conn);
    }

    @Override
    public void stop() throws IOException {
        try {
            wsServer.stop();
        } catch (InterruptedException ignored) {
        }
        super.stop();
    }

    private void event(ConnectionEvent event, Connection conn) {
        trustedSend(new Message(new ConnectionEventData(event, clientMap.get(conn))));
    }

    private void trustedSend(Message message) {
        wsServer.getConnections().stream().filter(c -> !config.auth || wsServer.authorizedConnections.contains(c)).forEach(conn -> conn.send(message.toString()));
    }
}
