package sillicat.module.impl.combat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;
import sillicat.module.Category;
import sillicat.module.Module;
import sillicat.module.ModuleInfo;
import sillicat.setting.impl.BindSetting;
import sillicat.setting.impl.BooleanSetting;
import sillicat.setting.impl.ModeSetting;
import sillicat.setting.impl.NumberSetting;

import java.util.ArrayList;
import java.util.List;

@ModuleInfo(
        name = "KillAura",
        description = "Automatically target mobs, players and others.",
        category = Category.Combat,
        defaultKey = -1,
        enabled = false
)

public class KillAura extends Module {
    private final BooleanSetting players = new BooleanSetting("Players", true);
    private final BooleanSetting passive = new BooleanSetting("Passive", false);
    private final BooleanSetting mobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting teams = new BooleanSetting("Teams", false);
    private final BooleanSetting invisible = new BooleanSetting("Invisible", false);
    private final BooleanSetting noswing = new BooleanSetting("No Swing", false);
    private final NumberSetting range = new NumberSetting("Range", 4.0, 1.0, 14.0, 0.1);
    private final NumberSetting rotationSpeed = new NumberSetting("Rotation Speed", 30.0, 1.0, 180.0, 1);
    private final NumberSetting delay = new NumberSetting("Delay (ms)", 50, 25, 500, 25);
    private final ModeSetting mode = new ModeSetting("Mode", "Single", "Switch", "Multi");

    private EntityLivingBase currentTarget = null;
    private List<EntityLivingBase> targets = new ArrayList<>();
    private int targetIndex = 0;
    private long lastAttackTime = 0;

    // Store calculated rotations for server-side
    private float[] targetRotations = new float[2]; // [yaw, pitch]
    private float serverYaw = 0;
    private float serverPitch = 0;

    public KillAura(){
        setKey(getKey());
        addSettings(players, passive, mobs, teams, invisible, noswing, range, rotationSpeed, delay, mode);
    }

    @Override
    public void onUpdate() {
        findTargets();

        if(mode.getCurrMode().equalsIgnoreCase("Single")){
            handleSingle();
        }else if(mode.getCurrMode().equalsIgnoreCase("Switch")){
            handleSwitch();
        }else if(mode.getCurrMode().equalsIgnoreCase("Multi")){
            handleMulti();
        }
    }

    private void handleSingle(){
        if(!targets.isEmpty()){
            currentTarget = targets.get(0);
            if(currentTarget != null){
                if(faceEntity(currentTarget, rotationSpeed.getVal())){
                    if(canAttack()){
                        attackEntity(currentTarget);
                        lastAttackTime = System.currentTimeMillis();
                    }
                }
            }
        }
    }

    private void handleSwitch(){
        if(!targets.isEmpty()){
            if(targetIndex >= targets.size()) targetIndex = 0;

            currentTarget = targets.get(targetIndex);
            if(currentTarget != null){
                if(faceEntity(currentTarget, rotationSpeed.getVal())){
                    if(canAttack()){
                        attackEntity(currentTarget);
                        lastAttackTime = System.currentTimeMillis();
                        targetIndex++;
                    }
                }
            }
        }
    }

    private void handleMulti(){
        if(!targets.isEmpty()){
            for (EntityLivingBase target : targets){
                if (target == null) continue;

                if (faceEntity(target, rotationSpeed.getVal()) && canAttack()){
                    attackEntity(target);
                    lastAttackTime = System.currentTimeMillis();
                }
            }
        }
    }

    private void findTargets(){
        targets.clear();
        List<Entity> entities = mc.theWorld.loadedEntityList;
        for(Entity entity : entities){
            if(entity instanceof EntityLivingBase && entity != mc.thePlayer && !entity.isDead){
                EntityLivingBase target = (EntityLivingBase) entity;
                double r = range.getVal();
                if (mc.thePlayer.getDistanceSqToEntity(target) <= r * r) {
                    if (shouldAttack(target)) {
                        targets.add(target);
                    }
                }
            }
        }
    }

    private boolean shouldAttack(EntityLivingBase target){
        if(target instanceof EntityPlayer){
            if(!players.isEnabled()) return false;
            if(!teams.isEnabled() && isOnSameTeam(target)) return false;
        }
        if(target instanceof EntityAnimal && !passive.isEnabled()) return false;
        if(target instanceof IMob && !mobs.isEnabled()) return false;
        if(!invisible.isEnabled() && target.isInvisible()) return false;

        return true;
    }

    private boolean isOnSameTeam(EntityLivingBase target){
        return mc.thePlayer.getTeam() != null && target.getTeam() != null && mc.thePlayer.isOnSameTeam(target);
    }

    private boolean faceEntity(Entity entity, double speed){
        double diffX = entity.posX - mc.thePlayer.posX;
        double diffY = (entity.posY + entity.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = entity.posZ - mc.thePlayer.posZ;

        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float targetYaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float targetPitch = (float) (-Math.toDegrees(Math.atan2(diffY, distXZ)));

        // Use server rotations instead of client camera
        float currYaw = serverYaw;
        float currPitch = serverPitch;

        float yawDiff = wrapAngle(targetYaw - currYaw);
        float pitchDiff = wrapAngle(targetPitch - currPitch);

        float rotationStep = (float) speed;
        boolean yawComplete = false;
        boolean pitchComplete = false;

        if(Math.abs(yawDiff) > rotationStep){
            yawDiff = (yawDiff > 0) ? rotationStep : -rotationStep;
        }else{
            yawComplete = true;
        }

        if(Math.abs(pitchDiff) > rotationStep){
            pitchDiff = (pitchDiff > 0) ? rotationStep : -rotationStep;
        }else{
            pitchComplete = true;
        }

        // Update server-side rotations only
        serverYaw += yawDiff;
        serverPitch += pitchDiff;

        // Apply rotations to head/body for visual (not camera)
        mc.thePlayer.rotationYawHead = serverYaw;
        mc.thePlayer.rotationPitchHead = serverPitch;

        // Store for attack packets
        targetRotations[0] = serverYaw;
        targetRotations[1] = serverPitch;

        return yawComplete && pitchComplete;
    }

    private float wrapAngle(float angle){
        angle %= 360.0F;
        if(angle >= 180f) angle -= 360.0F;
        if(angle < -180f) angle += 360.0F;
        return angle;
    }

    private boolean canAttack(){
        long time = System.currentTimeMillis();
        return time - lastAttackTime > delay.getVal();
    }

    private void attackEntity(EntityLivingBase entity){
        if(!noswing.isEnabled()) mc.thePlayer.swingItem();
        mc.playerController.attackEntity(mc.thePlayer, entity);
    }

    // Helper method to get current target rotations
    public float[] getRotations(Entity target) {
        double diffX = target.posX - mc.thePlayer.posX;
        double diffY = (target.posY + target.getEyeHeight()) - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        double diffZ = target.posZ - mc.thePlayer.posZ;

        double distXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F);
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, distXZ)));

        return new float[] { yaw, pitch };
    }
}