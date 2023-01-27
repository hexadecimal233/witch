package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record IntData(String id, int i) implements Data {
}
