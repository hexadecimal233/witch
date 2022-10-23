package me.soda.witch.client.utils;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import java.io.IOException;

import static me.soda.witch.client.Witch.mc;

public class ScreenshotUtil {
    private static boolean screenshot = false;

    public static byte[] takeScreenshot() throws IOException {
        try (NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer())) {
            return nativeImage.getBytes();
        }
    }

    public static boolean canScreenshot() {
        boolean sc = screenshot;
        if (sc) screenshot = false;
        return sc;
    }

    public static void screenshot() {
        screenshot = true;
    }
}
