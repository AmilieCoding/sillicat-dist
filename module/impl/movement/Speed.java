package sillicat.module.impl.movement;

import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

@ModuleInfo(
        name = "Speed",
        description = "Strafe around players at speed.",
        category = Category.Movement,
        defaultKey = Keyboard.KEY_M,
        enabled = false
)

public class Speed extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "OnGround", "Bunny");
    private final NumberSetting speed = new NumberSetting("speed", 0.5, 0.1, 5, 0.1);
    private final BooleanSetting noSlow = new BooleanSetting("NoSlow", true);

    public Speed(){
        addSettings(mode, speed, noSlow);
        setKey(getKey());
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null) return;

        if (noSlow.isEnabled() && mc.thePlayer.isSwingInProgress && isMoving()) {
            double motionMultiplier = 1;
            mc.thePlayer.motionX *= motionMultiplier;
            mc.thePlayer.motionZ *= motionMultiplier;
        }

        if(mode.getCurrMode().equalsIgnoreCase("Bunny")) {
            if (mc.thePlayer.isSneaking() || mc.thePlayer.moveForward == 0 && mc.thePlayer.moveStrafing == 0) return;

            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionY += 0.1;
                mc.thePlayer.motionX *= 1.8;
                mc.thePlayer.motionZ *= 1.8;

                double currSped = Math.sqrt(Math.pow(mc.thePlayer.motionX, 2) + Math.pow(mc.thePlayer.motionZ, 2));
                double maxSpeed = speed.getVal();
                if (currSped > maxSpeed) {
                    mc.thePlayer.motionX = mc.thePlayer.motionX / currSped * maxSpeed;
                    mc.thePlayer.motionZ = mc.thePlayer.motionZ / currSped * maxSpeed;
                }
                mc.thePlayer.jump();
            }
        }

        if (mode.getCurrMode().equalsIgnoreCase("OnGround")) {
            if (mc.thePlayer.onGround) {
                if(isMoving()){
                    mc.thePlayer.motionY += 0.1;
                    mc.thePlayer.motionX *= 1.8;
                    mc.thePlayer.motionZ *= 1.8;

                    double currSped = Math.sqrt(Math.pow(mc.thePlayer.motionX, 2) + Math.pow(mc.thePlayer.motionZ, 2));
                    double maxSpeed = speed.getVal();
                    if (currSped > maxSpeed) {
                        mc.thePlayer.motionX = mc.thePlayer.motionX / currSped * maxSpeed;
                        mc.thePlayer.motionZ = mc.thePlayer.motionZ / currSped * maxSpeed;
                    }
                }else{
                    mc.thePlayer.motionX *= 0.4;
                    mc.thePlayer.motionZ *= 0.4;
                }
            }
        }
        super.onUpdate();
    }

    private boolean isMoving(){
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }
}