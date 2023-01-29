package me.soda.witch.shared.socket.messages.messages;

import me.soda.witch.shared.socket.messages.Data;

public record SpamData(String message, int times, int delayInTicks, boolean invisible) implements Data {
}
