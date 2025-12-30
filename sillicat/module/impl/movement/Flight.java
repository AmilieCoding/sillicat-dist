package sillicat.module.impl.movement;

import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;

@ModuleInfo(
        name = "Flight",
        description = "Accelerate at speed through the air.",
        category = Category.Movement,
        enabled = true
)

public class Flight extends Module {

    @Override
    public void onUpdate() {
        if(mc.thePlayer != null){
            mc.thePlayer.capabilities.isFlying = true;

            if(mc.gameSettings.keyBindJump.isPressed()){
                mc.thePlayer.motionY += 0.2;
            }

            if(mc.gameSettings.keyBindSneak.isPressed()){
                mc.thePlayer.motionY -= 0.2;
            }

            if(mc.gameSettings.keyBindForward.isPressed()){
                mc.thePlayer.capabilities.setFlySpeed(0.25f);
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.capabilities.isFlying = false;
    }
}
