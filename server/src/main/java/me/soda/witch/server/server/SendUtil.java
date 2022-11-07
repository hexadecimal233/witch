package me.soda.witch.server.server;

import me.soda.witch.shared.Message;
import org.java_websocket.WebSocket;

import java.util.List;

public class SendUtil {
    private List<WebSocket> connCollection;
    private boolean all = true;

    public void trySend(Server server, String messageType, Object... object) {
        trySend(server, new Message(messageType, object));
    }

    public void trySend(WebSocket conn, Server server, String messageType, Object... object) {
        trySend(conn, server, new Message(messageType, object));
    }

    private void trySend(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> trySend(conn, server, message));
        } else {
            connCollection.forEach(conn -> trySend(conn, server, message));
        }
    }

    private void trySend(WebSocket conn, Server server, Message message) {
        try {
            conn.send(server.clientMap.get(conn).encrypt(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public void setConnCollection(List<WebSocket> connCollection) {
        this.all = false;
        this.connCollection = connCollection;
    }
}
