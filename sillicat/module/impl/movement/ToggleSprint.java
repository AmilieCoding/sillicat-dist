package sillicat.module.impl.movement;

import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;

@ModuleInfo(
        name = "ToggleSprint",
        description = "Always sprint enabled.",
        category = Category.Movement,
        enabled = true
)
public class ToggleSprint extends Module {
    @Override
    public void onUpdate() {
        mc.gameSettings.keyBindSprint.setPressed(true);
    }
}
