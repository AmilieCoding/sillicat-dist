package sillicat.notification;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.util.AnimationUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

public class Notification {
    private final String moduleName;
    private final String status;
    private final long startTime;
    private final long displayTime;

    // animation
    private final AnimationUtil anim = new AnimationUtil(0f, 0.02f, AnimationUtil.Easing.EASE_OUT_CUBIC);

    public Notification(String moduleName, boolean enabled){
        this.moduleName = moduleName;
        this.status = enabled ? "enabled" : "disabled";
        this.startTime = System.currentTimeMillis();
        this.displayTime = 2000;
    }

    public boolean shouldRemove(){
        return System.currentTimeMillis() - startTime >= displayTime;
    }

    public void draw(int yOffset){
        ScaledResolution sr = new ScaledResolution(Sillicat.INSTANCE.getMc());

        long elapsed = System.currentTimeMillis() - startTime;
        double progress = (double) elapsed / displayTime;

        int width = 95;
        int height = 30;

        int baseX = sr.getScaledWidth() - width - 2;
        int y = sr.getScaledHeight() - yOffset - height - 2;

        // --- decide animation target (in, hold, out) ---
        // last 250ms slides out
        long outStart = displayTime - 250L;

        if (elapsed < outStart) {
            anim.setTarget(1f);
        } else {
            anim.setTarget(0f);
        }

        float pt = Sillicat.INSTANCE.getMc().timer.renderPartialTicks;
        anim.update(pt); // FPS-aware update
        float t = anim.getValue(); // eased 0..1

        // --- slide from right ---
        float slide = (1f - t) * (width + 14f);
        int x = (int) (baseX + slide);

        RenderUtil.drawRect(x, y, width, height, 0x90000000);

        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);
        CustomFontRenderer cfrLarge = Sillicat.INSTANCE.getFontManager().getInter().size(20);

        cfrLarge.drawString("Sillicat", x + 3, y + 4, -1);
        cfr.drawString(moduleName + " " + status, x + 3, y + 16, -1);

        int progressBarWidth = (int) (width * (1 - progress));
        RenderUtil.drawRect(x, y + height - 2, progressBarWidth, 2, 0xFFC226FF);
    }
}
