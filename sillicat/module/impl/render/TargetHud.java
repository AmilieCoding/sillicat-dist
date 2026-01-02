package sillicat.module.impl.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import sillicat.Sillicat;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.NumberSetting;
import sillicat.ui.designLanguage.ColorScheme;
import sillicat.ui.designLanguage.Theme;
import sillicat.util.AnimationUtil;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

@ModuleInfo(
        name = "TargetHUD",
        description = "Displays opponent information",
        category = Category.Render,
        defaultKey = -1,
        enabled = false
)
public class TargetHud extends Module {

    private final NumberSetting range = new NumberSetting("Range", 6, 1, 10, 1);

    private int width;
    private int height;
    private int x;
    private int y;

    private EntityLivingBase lastTarget;
    private long lastTargetTime;
    private final long stickyMs = 350;

    private final AnimationUtil popAnim = new AnimationUtil(0f, 0.06f, AnimationUtil.Easing.EASE_OUT_BACK);
    private EntityLivingBase prevTarget = null;

    public TargetHud() {
        addSettings(range);
        setKey(getKey());
    }

    @Override
    public void on2D(ScaledResolution sr) {
        width = 140;
        height = 52;

        int marginX = 140;
        int marginY = 130;

        x = sr.getScaledWidth() - width - marginX;
        y = sr.getScaledHeight() - height - marginY;

        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);

        double r = range.getVal();
        EntityLivingBase target = getStickyTarget(r, 70.0f);

        float pt = mc.timer.renderPartialTicks;

        // Keep rendering last target while animating out
        EntityLivingBase renderTarget = (target != null) ? target : prevTarget;

        // Different speeds for in/out so it never "hangs"
        popAnim.setSpeed(target == null ? 0.18f : 0.08f);

        if (target != null) {
            if (target != prevTarget) {
                prevTarget = target;
                // hard reset punch without needing setValue()
                popAnim.setTarget(0f);
                popAnim.update(8f);
            }
            popAnim.setTarget(1f);
        } else {
            popAnim.setTarget(0f);
        }

        popAnim.update(pt);
        float t = popAnim.getValue();

        // Kill the tail quickly on fade-out
        if (target == null && t < 0.12f) {
            prevTarget = null;
            return;
        }

        if (t <= 0.02f) {
            if (target == null) prevTarget = null;
            return;
        }

        if (renderTarget == null) return;

        float scale = 0.90f + (0.10f * t);
        int cx = x + (width / 2);
        int cy = y + (height / 2);

        GL11.glPushMatrix();
        GL11.glTranslatef(cx, cy, 0f);
        GL11.glScalef(scale, scale, 1f);
        GL11.glTranslatef(-cx, -cy, 0f);
        GL11.glColor4f(1f, 1f, 1f, t);

        RenderUtil.drawRoundedRect(x, y, width, height, 10, 0xC0313335);

        int pad = 6;
        int headSize = 24;

        int contentX = x + pad;
        int contentY = y + pad;

        boolean isPlayer = renderTarget instanceof EntityPlayer;

        if (isPlayer) {
            drawPlayerHead(contentX, contentY, headSize, (EntityPlayer) renderTarget);
        }

        int textStartX = isPlayer ? (contentX + headSize + 8) : contentX;
        int nameY = contentY + 2;

        String name = renderTarget.getName();
        cfr.drawString(name, textStartX, nameY, 0xFFFFFFFF);

        String hpText = String.format("%.0f\u2764", renderTarget.getHealth());
        float hpW = cfr.getWidth(hpText);
        cfr.drawString(hpText, x + width - pad - hpW, nameY, 0xFFFF4D4D);

        renderHealthBar(renderTarget, pad);

        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glPopMatrix();
    }

    private void renderHealthBar(EntityLivingBase target, int pad) {
        int barW = width - (pad * 2);
        int barH = 6;

        int left = x + pad;
        int top = y + height - pad - barH;

        RenderUtil.drawRoundedRect(left, top, barW, barH, 5, ColorScheme.PANEL_BG.get());

        float maxHp = target.getMaxHealth();
        float healthPercent = maxHp <= 0f ? 0f : (target.getHealth() / maxHp);
        if (healthPercent < 0f) healthPercent = 0f;
        if (healthPercent > 1f) healthPercent = 1f;

        int filled = (int) (barW * healthPercent);
        RenderUtil.drawRoundedRect(left, top, filled, barH, 5, Theme.getAccent());
    }

    private void drawPlayerHead(int x, int y, int size, EntityPlayer player) {
        NetworkPlayerInfo playerInfo = mc.getNetHandler().getPlayerInfo(player.getUniqueID());
        if (playerInfo != null) {
            mc.getTextureManager().bindTexture(playerInfo.getLocationSkin());
            GL11.glColor4f(1F, 1F, 1F, 1F);
            Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, size, size, 64F, 64F);
        }
    }

    private EntityLivingBase getStickyTarget(double range, float fovDegrees) {
        long now = System.currentTimeMillis();

        EntityLivingBase current = getBestTarget(range, fovDegrees);

        if (current != null) {
            lastTarget = current;
            lastTargetTime = now;
            return current;
        }

        if (lastTarget != null && now - lastTargetTime <= stickyMs && isValidTarget(lastTarget, range)) {
            return lastTarget;
        }

        lastTarget = null;
        return null;
    }

    private EntityLivingBase getBestTarget(double range, float fovDegrees) {
        EntityLivingBase best = null;
        double bestDist = Double.MAX_VALUE;

        for (Object o : mc.theWorld.loadedEntityList) {
            if (!(o instanceof EntityLivingBase)) continue;
            EntityLivingBase e = (EntityLivingBase) o;

            if (!isValidTarget(e, range)) continue;
            if (!isInFov(e, fovDegrees)) continue;
            if (!mc.thePlayer.canEntityBeSeen(e)) continue;

            double dist = mc.thePlayer.getDistanceToEntity(e);
            if (dist < bestDist) {
                bestDist = dist;
                best = e;
            }
        }
        return best;
    }

    private boolean isValidTarget(EntityLivingBase e, double range) {
        if (e == null) return false;
        if (e == mc.thePlayer) return false;
        if (e.isDead || e.getHealth() <= 0) return false;
        return mc.thePlayer.getDistanceToEntity(e) <= range;
    }

    private boolean isInFov(EntityLivingBase e, float fovDegrees) {
        float yawTo = getYawTo(e);
        float yawDiff = wrapAngleTo180(yawTo - mc.thePlayer.rotationYaw);
        return Math.abs(yawDiff) <= (fovDegrees / 2.0f);
    }

    private float getYawTo(EntityLivingBase e) {
        double dx = e.posX - mc.thePlayer.posX;
        double dz = e.posZ - mc.thePlayer.posZ;
        return (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
    }

    private float wrapAngleTo180(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) angle -= 360.0f;
        if (angle < -180.0f) angle += 360.0f;
        return angle;
    }
}
