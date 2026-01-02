// I used ChatGPT for the sticky code, for making it last longer - I couldn't be arsed doing it myself.
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
import sillicat.setting.impl.BindSetting;
import sillicat.util.RenderUtil;
import sillicat.util.font.CustomFontRenderer;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "TargetHUD",
        description = "Displays opponent information",
        category = Category.Render,
        defaultKey = -1,
        enabled = false
)
public class TargetHud extends Module {

    public TargetHud(){
        setKey(getKey());
    }

    private final List<Long> clicks = new ArrayList<Long>();

    private int width;
    private int height;
    private int x;
    private int y;

    private EntityLivingBase lastTarget;
    private long lastTargetTime;
    private final long stickyMs = 350;

    @Override
    public void on2D(ScaledResolution sr) {
        width = 140;
        height = 52;

        int marginX = 140;
        int marginY = 130;

        // lower-right portion (anchored near bottom-right with margins)
        x = sr.getScaledWidth() - width - marginX;
        y = sr.getScaledHeight() - height - marginY;

        CustomFontRenderer cfr = Sillicat.INSTANCE.getFontManager().getInter().size(18);

        EntityLivingBase target = getStickyTarget(12.0, 70.0f);
        if (target == null) return;

        RenderUtil.drawRect(x, y, width, height, 0xC0313335);

        int pad = 6;
        int headSize = 24;

        int contentX = x + pad;
        int contentY = y + pad;

        boolean isPlayer = target instanceof EntityPlayer;

        if (isPlayer) {
            drawPlayerHead(contentX, contentY, headSize, (EntityPlayer) target);
        }

        int textStartX = isPlayer ? (contentX + headSize + 8) : contentX;
        int nameY = contentY + 2;

        String name = target.getName();
        cfr.drawString(name, textStartX, nameY, 0xFFFFFFFF);

        String hpText = String.format("%.0f\u2764", target.getHealth());
        float hpW = cfr.getWidth(hpText);
        cfr.drawString(hpText, x + width - pad - hpW, nameY, 0xFFFF4D4D);

        renderHealthBar(target, pad);
    }

    private void renderHealthBar(EntityLivingBase target, int pad) {
        int barW = width - (pad * 2);
        int barH = 6;

        int left = x + pad;
        int top = y + height - pad - barH;

        RenderUtil.drawRect(left, top, barW, barH, 0xFF19170D);

        float healthPercent = target.getHealth() / target.getMaxHealth();
        if (healthPercent < 0f) healthPercent = 0f;
        if (healthPercent > 1f) healthPercent = 1f;

        int filled = (int) (barW * healthPercent);
        RenderUtil.drawRect(left, top, filled, barH, 0xFFC226FF);
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
            if (!mc.thePlayer.canEntityBeSeen(e)) continue; // remove if you want through-walls HUD

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
        if (mc.thePlayer.getDistanceToEntity(e) > range) return false;
        return true;
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
