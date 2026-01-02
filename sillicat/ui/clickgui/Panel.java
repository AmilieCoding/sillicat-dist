package sillicat.ui.clickgui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.setting.Setting;
import sillicat.setting.impl.BindSetting;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.util.HoverUtil;
import sillicat.util.RenderUtil;
import sillicat.util.ScissorUtil;

public class Panel {
    final Category category;

    private static final int SETTING_H = 12;
    private static final int SETTING_GAP = 1;
    private static final int SETTING_INSET = 6;

    private BindSetting listeningBind = null;


    // Only 1 module should be expanded at a time (per panel).
    private Module expanded = null;

    private NumberSetting draggingSlider = null;
    private int scroll = 0;

    int x, y, w, h;
    boolean dragging;
    int dragOffX, dragOffY;

    private int headerX() { return x; }
    private int headerY() { return y; }
    private int headerW() { return w; }
    private int headerH() { return ClickGUIConstants.HEADER_H; }

    private int bodyX() { return x; }
    private int bodyY() { return y + ClickGUIConstants.HEADER_H; }
    private int bodyW() { return w; }
    private int bodyH() { return h - ClickGUIConstants.HEADER_H; }

    Panel(Category category, int x, int y, int w, int h) {
        this.category = category;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.dragging = false;
        this.dragOffX = 0;
        this.dragOffY = 0;
    }

    public void render(int mouseX, int mouseY, float partialTicks) {
        Module[] mods = Sillicat.INSTANCE.getModuleManager().getModules(category);

        // Panel background + header
        RenderUtil.drawRect(x, y, w, h, ColorScheme.PANEL_BG.get());
        RenderUtil.drawRect(x, y, w, ClickGUIConstants.HEADER_H, ColorScheme.PANEL_BG_ALT.get());
        ClickGUIConstants.TITLE_FONT.drawString(
                category.name(),
                x + ClickGUIConstants.PAD,
                y + 4,
                ColorScheme.TEXT_PRIMARY.get()
        );

        // Slider dragging updates (value follows mouse while LMB down)
        if (draggingSlider != null) {
            if (Mouse.isButtonDown(0)) {
                setSliderValueFromMouse(draggingSlider, mouseX);
            } else {
                draggingSlider = null;
            }
        }

        int yCursor = bodyY() + ClickGUIConstants.BODY_PAD - scroll;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScissorUtil.scissor(bodyX(), bodyY(), bodyW(), bodyH());

        for (Module m : mods) {
            // Module row
            RenderUtil.drawRect(x + 2, yCursor, w - 4, ClickGUIConstants.ROW_H, ColorScheme.ROW_BG.get());

            if (m.isToggled()) {
                RenderUtil.drawRect(x + 2, yCursor, 2, ClickGUIConstants.ROW_H, ColorScheme.ACCENT.get());
            }

            boolean hovered = HoverUtil.isHovered(
                    x + 2, yCursor,
                    x + w - 2, yCursor + ClickGUIConstants.ROW_H,
                    mouseX, mouseY
            );
            if (hovered) {
                RenderUtil.drawRect(x + 2, yCursor, w - 4, ClickGUIConstants.ROW_H, ColorScheme.ACCENT.alpha(40));
            }

            int textX = x + ClickGUIConstants.BODY_PAD + 3;
            int textY = yCursor + (ClickGUIConstants.ROW_H / 2) - (int)(ClickGUIConstants.TITLE_FONT.getHeight(m.getName()) / 2f);

            ClickGUIConstants.TITLE_FONT.drawString(m.getName(), textX, textY, ColorScheme.TEXT_PRIMARY.get());

            // Expanded marker (right edge)
            if (expanded == m) {
                RenderUtil.drawRect(x + w - 4, yCursor, 2, ClickGUIConstants.ROW_H, ColorScheme.ACCENT.get());
            }

            yCursor += ClickGUIConstants.ROW_H + ClickGUIConstants.ROW_GAP;

            // Inline settings under expanded module
            if (expanded == m) {
                yCursor += renderSettings(m, yCursor, mouseX, mouseY);
            }
        }

        // Content height for scroll clamp (include expanded settings)
        int base = mods.length * (ClickGUIConstants.ROW_H + ClickGUIConstants.ROW_GAP)
                - ClickGUIConstants.ROW_GAP
                + (ClickGUIConstants.BODY_PAD * 2);

        int extra = 0;
        if (expanded != null) {
            int count = expanded.getSettingList().size();
            if (count > 0) extra = count * (SETTING_H + SETTING_GAP) - SETTING_GAP;
        }

        clampScroll(base + extra);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    // Actual click handler (drag header + module rows + settings rows)
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        boolean overHeader = HoverUtil.isHovered(
                headerX(), headerY(),
                headerX() + headerW(), headerY() + headerH(),
                mouseX, mouseY
        );

        if (button == 0 && overHeader) {
            dragging = true;
            dragOffX = mouseX - x;
            dragOffY = mouseY - y;
            return true;
        }

        if (!isMouseOverbody(mouseX, mouseY)) {
            return false;
        }

        int yCursor = bodyY() + ClickGUIConstants.BODY_PAD - scroll;
        Module[] mods = Sillicat.INSTANCE.getModuleManager().getModules(category);

        for (Module m : mods) {
            boolean overRow = HoverUtil.isHovered(
                    x + 2, yCursor,
                    x + w - 2, yCursor + ClickGUIConstants.ROW_H,
                    mouseX, mouseY
            );

            if (overRow) {
                if (button == 0) {
                    m.toggle();
                } else if (button == 1) {
                    expanded = (expanded == m) ? null : m;
                }
                return true;
            }

            yCursor += ClickGUIConstants.ROW_H + ClickGUIConstants.ROW_GAP;

            if (expanded == m) {
                for (Setting s : m.getSettingList()) {
                    boolean overSettingRow = HoverUtil.isHovered(
                            x + SETTING_INSET, yCursor,
                            x + w - SETTING_INSET, yCursor + SETTING_H,
                            mouseX, mouseY
                    );

                    if (overSettingRow) {
                        if (s instanceof BooleanSetting) {
                            BooleanSetting bs = (BooleanSetting) s;
                            bs.setValue(!bs.isEnabled());
                            return true;
                        }

                        if (s instanceof ModeSetting) {
                            ModeSetting ms = (ModeSetting) s;
                            if (button == 0) ms.cycleForwards();
                            else if (button == 1) ms.cycleBack();
                            return true;
                        }

                        if (s instanceof BindSetting) {
                            BindSetting bs = (BindSetting) s;
                            listeningBind = (listeningBind == bs) ? null : bs;
                            return true;
                        }

                        if (s instanceof NumberSetting) {
                            draggingSlider = (NumberSetting) s;
                            setSliderValueFromMouse(draggingSlider, mouseX);
                            return true;
                        }

                        if (s instanceof BindSetting) {
                            // You can hook bind capture here later (Phase C2 bind).
                            return true;
                        }

                        return true;
                    }

                    yCursor += SETTING_H + SETTING_GAP;
                }
            }
        }

        return false;
    }

    public boolean handleBindKey(int keyCode) {
        if (listeningBind == null) return false;

        // ESC = cancel listening
        if (keyCode == org.lwjgl.input.Keyboard.KEY_ESCAPE) {
            listeningBind = null;
            return true;
        }

        // DEL or BACKSPACE = clear bind
        if (keyCode == org.lwjgl.input.Keyboard.KEY_DELETE || keyCode == org.lwjgl.input.Keyboard.KEY_BACK) {
            listeningBind.setKey(0);
            listeningBind = null;
            return true;
        }

        // Otherwise set the key
        listeningBind.setKey(keyCode);
        listeningBind = null;
        return true;
    }

    // Stop dragging on mouse release.
    public void mouseReleased() {
        dragging = false;
    }

    public void updateDrag(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - dragOffX;
            y = mouseY - dragOffY;
        }
    }

    public boolean isMouseOverbody(int mouseX, int mouseY) {
        return HoverUtil.isHovered(
                bodyX(), bodyY(),
                bodyX() + bodyW(), bodyY() + bodyH(),
                mouseX, mouseY
        );
    }

    private void clampScroll(int contentHeight) {
        int viewH = bodyH();
        int maxScroll = Math.max(0, contentHeight - viewH);

        if (scroll < 0) scroll = 0;
        if (scroll > maxScroll) scroll = maxScroll;
    }

    private int renderSettings(Module module, int startY, int mouseX, int mouseY) {
        int y = startY;

        for (Setting setting : module.getSettingList()) {
            RenderUtil.drawRect(
                    x + SETTING_INSET,
                    y,
                    w - (SETTING_INSET * 2),
                    SETTING_H,
                    ColorScheme.PANEL_BG_ALT.get()
            );

            String valueStr = "";

            if (setting instanceof BooleanSetting) {
                BooleanSetting bs = (BooleanSetting) setting;
                valueStr = bs.isEnabled() ? "ON" : "OFF";

            } else if (setting instanceof ModeSetting) {
                ModeSetting ms = (ModeSetting) setting;
                valueStr = ms.getCurrMode();

            } else if (setting instanceof NumberSetting) {
                NumberSetting ns = (NumberSetting) setting;

                double min = ns.getMinVal();
                double max = ns.getMaxVal();
                double val = ns.getVal();

                valueStr = String.format("%.2f", val);

                float nameW = ClickGUIConstants.SMALL_FONT.getWidth(setting.getName());
                float valueW = ClickGUIConstants.SMALL_FONT.getWidth(valueStr);

                // padding between text and bar
                int pad = 6;

                // bar starts after label, ends before value
                int barX = x + SETTING_INSET;
                int barY = y;
                int barW = w - (SETTING_INSET * 2);
                int barH = SETTING_H;

                double t = (val - min) / (max - min);
                if (t < 0) t = 0;
                if (t > 1) t = 1;

                RenderUtil.drawRect(barX, barY, barW, barH, ColorScheme.ROW_BG.get());
                RenderUtil.drawRect(barX, barY, (int) (barW * t), barH, ColorScheme.ACCENT.get());

                ClickGUIConstants.SMALL_FONT.drawString(
                        setting.getName(),
                        x + SETTING_INSET + 2,
                        y + 2,
                        ColorScheme.TEXT_SECONDARY.get()
                );

                // draw value right
                ClickGUIConstants.SMALL_FONT.drawString(
                        valueStr,
                        x + w - SETTING_INSET - 2 - valueW,
                        y + 2,
                        ColorScheme.TEXT_PRIMARY.get()
                );


            } else if (setting instanceof BindSetting) {
                BindSetting bs = (BindSetting) setting;

                if (listeningBind == bs) {
                    valueStr = "Press a key...";
                } else {
                    int key = bs.getKey();
                    valueStr = key == 0 ? "NONE" : org.lwjgl.input.Keyboard.getKeyName(key);
                }
            }

            // Left label
            ClickGUIConstants.SMALL_FONT.drawString(
                    setting.getName(),
                    x + SETTING_INSET + 2,
                    y + 2,
                    ColorScheme.TEXT_SECONDARY.get()
            );

            // Right value (keep it to the left of the slider region)
            float vw = ClickGUIConstants.SMALL_FONT.getWidth(valueStr);
            float rightX = x + w - SETTING_INSET - 2 - vw;

            ClickGUIConstants.SMALL_FONT.drawString(
                    valueStr,
                    rightX,
                    y + 2,
                    ColorScheme.TEXT_PRIMARY.get()
            );

            y += SETTING_H + SETTING_GAP;
        }

        return y - startY;
    }

    private void setSliderValueFromMouse(NumberSetting ns, int mouseX) {
        double min = ns.getMinVal();
        double max = ns.getMaxVal();

        int barX = x + SETTING_INSET;
        int barW = w - (SETTING_INSET * 2);

        double t = (mouseX - barX) / (double) barW;
        if (t < 0) t = 0;
        if (t > 1) t = 1;

        double raw = min + (max - min) * t;

        double inc = ns.getIncrement();
        if (inc > 0) raw = Math.round(raw / inc) * inc;

        ns.setVal(raw);
    }

    public void clearListening() {
        listeningBind = null;
        draggingSlider = null; // nice to clear too
    }
}
