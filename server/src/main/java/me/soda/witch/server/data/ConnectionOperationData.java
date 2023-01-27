package me.soda.witch.server.data;

import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;

import java.util.List;

public class ConnectionOperationData implements Data {
    public ConnectionOperation operation;
    public List<Integer> clientIDs;
    public Message message;
}
