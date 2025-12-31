package sillicat.module.impl.movement;

import net.minecraft.network.play.client.C03PacketPlayer;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;

@ModuleInfo(
        name = "NoFall",
        description = "Don't get hurt coming down!",
        category = Category.Movement,
        enabled = false
)
public class NoFall extends Module {
    @Override
    public void onUpdate(){
        if(mc.thePlayer == null) return;

        if(mc.thePlayer.fallDistance > 2.5F){
            mc.thePlayer.sendQueue.addToSendQueue(
                    new C03PacketPlayer(true)
            );
        }
    }
}
