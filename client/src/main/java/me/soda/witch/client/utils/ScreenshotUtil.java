package me.soda.witch.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import me.soda.witch.client.Witch;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static me.soda.witch.client.Witch.mc;

public class ScreenshotUtil {
    public static void gameScreenshot() {
        RenderSystem.recordRenderCall(() -> {
            try (NativeImage image = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer())) {
                Witch.send("screenshot", image.getBytes());
            } catch (Exception ignored) {
            }
        });
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
