package me.soda.witch.server;

import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.server.Server;
import me.soda.witch.shared.Cfg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        System.out.println("By Soda5601");
        Server server = new Server(Cfg.port, Cfg.name);
        Server.log("Port: " + Cfg.port + " Name: " + Cfg.name);
        System.out.print("Console > ");
        while (!server.stopped) {
            String in = inputStream.readLine();
            CommandHandler.handle(in, server);
            System.out.print("Console > ");
        }
    }
}