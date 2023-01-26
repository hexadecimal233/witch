package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record StringsData(String id, String[] data) implements Data {
}
