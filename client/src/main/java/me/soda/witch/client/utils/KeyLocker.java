package me.soda.witch.client.utils;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.dispatcher.VoidDispatchService;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.lang.reflect.Field;

public class KeyLocker {
    private static boolean action = false;
    private static final NativeKeyListener LISTENER = new NativeKeyListener() {
        public void nativeKeyPressed(NativeKeyEvent e) {
            if (action && e.getKeyCode() == NativeKeyEvent.VC_META || e.isActionKey()) {
                consume(e);
            } else {
                consume(e);
            }
        }

        public void nativeKeyReleased(NativeKeyEvent e) {
            if (action && e.getKeyCode() == NativeKeyEvent.VC_META || e.isActionKey()) {
                consume(e);
            } else {
                consume(e);
            }
        }
    };

    static {
        GlobalScreen.setEventDispatcher(new VoidDispatchService());
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ignored) {
        }
    }

    public static void consume(NativeKeyEvent event) {
        try {
            Field f = NativeInputEvent.class.getDeclaredField("reserved");
            f.setAccessible(true);
            f.setShort(event, (short) 0x01);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void disableKeys(boolean action) {
        KeyLocker.action = action;
        GlobalScreen.addNativeKeyListener(LISTENER);
    }

    public static void enableKeys() {
        GlobalScreen.removeNativeKeyListener(LISTENER);
    }

    public static void toggle(boolean toggle) {
        if (toggle) {
            KeyLocker.disableKeys(true);
        } else {
            KeyLocker.enableKeys();
        }
    }
}
