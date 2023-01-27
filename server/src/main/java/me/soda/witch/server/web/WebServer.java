package me.soda.witch.server.web;

import me.soda.witch.server.server.Server;
import me.soda.witch.shared.socket.messages.Message;

public class WebServer {
    static {
        Message.registerMessage(100, ConnectionData.class);
    }

    public static WSServer run(int port, Server server) {
        WSServer wsServer = new WSServer(port, server);
        wsServer.start();
        return wsServer;
    }
}
