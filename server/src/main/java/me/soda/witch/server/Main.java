package me.soda.witch.server;

import com.google.gson.Gson;
import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.server.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        System.out.println("By Soda5601");
        int port = args.length >= 1 ? Integer.parseInt(args[0]) : 11451;
        Server server = new Server(port);
        Server.log("Port: " + port + "config: " + new Gson().toJson(server.defaultConfig));
        System.out.print("Console > ");
        while (!server.isStopped()) {
            String in = inputStream.readLine();
            CommandHandler.handle(in, server);
            System.out.print("Console > ");
        }
    }
}