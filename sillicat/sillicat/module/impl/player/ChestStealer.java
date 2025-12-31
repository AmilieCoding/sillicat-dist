package sillicat.module.impl.player;

import net.minecraft.client.gui.inventory.GuiChest;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

@ModuleInfo(
        name = "ChestStealer",
        description = "Remove items from chests efficiently.",
        category = Category.Player,
        enabled = false
)

public class ChestStealer extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Steal", "Stash");
    public static final NumberSetting speed = new NumberSetting("Speed (ms)", 100, 5, 100, 5);

    public ChestStealer(){
        addSettings(mode, speed);
    }

    @Override
    public void onUpdate() {
        if(mc.thePlayer != null && mc.currentScreen instanceof GuiChest){
            if(mode.getCurrMode().equalsIgnoreCase("Steal")) {
                GuiChest.INSTANCE.steal();
            }if(mode.getCurrMode().equalsIgnoreCase("Stash")){
                GuiChest.INSTANCE.stash();
            }
        }
        super.onUpdate();
    }
}
