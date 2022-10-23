package me.soda.witch.server.server;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import java.util.List;

public class SendUtil {
    private static final Gson GSON = new Gson();
    private List<WebSocket> connCollection;
    private boolean all = true;

    public void trySend(Server server, String messageType, Object... object) {
        String json = GSON.toJson(object);
        trySend(server, new Message(messageType, json));
    }

    public void trySend(WebSocket conn, Server server, String messageType, Object... object) {
        String json = GSON.toJson(object);
        trySend(conn, server, new Message(messageType, json));
    }

    private void trySend(Server server, Message message) {
        if (all) {
            server.getConnections().forEach(conn -> trySend(conn, server, message));
        } else {
            connCollection.forEach(conn -> trySend(conn, server, message));
        }
    }

    private void trySend(WebSocket conn, Server server, Message message) {
        conn.send(server.clientMap.get(conn).encrypt(message, server));
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public void setConnCollection(List<WebSocket> connCollection) {
        this.all = false;
        this.connCollection = connCollection;
    }
}
