package me.soda.witch.client.modules;

import me.soda.witch.client.events.TickEvent;
import me.soda.witch.client.utils.ChatUtils;
import me.soda.witch.shared.socket.messages.messages.SpamInfo;
import meteordevelopment.orbit.EventHandler;

import static me.soda.witch.client.Witch.EVENT_BUS;

public class Spam {
    public static final Spam INSTANCE = new Spam();
    private int timer = 0;
    private int index = 0;
    private SpamInfo spamInfo;

    @EventHandler
    private void onTick(TickEvent event) {
        if (spamInfo.message.isEmpty()) return;
        if (index >= spamInfo.times) {
            EVENT_BUS.unsubscribe(this);
        } else if (timer <= 0) {
            String text = spamInfo.message;
            ChatUtils.sendChat(text, spamInfo.invisible);
            index++;
            timer = spamInfo.delayInTicks;
        } else {
            timer--;
        }
    }

    public void spam(SpamInfo msg) {
        timer = 0;
        index = 0;
        spamInfo = msg;
        EVENT_BUS.subscribe(this);
    }
}
