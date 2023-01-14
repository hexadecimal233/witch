package me.soda.witch.client.events;

import meteordevelopment.orbit.ICancellable;

public class Cancellable implements ICancellable {
    private boolean cancelled = false;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
