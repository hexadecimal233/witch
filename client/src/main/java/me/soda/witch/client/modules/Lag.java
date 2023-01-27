package me.soda.witch.client.modules;

import me.soda.witch.client.events.TickEvent;
import meteordevelopment.orbit.EventHandler;

import static me.soda.witch.client.Witch.EVENT_BUS;
import static me.soda.witch.client.Witch.mc;

public class Lag {
    public static final Lag INSTANCE = new Lag();
    int originalFPS = -1;

    @EventHandler
    private void onTick(TickEvent event) {
        mc.options.getMaxFps().setValue(10);
    }

    public void lag(boolean lag) {
        if (lag) {
            originalFPS = mc.options.getMaxFps().getValue();
            EVENT_BUS.subscribe(this);
        } else {
            EVENT_BUS.unsubscribe(this);
            if (originalFPS != -1) mc.options.getMaxFps().setValue(originalFPS);
        }
    }
}
