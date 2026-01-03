package sillicat.notification;

import net.minecraft.client.gui.ScaledResolution;
import sillicat.Sillicat;
import sillicat.module.impl.settings.Notifications;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.ui.designLanguage.Theme;
import sillicat.util.AnimationUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

public class Notification {
    private final String moduleName;
    private final String status;
    private final long startTime;
    private final long displayTime;

    private final AnimationUtil anim = new AnimationUtil(0f, 0.02f, AnimationUtil.Easing.EASE_OUT_CUBIC);

    public Notification(String moduleName, boolean enabled){
        this.moduleName = moduleName;
        this.status = enabled ? "enabled" : "disabled";
        this.startTime = System.currentTimeMillis();
        this.displayTime = 2000;
    }

    public boolean shouldRemove(){
        long elapsed = System.currentTimeMillis() - startTime;

        // start leaving after displayTime, but only remove once we've animated out
        if (elapsed >= displayTime) {
            anim.setTarget(0f);
            anim.update(Sillicat.INSTANCE.getMc().timer.renderPartialTicks);
            return anim.getValue() <= 0.01f;
        }
        return false;
    }

    public void draw(int yOffset){
        ScaledResolution sr = new ScaledResolution(Sillicat.INSTANCE.getMc());

        Notifications nm = (Notifications) Sillicat.INSTANCE.getModuleManager().getModule(Notifications.class);

        int width  = nm != null ? (int) nm.width.getVal()  : 105;
        int height = nm != null ? (int) nm.height.getVal() : 30;
        int gap    = nm != null ? (int) nm.gap.getVal()    : 2;

        int radius = nm != null ? (int) nm.radius.getVal() : 5;

        int titleSize = nm != null ? (int) nm.titleSize.getVal() : 20;
        int textSize  = nm != null ? (int) nm.textSize.getVal()  : 18;

        int barH = nm != null ? (int) nm.barH.getVal() : 2;

        int rightMargin  = nm != null ? (int) nm.rightMargin.getVal()  : 2;
        int bottomMargin = nm != null ? (int) nm.bottomMargin.getVal() : 2;

        long elapsed = System.currentTimeMillis() - startTime;
        double progress = (double) elapsed / displayTime;

        int baseX = sr.getScaledWidth() - width - rightMargin;
        int y = sr.getScaledHeight() - yOffset - height - gap - bottomMargin;

        long outStart = displayTime - 250L;
        anim.setTarget(elapsed < outStart ? 1f : 0f);

        float pt = Sillicat.INSTANCE.getMc().timer.renderPartialTicks;
        anim.update(pt);
        float t = anim.getValue();

        float slide = (1f - t) * (width + 14f);
        int x = (int) (baseX + slide);

        RenderUtil.drawRoundedRect(x, y, width, height, radius, ColorScheme.STANDARD_BG.get());

        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(textSize);
        CustomFontRenderer cfrLarge = Sillicat.INSTANCE.getFontManager().getInter().size(titleSize);

        int padTop = 6;
        int padBot = 6;
        int lineGap = 4;
        int barGap = 3;

        float titleH = cfrLarge.getHeight("Sillicat");
        float subH   = cfr.getHeight("Sillicat");

        int barTop = y + height - barH;
        int contentTop = y + padTop;
        int contentBot = barTop - barGap - padBot;

        int contentH = contentBot - contentTop;
        float blockH = titleH + lineGap + subH;

        float titleY = contentTop + Math.max(0, (contentH - blockH) / 2);
        float subY   = titleY + titleH + lineGap;

        cfrLarge.drawString("Sillicat", x + 6, titleY, -1);
        cfr.drawString(moduleName + " " + status, x + 6, subY, -1);


        int progressBarWidth = (int) (width * (1 - progress));
        progressBarWidth = Math.max(0, Math.min(progressBarWidth, width));

        RenderUtil.drawRect(x, y + height - barH, progressBarWidth, barH, Theme.getAccent());
    }
}
