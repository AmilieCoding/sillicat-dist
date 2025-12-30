package sillicat.ui.clickgui;

import net.minecraft.client.gui.FontRenderer;
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

import java.io.IOException;

public class SettingsScreen extends GuiScreen {

    private final FontRenderer fr = Sillicat.INSTANCE.getFr();
    private final Module module;

    private static final int BOX_W = 130;
    private static final int BOX_H = 160;

    private static final int FOOTER_HEIGHT = 11;
    private static final int FOOTER_PADDING = 6;

    public SettingsScreen(Module module) {
        this.module = module;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution sr = new ScaledResolution(mc);

        int boxX = sr.getScaledWidth() / 2 - BOX_W / 2;
        int boxY = sr.getScaledHeight() / 2 - BOX_H / 2;

        RenderUtil.drawRect(boxX, boxY, BOX_W, BOX_H, 0xDD222222);

        fr.drawString(
                module.getName(),
                sr.getScaledWidth() / 2 - fr.getStringWidth(module.getName()) / 2,
                boxY + 2,
                -1
        );

        RenderUtil.drawRect(
                boxX,
                boxY + fr.FONT_HEIGHT + 3,
                BOX_W,
                1,
                0xDDDDDDDD
        );

        int yOffset = boxY + 15;

        for (Setting setting : module.getSettingList()) {

            if (setting instanceof BooleanSetting) {
                BooleanSetting bs = (BooleanSetting) setting;

                fr.drawString(bs.name, boxX + 10, yOffset, -1);

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

                fr.drawString(ms.name, boxX + 10, yOffset, -1);
                fr.drawString("←", boxX + 50, yOffset, -1);
                fr.drawString(ms.getCurrMode(), boxX + 53 + fr.getStringWidth("←"), yOffset, -1);
                fr.drawString("→", boxX + 59 + fr.getStringWidth(ms.getCurrMode()), yOffset, -1);

                yOffset += 15;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                fr.drawString(ns.name, boxX + 10, yOffset, -1);

                fr.drawString("←",
                        boxX + 10 + fr.getStringWidth(ns.name) + 3,
                        yOffset,
                        -1
                );

                String val = String.format("%.1f", ns.getVal());

                fr.drawString(val,
                        boxX + 10 + fr.getStringWidth(ns.name) + 3 + fr.getStringWidth("←") + 3,
                        yOffset,
                        -1
                );

                fr.drawString("→",
                        boxX + 10 + fr.getStringWidth(ns.name) + 3 + fr.getStringWidth("←") + 3 + fr.getStringWidth(val) + 3,
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

        fr.drawString(
                "Back",
                boxX + BOX_W / 2 - fr.getStringWidth("Back") / 2,
                footerY + (FOOTER_HEIGHT - fr.FONT_HEIGHT) / 2,
                -1
        );

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution sr = new ScaledResolution(mc);

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

                if (HoverUtil.isHovered(
                        boxX + 45,
                        yOffset,
                        boxX + 55,
                        yOffset + fr.FONT_HEIGHT,
                        mouseX,
                        mouseY
                )) {
                    ms.cycleBack();
                }

                if (HoverUtil.isHovered(
                        boxX + 55 + fr.getStringWidth(ms.getCurrMode()),
                        yOffset,
                        boxX + 65 + fr.getStringWidth(ms.getCurrMode()),
                        yOffset + fr.FONT_HEIGHT,
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

                if (HoverUtil.isHovered(
                        boxX + 10 + fr.getStringWidth(ns.name) + 2,
                        yOffset,
                        boxX + 10 + fr.getStringWidth(ns.name) + 6,
                        yOffset + fr.FONT_HEIGHT,
                        mouseX,
                        mouseY
                )) {
                    ns.setVal(ns.getVal() - ns.getIncrement());
                }

                if (HoverUtil.isHovered(
                        boxX + 10 + fr.getStringWidth(ns.name) + 3 + fr.getStringWidth("←") + 3 + fr.getStringWidth(val),
                        yOffset,
                        boxX + 10 + fr.getStringWidth(ns.name) + 3 + fr.getStringWidth("←") + 3 + fr.getStringWidth(val) + 6,
                        yOffset + fr.FONT_HEIGHT,
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
