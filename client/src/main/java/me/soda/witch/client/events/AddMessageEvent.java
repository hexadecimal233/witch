package me.soda.witch.client.events;

import net.minecraft.text.Text;

public class AddMessageEvent extends Cancellable {
    public static final AddMessageEvent INSTANCE = new AddMessageEvent();

    public Text message;

    public static AddMessageEvent get(Text message) {
        INSTANCE.message = message;
        return INSTANCE;
    }
}
