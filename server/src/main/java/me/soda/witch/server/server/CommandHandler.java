package me.soda.witch.server.server;

import me.soda.witch.server.Main;
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
                if (msgArr[0].equals("stop")) {
                    Main.inputStream.close();
                    server.stop();
                    wsServer.stop();
                } else {
                    if (msgArr.length >= 2 && msgArr[0].equals("execute")) {
                        File file = new File(in.substring(msgArr[0].length() + 1));
                        try (FileInputStream is = new FileInputStream(file)) {
                            server.send.trySendBytes(server, msgArr[0], is.readAllBytes());
                        }
                    } else {
                        server.send.trySendJson(server, msgArr[0]);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage());
            }
        }
    }
}
