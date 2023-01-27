package me.soda.witch.server.data;

import me.soda.witch.shared.socket.messages.Data;
import me.soda.witch.shared.socket.messages.Message;

public record IndexedMessageData(int index, Message message) implements Data {
}
