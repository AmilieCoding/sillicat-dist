package sillicat.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class ScissorUtil {
    private ScissorUtil(){}

    public static void scissor(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int scaleFactor = sr.getScaleFactor();

        GL11.glScissor(
                x * scaleFactor,
                (sr.getScaledHeight() - (y + height)) * scaleFactor,
                width * scaleFactor,
                height * scaleFactor
        );
    }
}
