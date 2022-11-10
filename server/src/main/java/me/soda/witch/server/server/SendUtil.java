package me.soda.witch.server.server;

import me.soda.magictcp.Connection;
import me.soda.witch.shared.Message;

import java.util.List;

public class SendUtil {
    private List<Connection> connCollection;
    private boolean all = true;

    public void trySend(Server server, String messageType, Object object) {
        trySend(server, new Message(messageType, object));
    }

    public void trySend(Server server, String messageType) {
        trySend(server, new Message(messageType, null));
    }

    public void trySend(Connection conn, Server server, String messageType, Object object) {
        trySend(conn, server, new Message(messageType, object));
    }

    public void trySend(Connection conn, Server server, String messageType) {
        trySend(conn, server, new Message(messageType, null));
    }

    private void trySend(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> trySend(conn, server, message));
        } else {
            connCollection.forEach(conn -> trySend(conn, server, message));
        }
    }

    private void trySend(Connection conn, Server server, Message message) {
        try {
            conn.send(server.clientMap.get(conn).encrypt(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public void setConnCollection(List<Connection> connCollection) {
        this.all = false;
        this.connCollection = connCollection;
    }
}
