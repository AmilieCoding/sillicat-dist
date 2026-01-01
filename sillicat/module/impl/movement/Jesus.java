package sillicat.module.impl.movement;

import net.minecraft.util.Vec3;
import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;

@ModuleInfo(
        name = "Jesus",
        description = "Walk on water!",
        category = Category.Movement,
        enabled = false
)

public class Jesus extends Module {
    public Jesus(){
        setKey(Keyboard.KEY_J);
    }

    @Override
    public void onUpdate() {
        if (mc.thePlayer == null) return;

        if (mc.thePlayer.isInWater()) {
            mc.thePlayer.motionY = 10.0;
            mc.thePlayer.onGround = true;      // helps stop sinking client-side
            mc.thePlayer.fallDistance = 0.0F;  // avoids fall damage weirdness if you bob
        }
    }
}
