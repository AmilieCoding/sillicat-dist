package sillicat.module.impl.render;

import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.notification.Notification;
import sillicat.notification.NotificationManager;
import sillicat.setting.impl.BindSetting;
import sillicat.util.font.CustomFontRenderer;

@ModuleInfo(
        name = "HUD",
        description = "Primary Heads up Display.",
        category = Category.Render,
        defaultKey = -1,
        enabled = true
)

public class HUD extends Module {

    public HUD(){
        setKey(getKey());
    }

    @Override
    public void on2D(ScaledResolution sr){
        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(24);
        GL11.glPushMatrix();
        GL11.glScaled(1.5, 1.5, 1.5);
        cfr.drawString("Sillicat", 1, 1, -1);
        GL11.glPopMatrix();

        NotificationManager.renderNotifications();
    }

}
