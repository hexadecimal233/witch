package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record BooleanData(String id, boolean bl) implements Data {
}
