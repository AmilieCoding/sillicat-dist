package sillicat.module.impl.render;

import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.NumberSetting;

@ModuleInfo(
        name = "Notifications",
        description = "Customize notification popups.",
        category = Category.Render,
        enabled = true
)
public class Notifications extends Module {

    public final NumberSetting width  = new NumberSetting("Notif Width", 105, 70, 220, 5);
    public final NumberSetting height = new NumberSetting("Notif Height", 30, 18, 80, 1);
    public final NumberSetting gap    = new NumberSetting("Notif Gap", 2, 0, 10, 1);

    public final NumberSetting radius = new NumberSetting("Notif Radius", 5, 0, 12, 1);

    public final NumberSetting titleSize = new NumberSetting("Title Size", 20, 12, 28, 1);
    public final NumberSetting textSize  = new NumberSetting("Text Size", 18, 10, 24, 1);

    public final NumberSetting barH = new NumberSetting("Bar Height", 2, 1, 6, 1);
    public final NumberSetting rightMargin  = new NumberSetting("Right Margin", 2, 0, 20, 1);
    public final NumberSetting bottomMargin = new NumberSetting("Bottom Margin", 2, 0, 20, 1);

    public Notifications() {
        addSettings(width, height, gap, radius, titleSize, textSize, barH, rightMargin, bottomMargin);
    }
}
