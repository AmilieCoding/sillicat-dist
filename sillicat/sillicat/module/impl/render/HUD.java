package sillicat.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;

@ModuleInfo(
        name = "HUD",
        description = "Primary Heads up Display.",
        category = Category.Render,
        enabled = true
)

public class HUD extends Module {
    @Override
    public void on2D(ScaledResolution sr){
        GL11.glPushMatrix();
        GL11.glScaled(1.5, 1.5, 1.5);
        fr.drawString("Sillicat", 1, 1, -1);
        GL11.glPopMatrix();
    }

}
