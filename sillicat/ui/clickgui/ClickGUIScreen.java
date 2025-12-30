package sillicat.ui.clickgui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.util.HoverUtil;
import sillicat.util.RenderUtil;

public class ClickGUIScreen extends GuiScreen {
    private final FontRenderer fr = Sillicat.INSTANCE.getFr();

    private final int boxW = 400;
    private final int boxH = 248;

    private final int belowBoxW = boxW;
    private final int belowBoxH = 20;

    private int scrollOffset = 0;
    private List<Module> modules;
    private Category catSelected = Category.Movement;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        int boxX = (sr.getScaledWidth() - boxW) / 2;
        int boxY = (sr.getScaledHeight() - boxH) / 2;

        int belowBoxX = boxX;
        int belowBoxY = boxY + boxH + 8;

        RenderUtil.drawRect(boxX, boxY, boxW, boxH, 0xFF222222);
        RenderUtil.drawRect(belowBoxX, belowBoxY, belowBoxW, belowBoxH, 0xDD222222);

        int xOffset = belowBoxX + 4;
        for (Category category : Category.values()) {
            fr.drawString(category.name(), xOffset, belowBoxY + 6, 0xFFFFFFFF);
            xOffset += fr.getStringWidth(category.name()) + 8;
        }

        modules = Arrays.asList(
                Sillicat.INSTANCE.getModuleManager().getModules(catSelected)
        );

        renderModuleList(boxX, boxY);
        updateScrollOffset();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (mouseButton != 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        ScaledResolution sr = new ScaledResolution(mc);

        int boxX = (sr.getScaledWidth() - boxW) / 2;
        int boxY = (sr.getScaledHeight() - boxH) / 2;

        int belowBoxX = boxX;
        int belowBoxY = boxY + boxH + 8;

        // ---- Category clicks ----
        int xOffset = belowBoxX + 4;
        for (Category category : Category.values()) {
            int textWidth = fr.getStringWidth(category.name());
            int left = xOffset;
            int right = xOffset + textWidth;
            int top = belowBoxY + 6;
            int bottom = top + fr.FONT_HEIGHT;

            if (HoverUtil.isHovered(left, top, right, bottom, mouseX, mouseY)) {
                if (catSelected != category) {
                    catSelected = category;
                    scrollOffset = 0;
                }
                return;
            }

            xOffset += textWidth + 8;
        }

        // ---- Module clicks ----
        int yOffset = boxY + 4 - scrollOffset;
        int moduleHeight = 24;

        for (Module module : modules) {
            int moduleTop = yOffset;
            int moduleBottom = yOffset + moduleHeight;

            if (
                    mouseX >= boxX + 5 &&
                            mouseX <= boxX + boxW - 5 &&
                            mouseY >= moduleTop &&
                            mouseY <= moduleBottom
            ) {
                module.toggle();
                return;
            }

            yOffset += moduleHeight;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int scroll = Mouse.getEventDWheel();

        if (scroll != 0) {
            // Convert raw mouse coords to scaled GUI coords (1.8.9 style)
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            // Recompute layout exactly like drawScreen
            ScaledResolution sr = new ScaledResolution(mc);

            int boxX = (sr.getScaledWidth() - boxW) / 2;
            int boxY = (sr.getScaledHeight() - boxH) / 2;

            // Only scroll when mouse is inside the module box
            if (HoverUtil.isHovered(
                    boxX,
                    boxY,
                    boxX + boxW,
                    boxY + boxH,
                    mouseX,
                    mouseY
            )) {
                // Mouse wheel direction (1.8.9: positive = up)
                if (scroll > 0) {
                    scrollOffset -= 20;
                } else {
                    scrollOffset += 20;
                }

                updateScrollOffset();
            }
        }

        super.handleMouseInput();
    }

    private void updateScrollOffset(){
        int moduleHeight = 24;
        int visibleModules = 5;
        int totalModules = modules.size();
        int maxScroll = Math.max(0, totalModules * moduleHeight - (visibleModules * moduleHeight));
        if(scrollOffset < 0) scrollOffset = 0;
        if(scrollOffset > maxScroll) scrollOffset = maxScroll;
    }

    private void renderModuleList(int boxX, int boxY) {
        int moduleHeight = 24;
        int yOffset = boxY + 4 - scrollOffset;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(boxX, boxY, boxW, boxH);

        for (Module module : modules) {
            if (yOffset + moduleHeight > boxY && yOffset < boxY + boxH) {
                RenderUtil.drawRect(boxX + 5, yOffset, boxW - 10, 20, 0xDD555555);

                fr.drawString(module.getName(), boxX + 14, yOffset + 4, 0xFFFFFFFF);

                fr.drawSplitString(
                        module.getDescription(),
                        boxX + 14,
                        yOffset + 4 + fr.FONT_HEIGHT,
                        boxW - 28,
                        0xFFDDDDDD
                );

                int toggleX = boxX + boxW - 20;
                int toggleY = yOffset + 6;

                RenderUtil.drawHollowRect(toggleX, toggleY, 10, 10, 1, -1);
                RenderUtil.drawRect(
                        toggleX + 1,
                        toggleY + 1,
                        8,
                        8,
                        module.isToggled() ? 0xFF3FF34F : 0xFFFA4848
                );
            }

            yOffset += moduleHeight;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }


    private void scissor(int x, int y, int width, int height) {
        ScaledResolution sr = new ScaledResolution(mc);
        int scaleFactor = sr.getScaleFactor();

        GL11.glScissor(
                x * scaleFactor,
                (sr.getScaledHeight() - (y + height)) * scaleFactor,
                width * scaleFactor,
                height * scaleFactor
        );
    }
}
