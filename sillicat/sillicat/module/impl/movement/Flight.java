package sillicat.module.impl.movement;

import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

@ModuleInfo(
        name = "Flight",
        description = "Accelerate at speed through the air.",
        category = Category.Movement,
        enabled = false
)

public class Flight extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Creative", "Jetpack", "Glide");
    private final NumberSetting speed = new NumberSetting("Speed", 1, 0.1, 10, 0.1);

    public Flight(){
        addSettings(mode, speed);
        setKey(Keyboard.KEY_G);
    }

    private double oldY;
    private float oldJ;

    @Override
    public void onUpdate() {
        if(mc.thePlayer != null){
            oldY = mc.thePlayer.motionY;
            oldJ = mc.thePlayer.jumpMovementFactor;

            if(mode.getCurrMode().equalsIgnoreCase("Creative")){
                mc.thePlayer.capabilities.isFlying = true;

                if(mc.gameSettings.keyBindJump.isPressed()){
                    mc.thePlayer.motionY += 0.2;
                }

                if(mc.gameSettings.keyBindSneak.isPressed()){
                    mc.thePlayer.motionY -= 0.2;
                }

                if(mc.gameSettings.keyBindForward.isPressed()){
                    mc.thePlayer.capabilities.setFlySpeed((float) (speed.getVal() / 20%.1f));
                }
            }

            if(mode.getCurrMode().equalsIgnoreCase("Jetpack")){
                if(mc.gameSettings.keyBindJump.isPressed()){
                    mc.thePlayer.jump();
                }
            }

            if(mode.getCurrMode().equalsIgnoreCase("Glide")){
                if((mc.thePlayer.motionY < 0.0D) && (mc.thePlayer.isAirBorne) && (!mc.thePlayer.isInWater()) && (!mc.thePlayer.isInLava()) && (!mc.thePlayer.isOnLadder())){
                    mc.thePlayer.motionY = -.125D;
                    mc.thePlayer.jumpMovementFactor *= 1.12337f;
                }
            }
        }
    }

    @Override
    public void onDisable() {
        mc.thePlayer.motionY = oldY;
        mc.thePlayer.jumpMovementFactor = oldJ;
        mc.thePlayer.capabilities.isFlying = false;
    }
}
