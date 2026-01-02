package sillicat.module.impl.render;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.BindSetting;
import sillicat.setting.impl.ModeSetting;

@ModuleInfo(
        name = "Fullbright",
        description = "Fixes your sight.",
        category = Category.Render,
        defaultKey = -1,
        enabled = false
)
public class Fullbright extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Gamma", "Night Vision");


    public Fullbright(){
        addSettings(mode);
        setKey(getKey());
    }

    private float oldBright;
    private int nv;

    @Override
    public void onEnable() {
        float oldBright = mc.gameSettings.gammaSetting;

        super.onEnable();
    }

    @Override
    public void onUpdate() {

        if(mode.getCurrMode().equalsIgnoreCase("Gamma")){
            mc.gameSettings.gammaSetting = 100f;
        }

        if(mode.getCurrMode().equalsIgnoreCase("Night Vision")){
            int nv = Potion.nightVision.getId();
            mc.thePlayer.addPotionEffect(new PotionEffect(nv, 999999, 0, false, false));
        }

        super.onUpdate();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.gammaSetting = oldBright;
        mc.thePlayer.removePotionEffect(nv);

        super.onDisable();
    }
}
