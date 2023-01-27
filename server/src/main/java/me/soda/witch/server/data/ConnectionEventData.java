package me.soda.witch.server.data;

import me.soda.witch.shared.socket.messages.Data;

public record ConnectionEventData(ConnectionEvent event, ConnectionInfo info) implements Data {
}
