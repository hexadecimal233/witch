package me.soda.witch.server;

import me.soda.witch.server.handlers.CommandHandler;
import me.soda.witch.server.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static Server server;

    public static void main(String[] args) throws IOException {
        int port = 11451;
        String key = "good_key_qwq";
        String name = "Witch";
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) key = args[1];
        if (args.length >= 3) name = args[2];
        server = new Server(port, key, name);
        CommandHandler commandHandler = new CommandHandler();
        server.start();
        server.log("Port: " + server.getPort());
        System.out.print("Console > ");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!server.stopped) {
            String in = inputStream.readLine();
            commandHandler.handle(in, server);
            System.out.print("Console > ");
        }
    }
}