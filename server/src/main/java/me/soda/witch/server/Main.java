package me.soda.witch.server;

import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.server.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static Server server;

    public static void main(String[] args) throws Exception {
        System.out.println("By Soda5601");
        int port = 11451;
        String name = "Witch";
        if (args.length >= 1) port = Integer.parseInt(args[0]);
        if (args.length >= 2) name = args[1];
        server = new Server(port, name);
        server.log("Port: " + port + " Name: " + name);
        System.out.print("Console > ");
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (!server.stopped) {
            String in = inputStream.readLine();
            CommandHandler.handle(in, server);
            System.out.print("Console > ");
        }
    }
}