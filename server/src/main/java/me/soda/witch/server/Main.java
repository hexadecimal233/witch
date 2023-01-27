package me.soda.witch.server;

import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.server.Server;
import me.soda.witch.server.server.Utils;
import me.soda.witch.server.web.WSServer;
import me.soda.witch.server.web.WebServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        System.out.println("By Soda5601");
        Server server = new Server();
        WSServer wsServer = WebServer.run(Utils.getServerConfig().wsPort, server);
        while (!server.isStopped()) {
            String in = inputStream.readLine();
            CommandHandler.handle(in, server, wsServer);
        }
    }
}