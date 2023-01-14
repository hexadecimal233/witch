package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record BooleanData(String messageID, boolean data) implements Data {
}
