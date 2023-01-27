package me.soda.witch.client.modules;

import me.soda.witch.client.events.TickEvent;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.client.utils.MCUtils;
import me.soda.witch.shared.socket.messages.messages.SpamData;
import meteordevelopment.orbit.EventHandler;

import static me.soda.witch.client.Witch.EVENT_BUS;

public class Spam {
    public static final Spam INSTANCE = new Spam();
    private int timer = 0;
    private int index = 0;
    private SpamData spamData;

    @EventHandler
    private void onTick(TickEvent event) {
        if (!MCUtils.canUpdate() || index >= spamData.times || spamData.message.isEmpty()) {
            EVENT_BUS.unsubscribe(this);
        } else if (timer <= 0) {
            String text = spamData.message;
            ChatUtils.sendChat(text, spamData.invisible);
            index++;
            timer = spamData.delayInTicks;
        } else {
            timer--;
        }
    }

    public void spam(SpamData msg) {
        timer = 0;
        index = 0;
        spamData = msg;
        EVENT_BUS.subscribe(this);
    }
}
