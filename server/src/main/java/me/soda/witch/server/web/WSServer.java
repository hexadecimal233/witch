package me.soda.witch.server.web;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import me.soda.witch.server.server.Info;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.socket.Connection;
import me.soda.witch.shared.socket.messages.Message;
import me.soda.witch.shared.socket.messages.messages.DisconnectData;
import me.soda.witch.shared.socket.messages.messages.StringsData;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class WSServer extends WebSocketServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(WSServer.class);
    private static final Gson GSON = new Gson();
    private final List<WebSocket> authorizedConnections = new ArrayList<>();
    private final Server server;

    public WSServer(int port, Server server) {
        super(new InetSocketAddress(port));
        this.server = server;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        String ip = conn.getRemoteSocketAddress().toString();
        try {
            Message msg = Message.fromJson(message);
            if (!authorizedConnections.contains(conn)) {
                if (msg.data instanceof StringsData data
                        && data.id().equals("auth")
                        && data.data().length > 0
                        && data.data()[0].equals(server.config.accessCode)) {
                    LOGGER.info("Connection Authorized: {}", ip);
                    authorizedConnections.add(conn);
                } else {
                    LOGGER.info("Connection unauthorized: {}", ip);
                    conn.close();
                }
            }

            if (msg.data instanceof ConnectionData data) {
                List<Connection> conns = getConns(data.clientIDs);
                switch (data.operation) {
                    case LIST -> {
                        List<ConnectionInfo> connectionInfos = new ArrayList<>();
                        server.getConnections().forEach(conne -> {
                            Info info = server.clientMap.get(conne);
                            connectionInfos.add(new ConnectionInfo(info.index, info.ip, info.playerData));
                        });
                        conn.send(Message.fromString("connection_list", GSON.toJson(connectionInfos)).toString());
                    }
                    case SELECT -> server.send.setConnCollection(conns);
                    case SELECT_ALL -> server.send.all = true;
                    case RECONNECT -> conns.forEach(connection -> connection.close(DisconnectData.Reason.RECONNECT));
                    case DISCONNECT ->
                            conns.forEach(connection -> connection.close(DisconnectData.Reason.NO_RECONNECT));
                }
            } else {
                server.send.trySend(msg);
            }
        } catch (JsonParseException ignored) {
        } catch (Exception e) {
            conn.send(Message.fromString("internal_exception", e.getMessage()).toString());
            LOGGER.info(e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    @Override
    public void onStart() {
        LOGGER.info("Server started on {}.", getPort());
    }

    private List<Connection> getConns(List<Integer> ids) {
        return server.getConnections().stream().filter(conne -> ids.contains(server.clientMap.get(conne).index)).toList();
    }
}
