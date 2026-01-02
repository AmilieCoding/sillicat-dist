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
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.NumberSetting;
import sillicat.ui.designLanguage.Theme;
import sillicat.util.font.CustomFontRenderer;

@ModuleInfo(
        name = "HUD",
        description = "Primary Heads up Display.",
        category = Category.Render,
        defaultKey = -1,
        enabled = true
)

public class HUD extends Module {
    private final BooleanSetting dynamicAccent = new BooleanSetting("Dynamic Accent", true);
    // Color settings.
    private final NumberSetting hue = new NumberSetting("Hue", 300, 0, 360, 1);
    private final NumberSetting sat = new NumberSetting("Saturation", 70, 0, 100, 1);
    private final NumberSetting bri = new NumberSetting("Brightness", 76, 0, 100, 1);
    private final NumberSetting alpha = new NumberSetting("Alpha", 255, 0, 255, 1);

    public HUD(){
        addSettings(dynamicAccent, hue, sat, bri, alpha);
        setKey(getKey());
    }

    private int accentFromHSB() {
        float h = (float) (hue.getVal() / 360.0);
        float s = (float) (sat.getVal() / 100.0);
        float b = (float) (bri.getVal() / 100.0);

        int rgb = java.awt.Color.HSBtoRGB(h, s, b) & 0x00FFFFFF;
        int a = ((int) alpha.getVal() & 0xFF) << 24;

        return a | rgb;
    }

    @Override
    public void on2D(ScaledResolution sr){
        if (dynamicAccent.isEnabled()) {
            Theme.setAccent(accentFromHSB());
        }

        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(24);
        GL11.glPushMatrix();
        GL11.glScaled(1.5, 1.5, 1.5);
        cfr.drawString("Sillicat", 1, 1, -1);
        GL11.glPopMatrix();

        NotificationManager.renderNotifications();
    }

}
