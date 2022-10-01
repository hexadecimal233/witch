package me.soda.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {

    public static Server server = new Server(11451);

    public static void main(String[] args) throws IOException {
        CommandHandler commandHandler = new CommandHandler();
        server.start();
        Server.log("Port: " + server.getPort());
        BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = inputStream.readLine();
            if (commandHandler.handle(in, server)) break;
        }
    }
}