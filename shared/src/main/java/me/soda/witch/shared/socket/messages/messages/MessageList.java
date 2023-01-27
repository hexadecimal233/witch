package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;

import java.util.List;

public record MessageList<T extends Message>(String id, List<T> data) implements Data {
}
