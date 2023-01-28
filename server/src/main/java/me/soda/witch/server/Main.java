package me.soda.witch.server;

import com.formdev.flatlaf.FlatLightLaf;
import me.soda.witch.server.server.Server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        new Server();
    }
}