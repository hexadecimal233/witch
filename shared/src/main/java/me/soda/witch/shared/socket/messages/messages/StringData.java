package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record StringData(String messageID, String data) implements Data {
}
