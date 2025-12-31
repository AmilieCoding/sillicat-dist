package sillicat.util;

import net.minecraft.client.gui.Gui;

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
}
