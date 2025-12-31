package sillicat.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
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

        for (Module mod : Sillicat.INSTANCE.getModuleManager().getModules().values().stream().filter(Module::isToggled).sorted(Comparator.<Module>comparingInt(m -> (int) (cfr.getWidth(m.getName()) + (m.getName().length() * 2))).reversed().thenComparing(Module::getName, String.CASE_INSENSITIVE_ORDER)).toArray(Module[]::new)) {

            if (!mod.getName().equalsIgnoreCase("clickgui") && !mod.getName().equalsIgnoreCase("hud") && !mod.getName().equalsIgnoreCase("arraylist")) {

                int w = (int) cfr.getWidth(mod.getName());
                int h = (int) cfr.getHeight(mod.getName());

                RenderUtil.drawRect(sr.getScaledWidth() - w - 9, offset, 2, 6 + h, 0xFF48BDFA);
                RenderUtil.drawRect(sr.getScaledWidth() - w - 7, offset, w + 8, 6 + h, 0x90000000);
                cfr.drawString(mod.getName(), sr.getScaledWidth() - w - 3, 4 + offset, -1);

                offset += 15;
            }
        }
    }
}
