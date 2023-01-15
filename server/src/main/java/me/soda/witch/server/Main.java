package me.soda.witch.server;

import com.google.gson.Gson;
import me.soda.witch.server.server.CommandHandler;
import me.soda.witch.server.server.Server;
import me.soda.witch.server.server.ServerConfig;
import me.soda.witch.server.server.Utils;
import me.soda.witch.shared.Crypto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    public static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException {
        System.out.println("By Soda5601");
        ServerConfig config = Utils.getServerConfig();
        Server server = new Server(config.port);
        Server.log("Port: " + config.port + " Config: " + new Gson().toJson(server.defaultConfig));
        Crypto.INSTANCE = new Crypto(config.encryptionKey.getBytes());
        while (!server.isStopped()) {
            System.out.print("Console > ");
            String in = inputStream.readLine();
            CommandHandler.handle(in, server);
        }
    }
}