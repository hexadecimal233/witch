package me.soda.witch.server.web;

import me.soda.witch.shared.socket.messages.messages.PlayerData;

public record ConnectionInfo(int id, String ip, PlayerData name) {
}
