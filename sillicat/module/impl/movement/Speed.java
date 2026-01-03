package sillicat.module.impl.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.module.ModuleManager;
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
    public Minecraft mc = Minecraft.getMinecraft();

    private final ModeSetting mode = new ModeSetting("Mode", "OnGround", "Bunny");
    private final ModeSetting modeBMC = new ModeSetting("Mode (BMC)", "BlocksGround", "BlocksBunny");
    private final ModeSetting server = new ModeSetting("Server", "BlocksMC", "Vanilla");
    private final NumberSetting speed = new NumberSetting("speed", 0.5, 0.1, 5, 0.1);
    private final BooleanSetting noSlow = new BooleanSetting("NoSlow", true);

    public Speed(){
        mode.setVisible(() -> server.getCurrMode().trim().equalsIgnoreCase("Vanilla"));
        modeBMC.setVisible(() -> server.getCurrMode().trim().equalsIgnoreCase("BlocksMC"));

        addSettings(mode, modeBMC, server, speed, noSlow);
        setKey(getKey());
    }

    private int jumpCooldown = 0;

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null) return;

        if(server.getCurrMode().equalsIgnoreCase("Vanilla")) {
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
        }

        // Verus
        if(server.getCurrMode().equalsIgnoreCase("BlocksMC")) {
            if (modeBMC.getCurrMode().equalsIgnoreCase("BlocksGround")) {
                int groundTicks = 0;

                if (mc.thePlayer.onGround) {
                    groundTicks++;

                    mc.thePlayer.motionX *= 1.15;
                    mc.thePlayer.motionZ *= 1.15;

                    if (groundTicks > 2) { // Wait a few ticks on ground
                        mc.thePlayer.motionY = 0.0001; // Barely leave ground
                        groundTicks = 0;
                    }
                } else {
                    groundTicks = 0; // Reset when in air
                }
            }
        }
        super.onUpdate();
    }

    private boolean isMoving(){
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }
}