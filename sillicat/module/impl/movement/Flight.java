package sillicat.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
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
    private final ModeSetting server = new ModeSetting("Server", "Vanilla", "BlocksMC");

    public Flight(){
        addSettings(mode, speed, server);
        setKey(Keyboard.KEY_G);
    }

    private double oldY;
    private float oldJ;

    private long lastC04Time = 0;

    @Override
    public void onUpdate() {
        if(mc.thePlayer != null){
            oldY = mc.thePlayer.motionY;
            oldJ = mc.thePlayer.jumpMovementFactor;
            if(server.getCurrMode().equalsIgnoreCase("Vanilla")){
                if(mode.getCurrMode().equalsIgnoreCase("Creative")) {
                    mc.thePlayer.capabilities.isFlying = true;

                    if (mc.gameSettings.keyBindJump.isPressed()) {
                        mc.thePlayer.motionY += 0.2;
                    }

                    if (mc.gameSettings.keyBindSneak.isPressed()) {
                        mc.thePlayer.motionY -= 0.2;
                    }

                    if (mc.gameSettings.keyBindForward.isPressed()) {
                        mc.thePlayer.capabilities.setFlySpeed((float) (speed.getVal() / 20 % .1f));
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

                        long now = System.currentTimeMillis();

                        if(mc.thePlayer.ticksExisted % 5 == 0){
                            mc.thePlayer.sendQueue.addToSendQueue(
                                    new C03PacketPlayer.C04PacketPlayerPosition(
                                            mc.thePlayer.posX, mc.thePlayer.posY - 0.6F, mc.thePlayer.posZ, true)
                            );
                            if(mc.thePlayer.fallDistance > 2.5F) {
                                mc.thePlayer.sendQueue.addToSendQueue(
                                        new C03PacketPlayer(true)
                                );
                            }

                            lastC04Time = now;
                        }
                    }
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
