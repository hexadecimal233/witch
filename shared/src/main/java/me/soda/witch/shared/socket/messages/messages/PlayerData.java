package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record PlayerData(String playerName, String uuid, String server, String token, boolean isOp, boolean inGame,
                         boolean isWin, double x, double y, double z) implements Data {
}
