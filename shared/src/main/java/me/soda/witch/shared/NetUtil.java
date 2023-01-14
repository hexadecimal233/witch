package me.soda.witch.shared;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

public class NetUtil {
    public static String getIP() {
        try {
            return httpSend("https://api.ipify.org/");
        } catch (IOException e) {
            LogUtil.printStackTrace(e);
            return "unknown";
        }
    }

    public static String httpSend(String url) throws IOException {
        try (InputStream inputStream = new URL(url).openStream(); Scanner scanner = new Scanner(inputStream)) {
            StringBuilder sb = new StringBuilder();
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }
            return sb.toString();
        }
    }
}
