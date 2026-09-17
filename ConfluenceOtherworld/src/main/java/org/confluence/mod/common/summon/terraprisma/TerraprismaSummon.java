package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;

/// 泰拉棱镜召唤物的运行实例。
public final class TerraprismaSummon extends SummonInstance {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 18.0F;
    private static final double SEARCH_RANGE = 40.0;
    private static final SummonVisualState FOLLOWING_VISUAL_STATE = new SummonVisualState(true, SummonAnimation.NONE, 0, 0, 0.0F, 1.0F, 1.0F);
    private final TerraprismaSlashGoal slashGoal = new TerraprismaSlashGoal(this);
    private final TerraprismaRotateGoal rotateGoal = new TerraprismaRotateGoal(this);
    private float skillDamageMultiplier = 1.0F;
    private boolean followingOwner;
    private SummonAnimation animationState = SummonAnimation.NONE;
    private int animationTicks;
    private int animationDuration;
    private float animationDegrees;
    private float scale = 1.0F;
    private float scaleY = 1.0F;
    private int scaleYTicks;

    public TerraprismaSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("terraprisma"), owner, slotCost, stats, initialPose);
        addGoal(0, slashGoal);
        addGoal(0, rotateGoal);
        addGoal(1, new TerraprismaChaseGoal(this));
        addGoal(2, new TerraprismaFollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), SEARCH_RANGE);
    }

    @Override
    protected boolean usesOwnerRecovery() {
        return false;
    }

    @Override
    protected void beforeGoalTick() {
        if (scaleYTicks > 0 && --scaleYTicks == 0) scaleY = 1.0F;
        if (animationTicks < animationDuration) {
            animationTicks++;
        } else if (animationState != SummonAnimation.NONE) {
            animationState = SummonAnimation.NONE;
            scale = 1.0F;
            scaleY = 1.0F;
        }
    }

    @Override
    protected void afterGoalTick() {
        slashGoal.updateCooldown();
        rotateGoal.updateCooldown();
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (!hasValidTarget()) {
            return;
        }
        for (SummonCollision.Hit hit : SummonCollision.sweep(owner().level(), previousPreviousPose, previousPose,
                currentPose, attackBox(), candidate -> candidate == target()
                        || SummonTargetCache.isValidTarget(owner(), candidate, SEARCH_RANGE * 2.0, false))) {
            hurtEntity(hit.damageRecipient(), hit.encounterOwner(), skillDamageMultiplier);
        }
    }

    private AABB attackBox() {
        double horizontalScale = scale;
        double verticalScale = scale * scaleY;
        return new AABB(-0.75 * horizontalScale, -0.75 * verticalScale, -0.75 * horizontalScale, 0.75 * horizontalScale, 0.75 * verticalScale, 1.5 * horizontalScale);
    }

    public boolean hasValidTarget() {
        LivingEntity target = target();
        return target != null && target.isAlive() && !target.isRemoved() && target.level() == owner().level();
    }

    boolean targetWithinOwnerRange() {
        return hasValidTarget() && target().distanceToSqr(owner()) <= SEARCH_RANGE * SEARCH_RANGE;
    }

    SummonPose followPose(Vec3 nextPosition, Vec3 targetPosition) {
        Vec3 direction = targetPosition.subtract(nextPosition);
        Vec3 horizontal = direction.multiply(1.0, 0.0, 1.0);
        if (horizontal.lengthSqr() < 1.0E-6) {
            float yaw = summonYawFromOwner();
            float pitch = IDLE_PITCH;
            return new SummonPose(nextPosition, yaw, pitch, 0.0F);
        }
        Vec3 aimed = horizontal.normalize().add(0.0, -IDLE_PITCH_TANGENT, 0.0);
        return aimed.lengthSqr() < 1.0E-6 ? currentPose() : aimAt(nextPosition, aimed);
    }

    /// 返回主人当前朝向的偏航角，供就位状态下的剑身对齐。
    float summonYawFromOwner() {
        return owner().yBodyRot;
    }

    /// 把主人身体朝向投影到水平面，得到跟随站位使用的单位前向向量。
    static Vec3 ownerFacing(float bodyYaw) {
        return Vec3.directionFromRotation(0.0F, bodyYaw);
    }

    /// 就位时剑身相对水平面的下压角度，单位为度。
    private static final float IDLE_PITCH = 62.0F;
    private static final double IDLE_PITCH_TANGENT = Math.tan(Math.toRadians(IDLE_PITCH));

    SummonPose aimAt(Vec3 position, Vec3 direction) {
        Vec3 normalized = direction.normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-normalized.x, normalized.z));
        float pitch = (float) Math.toDegrees(Math.asin(-normalized.y));
        return new SummonPose(position, yaw, pitch, 0.0F);
    }

    Vec3 eyePosition() {
        return position().add(0.0, 0.85, 0.0);
    }

    void moveAndLook(Vec3 movement, Vec3 lookPosition) {
        Vec3 direction = lookPosition.subtract(eyePosition());
        SummonPose aimed = direction.lengthSqr() < 1.0E-6 ? currentPose() : aimAt(position(), direction);
        moveTo(new SummonPose(position().add(movement), aimed.yaw(), aimed.pitch(), aimed.roll()));
    }

    void moveTo(SummonPose pose) {
        advanceTo(pose);
    }

    void setSkillDamageMultiplier(float multiplier) {
        skillDamageMultiplier = multiplier;
    }

    void setFollowingOwner(boolean followingOwner) {
        this.followingOwner = followingOwner;
    }

    void beginSlashAnimation() {
        animationState = SummonAnimation.SLASH;
        animationTicks = 0;
        animationDuration = 14;
        animationDegrees = 0.0F;
    }

    void finishSlashAnimation() {
        resetAnimation();
    }

    void beginRotateAnimation() {
        resetAnimation();
    }

    void finishRotateAnimation() {
        resetAnimation();
    }

    private void resetAnimation() {
        animationState = SummonAnimation.NONE;
        animationTicks = 0;
        animationDuration = 0;
        animationDegrees = 0.0F;
        scale = 1.0F;
        scaleY = 1.0F;
        scaleYTicks = 0;
    }

    @Override
    public SummonVisualState visualState() {
        if (animationState == SummonAnimation.NONE && scale == 1.0F && scaleY == 1.0F)
            return followingOwner ? FOLLOWING_VISUAL_STATE : SummonVisualState.DEFAULT;
        return new SummonVisualState(followingOwner, animationState, animationTicks, animationDuration, animationDegrees, scale, scaleY);
    }

}
