package sillicat.util;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

public class RenderUtil {
    public static void drawRect(double x, double y, double w, double h, int color){
        Gui.drawRect(x, y, x + w, y + h, color);
    }

    public static void drawHollowRect(int x, int y, int width, int height, int lineW, int color){
        Gui.drawRect(x, y, x + width, y + lineW, color); // Top
        Gui.drawRect(x + width, y, x + width + lineW, y + height + 1, color); // Right
        Gui.drawRect(x, y, x + lineW, y + height, color); // Left
        Gui.drawRect(x, y + height, x + width, y + height + lineW, color); // Bottom
    }

    // Yoinked from our beloved flaily and Xynon.
    public static void drawRoundedRect(double x, double y, double width, double height, double radius, int color) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        double x1 = x + width;
        double y1 = y + height;
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glScaled(0.5, 0.5, 0.5);

        x *= 2;
        y *= 2;
        x1 *= 2;
        y1 *= 2;

        GL11.glDisable(GL_TEXTURE_2D);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        GL11.glBegin(GL11.GL_POLYGON);

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x + radius + +(Math.sin((i * Math.PI / 180)) * (radius * -1)), y + radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x + radius + (Math.sin((i * Math.PI / 180)) * (radius * -1)), y1 - radius + (Math.cos((i * Math.PI / 180)) * (radius * -1)));
        }

        for (int i = 0; i <= 90; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y1 - radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        for (int i = 90; i <= 180; i += 3) {
            GL11.glVertex2d(x1 - radius + (Math.sin((i * Math.PI / 180)) * radius), y + radius + (Math.cos((i * Math.PI / 180)) * radius));
        }

        GL11.glEnd();

        GL11.glEnable(GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glEnable(GL_TEXTURE_2D);

        GL11.glScaled(2, 2, 2);

        GL11.glPopMatrix();
        GL11.glPopAttrib();

        GlStateManager.color(1f, 1f, 1f, 1f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();

    }
}
