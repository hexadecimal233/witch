package me.soda.witch.server.utils;

import com.google.gson.Gson;
import me.soda.witch.server.server.ServerConfig;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.messages.messages.ClientConfigData;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final Gson GSON = new Gson();

    public static ClientConfigData getDefaultClientConfig() {
        String json = new String(FileUtil.read(getDataFile("config/default.json")), StandardCharsets.UTF_8);
        return GSON.fromJson(json, ClientConfigData.class);
    }

    public static ServerConfig getServerConfig() {
        String json = new String(FileUtil.read(getDataFile("config/server.json")), StandardCharsets.UTF_8);
        return GSON.fromJson(json, ServerConfig.class);
    }

    public static File getDataFile(String path) {
        return new File("data", path);
    }
}
