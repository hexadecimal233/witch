package me.soda.witch.client.utils;

import me.soda.witch.client.Witch;
import me.soda.witch.shared.Message;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import static me.soda.witch.client.Witch.client;

public class NetUtil {
    public static void send(String messageType, Object object) {
        try {
            client.send(new Message(messageType, object));
        } catch (Exception e) {
            Witch.printStackTrace(e);
        }
    }

    public static void send(String messageType) {
        send(messageType, null);
    }

    public static String httpSend(String url) {
        try (InputStream inputStream = new URL(url).openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
            return sb.toString();
        } catch (Exception ignored) {
            return "";
        }
    }
}
