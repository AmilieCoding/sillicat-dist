package sillicat.module.impl.settings;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.ui.designLanguage.Theme;
import sillicat.util.AnimationUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(
        name = "ArrayList",
        description = "Show active modules in a neat way!",
        category = Category.Settings,
        defaultKey = -1,
        enabled = true
)
public class ArrayList extends Module {

    public ArrayList(){
        setKey(getKey());
    }

    private final Map<String, AnimationUtil> anims = new HashMap<>();
    private final Map<String, Boolean> lastToggled = new HashMap<>();

    private static final float ROW_H = 15f;
    private static final float SLIDE_PAD = 14f;

    @Override
    public void on2D(ScaledResolution sr) {
        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);
        float pt = mc.timer.renderPartialTicks;

        // Update anims for every module every frame
        for (Module m : Sillicat.INSTANCE.getModuleManager().getModules().values()) {
            String key = m.getName().toLowerCase();

            AnimationUtil anim = anims.get(key);
            if (anim == null) {
                anim = new AnimationUtil(0f, 0.07f, AnimationUtil.Easing.EASE_OUT_BACK);
                anims.put(key, anim);
            }

            boolean toggled = m.isToggled();
            boolean wasToggled = lastToggled.getOrDefault(key, false);

            anim.setTarget(toggled ? 1f : 0f);
            anim.update(pt);

            lastToggled.put(key, toggled);
        }

        float offset = 0f;

        for (Module mod : Sillicat.INSTANCE.getModuleManager().getModules().values().stream()
                .filter(m -> !m.getName().equalsIgnoreCase("clickgui")
                        && !m.getName().equalsIgnoreCase("hud")
                        && !m.getName().equalsIgnoreCase("arraylist"))
                .sorted(Comparator.<Module>comparingInt(m -> {
                    ModeSetting ms = (ModeSetting) m.getSettingList().stream()
                            .filter(s -> s instanceof ModeSetting && s.getName().equalsIgnoreCase("Mode"))
                            .findFirst().orElse(null);
                    String modeText = ms != null ? " " + ms.getCurrMode() : "";
                    return (int) (cfr.getWidth(m.getName()) + cfr.getWidth(modeText));
                }).reversed())
                .toArray(Module[]::new)) {

            AnimationUtil anim = anims.get(mod.getName().toLowerCase());
            float t = anim != null ? anim.getValue() : (mod.isToggled() ? 1f : 0f);

            // draw while animating in/out
            if (t <= 0.01f) continue;

            ModeSetting ms = (ModeSetting) mod.getSettingList().stream()
                    .filter(s -> s instanceof ModeSetting && s.getName().equalsIgnoreCase("Mode"))
                    .findFirst().orElse(null);

            String modeText = ms != null ? " " + ms.getCurrMode() : "";
            String name = mod.getName();

            int nameW = (int) cfr.getWidth(name);
            int modeW = (int) cfr.getWidth(modeText);
            int h = (int) cfr.getHeight(name);
            int totalW = nameW + modeW;

            float baseTextX = sr.getScaledWidth() - totalW - 3;

            // Slide: offscreen when t=0, settled when t=1
            float slide = (1f - t) * (totalW + SLIDE_PAD);
            float textX = baseTextX + slide;

            float y = offset;

            float barX = textX - 6;
            float bgX  = textX - 4;

            RenderUtil.drawRect((int) barX, (int) y, 2, 6 + h, Theme.getAccent());
            RenderUtil.drawRect((int) bgX,  (int) y, totalW + 8, 6 + h, ColorScheme.STANDARD_BG.get());

            cfr.drawString(name, textX, 3 + y, ColorScheme.TEXT_PRIMARY.get());
            cfr.drawString(modeText, textX + nameW, 3 + y, ColorScheme.TEXT_SECONDARY.get());

            offset += ROW_H * (mod.isToggled() ? 1f : t);
        }
    }
}
