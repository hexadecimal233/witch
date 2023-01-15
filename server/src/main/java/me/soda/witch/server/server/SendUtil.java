package me.soda.witch.server.server;

import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.Message;

import java.util.List;

public class SendUtil {
    private List<Connection> connCollection;
    private boolean all = true;

    public void trySendBytes(Server server, String messageType, byte[] bytes) {
        trySend(server, Message.fromBytes(messageType, bytes));
    }

    public void trySendString(Server server, String messageType) {
        trySend(server, Message.fromString(messageType));
    }

    public void trySendJson(Server server, String messageType, String object) {
        trySend(server, Message.fromJson(Integer.parseInt(messageType), object));
    }

    private void trySend(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> trySend(conn, message));
        } else {
            connCollection.forEach(conn -> trySend(conn, message));
        }
    }

    public void trySend(Connection conn, Message message) {
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
