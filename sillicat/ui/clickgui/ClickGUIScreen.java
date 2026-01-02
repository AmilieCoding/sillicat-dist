package sillicat.ui.clickgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.util.HoverUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;
import sillicat.ui.clickgui.Panel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClickGUIScreen extends GuiScreen {
    private final List<Panel> panels = new ArrayList<>();
    private final CustomFontRenderer titleFont = Sillicat.INSTANCE.getFontManager().getInter().size(16);

    private static final int HEADER_H = 18;
    private static final int PAD = 4;

    public boolean doesGuiPauseGame() {
        return false;
    }

    public ClickGUIScreen(){

    }

    public void initGui(){
        panels.clear();

        int startX = 20;
        int startY = 20;
        int panelW = 120;
        int panelH = 220;
        int gapX = 10;

        int x = startX;

        for (Category c : Category.values()){
            panels.add(new Panel(c, x, startY, panelW, panelH));
            x += panelW + gapX;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        for (Panel p : panels){
            // Update panels if dragging before actually drawing.
            if(p.dragging){
                p.x = mouseX - p.dragOffX;
                p.y = mouseY - p.dragOffY;
            }
            p.render(mouseX, mouseY, partialTicks);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private static class ModuleRow{

    }

    private static class SettingRow{

    }

    // Actual dragger function, also brings to font of needed.
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (int i = panels.size() - 1; i >= 0; i--) {
            Panel p = panels.get(i);
            if (HoverUtil.isHovered(p.x, p.y, p.x + p.w, p.y + p.h, mouseX, mouseY)) {
                // Bring clicked panel to front of all panels.
                panels.remove(i);
                panels.add(p);

                if (p.mouseClicked(mouseX, mouseY, mouseButton)) {
                    return;
                }
                return;
            }
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }


    // Stop dragging on mouserelease.
    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel p : panels) {
            p.mouseReleased();
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    // For dragging.
    // mx = mouseX , my = mouseY;
    private boolean hit(int x, int y, int w, int h, int mx, int my){
        return HoverUtil.isHovered(x, y, x + w, x + h, mx, my);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        // Give panels first chance to consume keybind input
        for (int i = panels.size() - 1; i >= 0; i--) { // topmost first is nice
            if (panels.get(i).handleBindKey(keyCode)) {
                return;
            }
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void onGuiClosed() {
        for (Panel p : panels) p.clearListening();
    }
}
