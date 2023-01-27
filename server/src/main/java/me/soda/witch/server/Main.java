package me.soda.witch.server;

import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.web.WS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        WS wsServer = new WS();
        while (!wsServer.isStopped()) {
            String in = inputStream.readLine();
            CommandHandler.handle(in, wsServer);
        }
    }
}