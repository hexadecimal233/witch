package me.soda.witch.server.utils;

import com.google.gson.Gson;
import me.soda.witch.server.server.ServerConfig;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.messages.messages.ClientConfigData;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class Utils {
    private static final Gson GSON = new Gson();

    public static ClientConfigData getDefaultClientConfig() {
        String json = FileUtil.read(getDataFile("config/default.json"));
        return GSON.fromJson(json, ClientConfigData.class);
    }

    public static ServerConfig getServerConfig() {
        String json = FileUtil.read(getDataFile("config/server.json"));
        return GSON.fromJson(json, ServerConfig.class);
    }

    public static File getDataFile(String path) {
        return new File("data", path);
    }

    public static String chooseFile(boolean save, Component parent) {
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter(".jar", "jar"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if ((save ? fileChooser.showSaveDialog(parent) : fileChooser.showOpenDialog(parent)) == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().toString();
        }
        return "";
    }

    public static JDialog dialog(Frame owner, String title, Container pane) {
        JDialog dialog = new JDialog(owner, true);
        dialog.setContentPane(pane);
        dialog.setTitle(title);
        dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        return dialog;
    }
}
