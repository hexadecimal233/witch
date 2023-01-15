package me.soda.witch.server.server;

import com.google.gson.Gson;
import me.soda.witch.shared.FileUtil;
import me.soda.witch.shared.socket.messages.messages.ConfigData;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Utils {
    private static final Gson GSON = new Gson();

    public static ConfigData getDefaultConfig() {
        String json = new String(FileUtil.read(getDataFile("config/default.json")), StandardCharsets.UTF_8);
        return GSON.fromJson(json, ConfigData.class);
    }

    public static File getDataFile(String path) {
        return new File("data", path);
    }
}
