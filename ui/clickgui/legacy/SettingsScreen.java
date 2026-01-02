package sillicat.ui.clickgui.legacy;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;
import sillicat.Sillicat;
import sillicat.module.Module;
import sillicat.setting.Setting;
import sillicat.setting.impl.BindSetting;
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

    // bind capture state
    private BindSetting listeningBind = null;

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

                cfr.drawString(bs.getName(), boxX + 10, yOffset, -1);

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
                        bs.isEnabled() ? 0xFF3FF34F : 0x00FFFFFF
                );

                yOffset += 10;
            }

            if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting) setting;

                cfr.drawString(ms.getName(), boxX + 10, yOffset, -1);

                cfr.drawString("<", boxX + 50, yOffset, -1);
                cfr.drawString(ms.getCurrMode(), boxX + 53 + cfr.getWidth("<"), yOffset, -1);
                cfr.drawString(">", boxX + 59 + cfr.getWidth(ms.getCurrMode()), yOffset, -1);

                yOffset += 15;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                cfr.drawString(ns.getName(), boxX + 10, yOffset, -1);

                cfr.drawString("<",
                        boxX + 10 + cfr.getWidth(ns.getName()) + 3,
                        yOffset,
                        -1
                );

                String val = String.format("%.1f", ns.getVal());

                cfr.drawString(val,
                        boxX + 10 + cfr.getWidth(ns.getName()) + 3 + cfr.getWidth("←") + 3,
                        yOffset,
                        -1
                );

                cfr.drawString(">",
                        boxX + 10 + cfr.getWidth(ns.getName()) + 3 + cfr.getWidth("←") + 3 + cfr.getWidth(val) + 3,
                        yOffset,
                        -1
                );

                yOffset += 10;
            }

            if (setting instanceof BindSetting) {
                BindSetting bs = (BindSetting) setting;

                cfr.drawString(bs.getName(), boxX + 10, yOffset, -1);

                String keyText;
                if (listeningBind == bs) {
                    keyText = "Press a key...";
                } else {
                    int k = bs.getKey();
                    keyText = (k <= 0) ? "None" : Keyboard.getKeyName(k);
                }

                float keyW = cfr.getWidth(keyText);
                float keyX = boxX + BOX_W - 10 - keyW;

                cfr.drawString(keyText, keyX, yOffset, -1);

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
            listeningBind = null;
            mc.displayGuiScreen(new ClickGUIScreenLegacy());
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
                    bs.setValue(!bs.isEnabled());
                    Sillicat.INSTANCE.getConfigManager().saveConfig();
                }

                yOffset += 10;
            }

            if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting) setting;

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
                    Sillicat.INSTANCE.getConfigManager().saveConfig();
                }

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
                    Sillicat.INSTANCE.getConfigManager().saveConfig();
                }

                yOffset += 15;
            }

            if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                String val = String.format("%.1f", ns.getVal());

                int nameW = (int) cfr.getWidth(ns.getName());
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
                    Sillicat.INSTANCE.getConfigManager().saveConfig();
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
                    Sillicat.INSTANCE.getConfigManager().saveConfig();
                }

                yOffset += 10;
            }

            if (setting instanceof BindSetting) {
                BindSetting bs = (BindSetting) setting;

                // Click anywhere on the row to start listening
                if (HoverUtil.isHovered(
                        boxX + 5,
                        yOffset,
                        boxX + BOX_W - 5,
                        yOffset + fontH,
                        mouseX,
                        mouseY
                )) {
                    listeningBind = bs;
                }

                yOffset += 10;
            }
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (listeningBind != null) {

            // ESC cancels capture
            if (keyCode == Keyboard.KEY_ESCAPE) {
                listeningBind = null;
                return;
            }

            // DEL/BACK clears bind
            if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_BACK) {
                listeningBind.setKey(-1);
                // if your BindSetting does NOT delegate to module, also do:
                // module.setKey(-1);
                Sillicat.INSTANCE.getConfigManager().saveConfig();
                listeningBind = null;
                return;
            }

            listeningBind.setKey(keyCode);
            // if your BindSetting does NOT delegate to module, also do:
            // module.setKey(keyCode);

            Sillicat.INSTANCE.getConfigManager().saveConfig();
            listeningBind = null;
            return;
        }

        super.keyTyped(typedChar, keyCode);
    }
}
