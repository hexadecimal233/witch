package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

import java.util.List;

public record StringsData(String id, List<String> data) implements Data {
}
