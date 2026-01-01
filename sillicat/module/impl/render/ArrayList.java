package sillicat.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

import java.util.Comparator;

@ModuleInfo(
        name = "ArrayList",
        description = "Show active modules in a neat way!",
        category = Category.Render,
        enabled = true
)
public class ArrayList extends Module {
    @Override
    public void on2D(ScaledResolution sr) {
        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);
        float offset = 0;

        // Sorting with module modes taken into account.
        for (Module mod : Sillicat.INSTANCE.getModuleManager().getModules().values().stream().filter(Module::isToggled).filter(m -> !m.getName().equalsIgnoreCase("clickgui")
                        && !m.getName().equalsIgnoreCase("hud")
                        && !m.getName().equalsIgnoreCase("arraylist")).sorted(Comparator.<Module>comparingInt(m -> {
                            ModeSetting ms = (ModeSetting) m.getSettingList().stream().filter(s -> s instanceof ModeSetting && s.getName().equalsIgnoreCase("Mode")).findFirst().orElse(null);

                    String modeText = ms != null ? " " + ms.getCurrMode() : "";
                    return (int) (cfr.getWidth(m.getName()) + cfr.getWidth(modeText));}).reversed()).toArray(Module[]::new)) {

            if (mod.getName().equalsIgnoreCase("clickgui") || mod.getName().equalsIgnoreCase("hud") || mod.getName().equalsIgnoreCase("arraylist")) {
                continue;
            }

            ModeSetting ms = (ModeSetting) mod.getSettingList().stream().filter(s -> s instanceof ModeSetting && s.getName().equalsIgnoreCase("Mode")).findFirst().orElse(null);

            String modeText = ms != null ? " " + ms.getCurrMode() : "";
            String name = mod.getName();

            int nameW = (int) cfr.getWidth(name);
            int modeW = (int) cfr.getWidth(modeText);
            int h = (int) cfr.getHeight(name);
            int totalW = nameW + modeW;

            RenderUtil.drawRect(sr.getScaledWidth() - totalW - 9, offset, 2, 6 + h, 0xFFC226FF);
            RenderUtil.drawRect(sr.getScaledWidth() - totalW - 7, offset, totalW + 8, 6 + h, 0x90000000);

            float x = sr.getScaledWidth() - totalW - 3;
            cfr.drawString(name, x, 3 + offset, 0xFFFFFFFF);
            cfr.drawString(modeText, x + nameW, 3 + offset, 0xFFAAAAAA);

            offset += 15;
        }
    }
}

