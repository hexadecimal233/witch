package me.soda.witch.server;

import com.formdev.flatlaf.FlatLightLaf;
import me.soda.witch.server.gui.ServerGUI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        FlatLightLaf.setup();
        new ServerGUI();
    }
}