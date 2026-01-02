package sillicat.ui.clickgui.legacy;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.ui.designLanguage.Theme;
import sillicat.util.HoverUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

public class ClickGUIScreenLegacy extends GuiScreen {
    private final FontRenderer fr = Sillicat.INSTANCE.getFr();

    private final CustomFontRenderer titleFont = Sillicat.INSTANCE.getFontManager().getInter().size(18);
    private final CustomFontRenderer descFont  = Sillicat.INSTANCE.getFontManager().getInter().size(12);

    private final int boxW = 400;
    private final int boxH = 248;

    private final int belowBoxW = boxW;
    private final int belowBoxH = 20;

    private int scrollOffset = 0;
    private List<Module> modules = new ArrayList<>();
    private Category catSelected = Category.Movement;

    // Layout constants (keeps everything aligned)
    private static final int PAD_L = 14;
    private static final int PAD_R = 14;
    private static final int ROW_H = 30;
    private static final int ROW_GAP = 3;
    private static final int ROW_INSET = 5;

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        int boxX = (sr.getScaledWidth() - boxW) / 2;
        int boxY = (sr.getScaledHeight() - boxH) / 2;

        int belowBoxX = boxX;
        int belowBoxY = boxY + boxH + 8;

        RenderUtil.drawRect(boxX, boxY, boxW, boxH, ColorScheme.PANEL_BG.get());
        RenderUtil.drawRect(belowBoxX, belowBoxY, belowBoxW, belowBoxH, ColorScheme.PANEL_BG_ALT.get());

        int xOffset = belowBoxX + 4;
        for (Category category : Category.values()) {
            titleFont.drawString(category.name(), xOffset, belowBoxY + 6, ColorScheme.TEXT_PRIMARY.get());
            xOffset += (int) (titleFont.getWidth(category.name()) + 8);
        }

        modules = Arrays.asList(
                Sillicat.INSTANCE.getModuleManager().getModules(catSelected)
        );

        updateScrollOffset();       // clamp BEFORE drawing
        renderModuleList(boxX, boxY, mouseX, mouseY);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

        int boxX = (sr.getScaledWidth() - boxW) / 2;
        int boxY = (sr.getScaledHeight() - boxH) / 2;

        int belowBoxX = boxX;
        int belowBoxY = boxY + boxH + 8;

        // ---- Category clicks (left only) ----
        if (mouseButton == 0) {
            int xOffset = belowBoxX + 4;
            for (Category category : Category.values()) {
                int textWidth = (int) titleFont.getWidth(category.name());
                int left = xOffset;
                int right = xOffset + textWidth;
                int top = belowBoxY + 6;
                int bottom = (int) (top + titleFont.getHeight("A"));

                if (HoverUtil.isHovered(left, top, right, bottom, mouseX, mouseY)) {
                    if (catSelected != category) {
                        catSelected = category;
                        scrollOffset = 0;
                    }
                    return;
                }
                xOffset += textWidth + 8;
            }
        }

        // ---- Module clicks (left = toggle, right = settings) ----
        int yOffset = boxY + 4 - scrollOffset;

        for (Module module : modules) {
            int moduleTop = yOffset;
            int moduleBottom = yOffset + ROW_H;

            if (HoverUtil.isHovered(
                    boxX + ROW_INSET,
                    moduleTop,
                    boxX + boxW - ROW_INSET,
                    moduleBottom,
                    mouseX,
                    mouseY
            )) {
                if (mouseButton == 0) module.toggle();
                else if (mouseButton == 1) showSettings(module);
                return;
            }

            yOffset += ROW_H + ROW_GAP;
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        int scroll = Mouse.getEventDWheel();

        if (scroll != 0) {
            int mouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
            int mouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

            ScaledResolution sr = new ScaledResolution(mc);
            int boxX = (sr.getScaledWidth() - boxW) / 2;
            int boxY = (sr.getScaledHeight() - boxH) / 2;

            if (HoverUtil.isHovered(
                    boxX,
                    boxY,
                    boxX + boxW,
                    boxY + boxH,
                    mouseX,
                    mouseY
            )) {
                if (scroll > 0) scrollOffset -= 20;
                else scrollOffset += 20;

                updateScrollOffset();
            }
        }

        super.handleMouseInput();
    }

    private void updateScrollOffset() {
        int totalRows = modules == null ? 0 : modules.size();
        int contentH = totalRows * (ROW_H + ROW_GAP) - ROW_GAP;
        int maxScroll = Math.max(0, contentH - (boxH - 8)); // -8 for top/bottom breathing room

        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxScroll) scrollOffset = maxScroll;
    }

    private void renderModuleList(int boxX, int boxY, int mouseX, int mouseY) {
        int yOffset = boxY + 4 - scrollOffset;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        scissor(boxX, boxY, boxW, boxH);

        for (Module module : modules) {
            if (yOffset + ROW_H > boxY && yOffset < boxY + boxH) {

                RenderUtil.drawRect(
                        boxX + ROW_INSET,
                        yOffset,
                        boxW - (ROW_INSET * 2),
                        ROW_H,
                        ColorScheme.ROW_BG.get()
                );

                boolean hovered = HoverUtil.isHovered(
                        boxX + ROW_INSET,
                        yOffset,
                        boxX + boxW - ROW_INSET,
                        yOffset + ROW_H,
                        mouseX,
                        mouseY
                );

                if (hovered) {
                    RenderUtil.drawRect(
                            boxX + ROW_INSET,
                            yOffset,
                            boxW - (ROW_INSET * 2),
                            ROW_H,
                            Theme.getAccentWithAlpha(40)
                    );
                }

                // Title line
                float nameX = boxX + PAD_L;
                float nameY = yOffset + 5;
                titleFont.drawString(module.getName(), nameX, nameY, ColorScheme.TEXT_PRIMARY.get());

                // Description lines (wrapped using descFont widths)
                float descX = boxX + PAD_L;
                float descStartY = nameY + titleFont.getHeight("A") + 5;
                float wrapW = boxW - (PAD_L + PAD_R);

                List<String> lines = wrapCfr(module.getDescription(), wrapW, descFont);

                float y = descStartY;
                float lineH = descFont.getHeight("A");

                for (String line : lines) {
                    if (y + lineH > yOffset + ROW_H - 2) break; // don't overflow row
                    descFont.drawString(line, descX, y, ColorScheme.TEXT_SECONDARY.get());
                    y += lineH + 1;
                }

                // Toggle box, vertically centered
                int toggleSize = 10;
                int toggleX = boxX + boxW - 20;
                int toggleY = yOffset + (ROW_H - toggleSize) / 2;

                RenderUtil.drawHollowRect(toggleX, toggleY, toggleSize, toggleSize, 1, ColorScheme.TEXT_PRIMARY.get());
                RenderUtil.drawRect(
                        toggleX + 1,
                        toggleY + 1,
                        toggleSize - 1,
                        toggleSize - 1,
                        module.isToggled()
                                ? Theme.getAccent()
                                : ColorScheme.ROW_BG.get()
                );
            }

            yOffset += ROW_H + ROW_GAP;
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

    private void showSettings(Module module) {
        mc.displayGuiScreen(new SettingsScreen(module));
    }

    // ---- CustomFontRenderer wrapping (keeps § formatting) ----
    private static List<String> wrapCfr(String text, float maxWidth, CustomFontRenderer cfr) {
        List<String> out = new ArrayList<>();
        if (text == null || text.isEmpty()) return out;

        // Respect manual newlines too
        String[] paragraphs = text.split("\n");
        for (String para : paragraphs) {
            if (para.isEmpty()) {
                out.add("");
                continue;
            }

            String[] words = para.split(" ");
            StringBuilder line = new StringBuilder();
            String activeFormat = "";

            for (String word : words) {
                activeFormat = getLastFormat(activeFormat + word);

                String test = line.length() == 0 ? (activeFormat + word) : (line + " " + word);
                if (cfr.getWidth(test) <= maxWidth) {
                    if (line.length() != 0) line.append(" ");
                    if (line.length() == 0 && !activeFormat.isEmpty()) line.append(activeFormat);
                    line.append(word);
                } else {
                    if (line.length() > 0) out.add(line.toString());
                    line.setLength(0);
                    if (!activeFormat.isEmpty()) line.append(activeFormat);
                    line.append(word);
                }
            }

            if (line.length() > 0) out.add(line.toString());
        }

        return out;
    }

    private static String getLastFormat(String s) {
        StringBuilder fmt = new StringBuilder();
        for (int i = 0; i < s.length() - 1; i++) {
            if (s.charAt(i) == '\u00A7') {
                char c = Character.toLowerCase(s.charAt(i + 1));
                if ((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || c == 'r') fmt.setLength(0);
                fmt.append('\u00A7').append(c);
            }
        }
        return fmt.toString();
    }
}
