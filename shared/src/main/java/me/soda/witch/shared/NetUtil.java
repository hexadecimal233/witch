package me.soda.witch.shared;

import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class NetUtil {
    public static String getIP() {
        return httpSend("https://api.ipify.org/");
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
