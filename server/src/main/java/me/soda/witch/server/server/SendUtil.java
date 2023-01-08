package me.soda.witch.server.server;

import me.soda.witch.shared.Message;
import me.soda.witch.shared.socket.Connection;

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

    public void trySend(Connection conn, String messageType, Object object) {
        trySend(conn, new Message(messageType, object));
    }

    public void trySend(Connection conn, String messageType) {
        trySend(conn, new Message(messageType, null));
    }

    private void trySend(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> trySend(conn, message));
        } else {
            connCollection.forEach(conn -> trySend(conn, message));
        }
    }

    private void trySend(Connection conn, Message message) {
        try {
            conn.send(message);
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
