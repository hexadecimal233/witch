package me.soda.witch.server;

import com.formdev.flatlaf.FlatLightLaf;
import me.soda.witch.server.gui.GUI;

public class Main {
    public static void main(String[] args) {
        FlatLightLaf.setup();
        new GUI();
    }
}