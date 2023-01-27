package me.soda.witch.server.web;

import me.soda.witch.shared.socket.messages.Data;

import java.util.List;

public class ConnectionData implements Data {
    public Operation operation;
    public List<Integer> clientIDs;
}
