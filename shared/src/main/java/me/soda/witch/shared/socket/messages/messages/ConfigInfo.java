package me.soda.witch.shared.socket.messages.messages;

public class ConfigInfo {
    public boolean passwordBeingLogged = true;
    public boolean isMuted = false;
    public boolean isBeingFiltered = false;
    public String filterPattern = "";
    public boolean logChatAndCommand = false;
    public boolean canJoinServer = true;
    public String name = "Witch";
}
