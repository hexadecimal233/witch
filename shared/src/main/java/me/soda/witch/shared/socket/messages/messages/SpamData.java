package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public class SpamData implements Data {
    public String message;
    public int times;
    public int delayInTicks;
    public boolean invisible;
}
