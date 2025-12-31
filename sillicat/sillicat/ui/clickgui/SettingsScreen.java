package sillicat.ui.clickgui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Module;
import sillicat.setting.Setting;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;
import sillicat.util.HoverUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

import java.io.IOException;

public class SettingsScreen extends GuiScreen {

    private final Module module;

    private static final int BOX_W = 130;
    private static final int BOX_H = 160;

    private static final int FOOTER_HEIGHT = 11;
    private static final int FOOTER_PADDING = 6;

    public SettingsScreen(Module module) {
        this.module = module;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);
        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);

        if (cfr == null) {
            super.drawScreen(mouseX, mouseY, partialTicks);
            return;
        }

        int fontH = (int) cfr.getHeight("A");

        int boxX = sr.getScaledWidth() / 2 - BOX_W / 2;
        int boxY = sr.getScaledHeight() / 2 - BOX_H / 2;

        RenderUtil.drawRect(boxX, boxY, BOX_W, BOX_H, 0xDD222222);

        cfr.drawString(
                module.getName(),
                (float) sr.getScaledWidth() / 2 - cfr.getWidth(module.getName()) / 2f,
                boxY + 2,
                -1
        );

        RenderUtil.drawRect(
                boxX,
                boxY + fontH + 3,
                BOX_W,
                1,
                0xDDDDDDDD
        );

        int yOffset = boxY + 15;

        for (Setting setting : module.getSettingList()) {

            if (setting instanceof BooleanSetting) {
                BooleanSetting bs = (BooleanSetting) setting;

                cfr.drawString(bs.name, boxX + 10, yOffset, -1);

                RenderUtil.drawHollowRect(
                        boxX + BOX_W - 20,
                        yOffset,
                        5,
                        5,
                        1,
                        -1
                );

                RenderUtil.drawRect(
                        boxX + BOX_W - 19,
                        yOffset + 1,
                        4,
                        4,
                        bs.isState() ? 0xFF3FF34F : 0x00FFFFFF
                );

                yOffset += 10;
            }

            if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting) setting;

                cfr.drawString(ms.name, boxX + 10, yOffset, -1);

                cfr.drawString("<", boxX + 50, yOffset, -1);
                cfr.drawString(ms.getCurrMode(), boxX + 53 + cfr.getWidth("<"), yOffset, -1);
                cfr.drawString(">", boxX + 59 + cfr.getWidth(ms.getCurrMode()), yOffset, -1);

                yOffset += 15;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                cfr.drawString(ns.name, boxX + 10, yOffset, -1);

                cfr.drawString("<",
                        boxX + 10 + cfr.getWidth(ns.name) + 3,
                        yOffset,
                        -1
                );

                String val = String.format("%.1f", ns.getVal());

                cfr.drawString(val,
                        boxX + 10 + cfr.getWidth(ns.name) + 3 + cfr.getWidth("←") + 3,
                        yOffset,
                        -1
                );

                cfr.drawString(">",
                        boxX + 10 + cfr.getWidth(ns.name) + 3 + cfr.getWidth("←") + 3 + cfr.getWidth(val) + 3,
                        yOffset,
                        -1
                );

                yOffset += 10;
            }
        }

        int footerY = boxY + BOX_H + FOOTER_PADDING;

        RenderUtil.drawRect(
                boxX,
                footerY,
                BOX_W,
                FOOTER_HEIGHT,
                0xFFF33F3F
        );

        cfr.drawString(
                "Back",
                boxX + BOX_W / 2f - cfr.getWidth("Back") / 2f,
                footerY + (FOOTER_HEIGHT - fontH) / 2f,
                -1
        );

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);
        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);

        if (cfr == null) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        int fontH = (int) cfr.getHeight("A");

        int boxX = sr.getScaledWidth() / 2 - BOX_W / 2;
        int boxY = sr.getScaledHeight() / 2 - BOX_H / 2;

        int footerY = boxY + BOX_H + FOOTER_PADDING;

        if (HoverUtil.isHovered(
                boxX,
                footerY,
                boxX + BOX_W,
                footerY + FOOTER_HEIGHT,
                mouseX,
                mouseY
        )) {
            mc.displayGuiScreen(new ClickGUIScreen());
            return;
        }

        int yOffset = boxY + 15;

        for (Setting setting : module.getSettingList()) {

            if (setting instanceof BooleanSetting) {
                BooleanSetting bs = (BooleanSetting) setting;

                if (HoverUtil.isHovered(
                        boxX + BOX_W - 20,
                        yOffset,
                        boxX + BOX_W - 15,
                        yOffset + 5,
                        mouseX,
                        mouseY
                )) {
                    bs.setState(!bs.isState());
                }

                yOffset += 10;
            }

            if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting) setting;

                // left arrow
                int leftArrowX = boxX + 50;
                int leftArrowW = (int) cfr.getWidth("<");

                if (HoverUtil.isHovered(
                        leftArrowX,
                        yOffset,
                        leftArrowX + leftArrowW,
                        yOffset + fontH,
                        mouseX,
                        mouseY
                )) {
                    ms.cycleBack();
                }

                // right arrow (placed after mode text)
                int modeX = boxX + 53 + leftArrowW;
                int modeW = (int) cfr.getWidth(ms.getCurrMode());

                int rightArrowX = boxX + 59 + modeW;
                int rightArrowW = (int) cfr.getWidth(">");

                if (HoverUtil.isHovered(
                        rightArrowX,
                        yOffset,
                        rightArrowX + rightArrowW,
                        yOffset + fontH,
                        mouseX,
                        mouseY
                )) {
                    ms.cycleForwards();
                }

                yOffset += 15;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                String val = String.format("%.1f", ns.getVal());

                int nameW = (int) cfr.getWidth(ns.name);
                int leftArrowX = boxX + 10 + nameW + 3;
                int leftArrowW = (int) cfr.getWidth("<");

                if (HoverUtil.isHovered(
                        leftArrowX,
                        yOffset,
                        leftArrowX + leftArrowW,
                        yOffset + fontH,
                        mouseX,
                        mouseY
                )) {
                    ns.setVal(ns.getVal() - ns.getIncrement());
                }

                int valX = leftArrowX + leftArrowW + 3;
                int valW = (int) cfr.getWidth(val);

                int rightArrowX = valX + valW + 3;
                int rightArrowW = (int) cfr.getWidth(">");

                if (HoverUtil.isHovered(
                        rightArrowX,
                        yOffset,
                        rightArrowX + rightArrowW,
                        yOffset + fontH,
                        mouseX,
                        mouseY
                )) {
                    ns.setVal(ns.getVal() + ns.getIncrement());
                }

                yOffset += 10;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
