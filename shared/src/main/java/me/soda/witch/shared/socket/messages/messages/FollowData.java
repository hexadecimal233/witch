package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record FollowData(String playerName, double distance, boolean stop) implements Data {
}
