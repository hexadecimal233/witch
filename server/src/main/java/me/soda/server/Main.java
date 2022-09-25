package me.soda.server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static me.soda.server.Server.LOGGER;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(11451);
        server.start();
        LOGGER.info("端口: " + server.getPort());
        BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String in = sysin.readLine();
            server.broadcast(in);
            if (in.equals("exit")) {
                server.stop(1000);
                break;
            }
        }
    }
}