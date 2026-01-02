package sillicat.module.impl.movement;

import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.BindSetting;

@ModuleInfo(
        name = "ToggleSprint",
        description = "Always sprint enabled.",
        category = Category.Movement,
        defaultKey = -1,
        enabled = false
)
public class ToggleSprint extends Module {

    public ToggleSprint(){
        setKey(getKey());
    }

    @Override
    public void onUpdate() {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }
}
