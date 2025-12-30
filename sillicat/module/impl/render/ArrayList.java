package sillicat.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.util.RenderUtil;

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
        float offset = 0;

        for(Object module : Sillicat.INSTANCE.getModuleManager().getModules().values().stream().filter(Module::isToggled).sorted(Comparator.comparingInt((Module m) -> m.getName().length()).thenComparing(Module::getName).reversed()).toArray()) {
            Module mod = (Module) module;
            if(!mod.getName().equalsIgnoreCase("clickgui") && !mod.getName().equalsIgnoreCase("hud") && !mod.getName().equalsIgnoreCase("arraylist")){
                RenderUtil.drawRect(sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - 9, offset, 2, 6 + fr.FONT_HEIGHT, 0xFF48BDFA);
                RenderUtil.drawRect(sr.getScaledWidth() - fr .getStringWidth(mod.getName()) - 7, offset, fr.getStringWidth(mod.getName()) + 8, 6 + fr.FONT_HEIGHT, 0x90000000);
                fr.drawString(mod.getName(), sr.getScaledWidth() - fr.getStringWidth(mod.getName()) - 3, 4 + offset, -1, false);

                offset += 15;
            }
        };
    }
}
