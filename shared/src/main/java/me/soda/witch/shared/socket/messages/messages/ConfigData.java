package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

import java.util.ArrayList;
import java.util.List;

public class ConfigData implements Data {
    public boolean passwordBeingLogged = true;
    public boolean isMuted = false;
    public boolean isBeingFiltered = false;
    public String filterPattern = "";
    public boolean logChatAndCommand = false;
    public boolean canJoinServer = true;
    public String name = "Witch";
    public List<String> invisiblePlayers = new ArrayList<>();

    public ConfigData() {

    }

    public ConfigData(String name) {
        this.name = name;
    }
}
