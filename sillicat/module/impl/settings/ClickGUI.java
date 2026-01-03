package sillicat.module.impl.settings;

import net.minecraft.util.ResourceLocation;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;

import java.util.HashMap;
import java.util.Map;

@ModuleInfo(
        name = "ClickGUI",
        description = "Customise the ClickGUI",
        category = Category.Settings,
        enabled = true
)
public class ClickGUI extends Module {

    public final ModeSetting waifu = new ModeSetting("Waifu", "None", "Astolfo", "Chika", "Mai");

    public static final Map<String, ResourceLocation> WAIFU_TEX = new HashMap<String, ResourceLocation>() {{
        put("None", null);
        put("Astolfo", new ResourceLocation("minecraft", "waifus/astolfo.png"));
        put("Chika",   new ResourceLocation("minecraft", "waifus/chika.png"));
        put("Mai",   new ResourceLocation("minecraft", "waifus/mai.png"));
    }};

    public ClickGUI() {
        addSettings(waifu);
    }
}
