package sillicat.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.ModeSetting;

@ModuleInfo(
        name = "Jesus",
        description = "Walk on water!",
        category = Category.Movement,
        enabled = false
)

public class Jesus extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Walk", "Bounce");

    public Jesus(){
        addSettings(mode);
        setKey(Keyboard.KEY_J);
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null) return;

        if(mode.getCurrMode().equalsIgnoreCase("Bounce")){
            if (mc.thePlayer.isInWater()) {
                mc.thePlayer.motionY = 10.0;
                mc.thePlayer.onGround = true;
                mc.thePlayer.fallDistance = 0.0F;
            }
            if(mode.getCurrMode().equalsIgnoreCase("Walk")){
                if(mc.thePlayer.isInWater() || mc.thePlayer.isInLava()){
                    if(mc.thePlayer.ticksExisted % 5 == 0){
                        mc.thePlayer.sendQueue.addToSendQueue(
                                new C03PacketPlayer(true)
                        );
                    }
                }
            }
        }
    }
}
