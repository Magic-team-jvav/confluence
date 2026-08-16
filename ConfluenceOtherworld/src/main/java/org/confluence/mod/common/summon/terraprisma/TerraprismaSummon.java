package org.confluence.mod.common.summon.terraprisma;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonCollision;
import org.confluence.mod.common.summon.SummonInstance;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonVisualState;

import java.util.List;

/// 泰拉棱镜召唤物的运行实例。
public final class TerraprismaSummon extends SummonInstance {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 18.0F;
    private static final double SEARCH_RANGE = 40.0;
    private static final AABB ATTACK_BOX = new AABB(-0.15, -0.06, -0.35, 0.15, 0.06, 1.0);
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

    public TerraprismaSummon(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(Confluence.asResource("terraprisma"), owner, slotCost, snapshot, initialPose);
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
    protected double ownerRecoveryDistanceSqr() {
        return 40.0 * 40.0;
    }

    @Override
    protected void beforeGoalTick() {
        slashGoal.updateCooldown();
        rotateGoal.updateCooldown();
        if (animationTicks < animationDuration) {
            animationTicks++;
        } else if (animationState != SummonAnimation.NONE) {
            animationState = SummonAnimation.NONE;
            scale = 1.0F;
            scaleY = 1.0F;
        }
    }

    @Override
    protected void afterPathAdvance(SummonPose previousPreviousPose, SummonPose previousPose, SummonPose currentPose) {
        if (!hasValidTarget()) {
            return;
        }
        for (SummonCollision.Hit hit : SummonCollision.sweep(owner().level(), previousPreviousPose, previousPose,
                currentPose, ATTACK_BOX, candidate -> candidate == target()
                        || SummonTargetCache.isValidTarget(owner(), candidate, SEARCH_RANGE * 2.0, false))) {
            hurtTarget(hit.target(), skillDamageMultiplier);
        }
    }

    public boolean hasValidTarget() {
        LivingEntity target = target();
        return target != null && target.isAlive() && !target.isRemoved() && target.level() == owner().level();
    }

    Vec3 followPosition() {
        int sequence = order() + 1;
        Vec3 forward = Vec3.directionFromRotation(0.0F, owner().yBodyRot).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 right = forward.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        int layer = sequence / 2;
        float side = (sequence & 1) == 0 ? 1.0F : -1.0F;
        double backDistance = Math.max(0.16, 0.24F - 0.012F * (sequence - 1));
        return owner().position().subtract(forward.scale(backDistance))
                .add(0.0, 1.0 + layer * 0.08F, 0.0)
                .add(right.scale(0.32F * layer * side));
    }

    SummonPose followPose(Vec3 position, Vec3 targetPosition) {
        int sequence = order() + 1;
        Vec3 forward = Vec3.directionFromRotation(0.0F, owner().yBodyRot).multiply(1.0, 0.0, 1.0).normalize();
        Vec3 lookPosition = position.subtract(forward.scale(5.0))
                .add(0.0, -8.0 - (sequence - 1) / 2.0, 0.0)
                .add(position.subtract(targetPosition).scale(20.0));
        Vec3 direction = lookPosition.subtract(position);
        return direction.lengthSqr() < 1.0E-6 ? currentPose() : aimAt(position, direction);
    }

    SummonPose aimAt(Vec3 position, Vec3 direction) {
        Vec3 normal = direction.cross(new Vec3(0.0, 1.0, 0.0)).normalize();
        if (normal.lengthSqr() < 1.0E-6) {
            normal = new Vec3(1.0, 0.0, 0.0);
        }
        return poseFromAxes(position, direction, normal);
    }

    void moveAndLook(Vec3 movement, Vec3 lookPosition) {
        Vec3 direction = lookPosition.subtract(position());
        SummonPose aimed = direction.lengthSqr() < 1.0E-6 ? currentPose() : aimAt(position(), direction);
        moveTo(new SummonPose(position().add(movement), aimed.yaw(), aimed.pitch(), aimed.roll()));
    }

    void followOwner(Vec3 movement) {
        moveTo(new SummonPose(position().add(movement), owner().yBodyRot, 0.0F, 0.0F));
    }

    void moveTo(SummonPose pose) {
        setPath("terraprisma_move", List.of(pose));
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
        animationDuration = 10;
        animationDegrees = 0.0F;
    }

    void finishSlashAnimation() {
        boolean validTarget = hasValidTarget();
        if (validTarget && owner().getRandom().nextBoolean() || !validTarget && owner().getRandom().nextFloat() < 0.1F) {
            int cycles = 2 + owner().getRandom().nextInt(4);
            animationState = SummonAnimation.SPIN_X;
            animationTicks = 0;
            animationDuration = 12 * cycles;
            animationDegrees = (owner().getRandom().nextBoolean() ? 1.0F : -1.0F) * 360.0F * cycles;
            scaleY = 2.0F;
        }
    }

    void beginRotateAnimation() {
        animationState = SummonAnimation.ROTATE_Z;
        animationTicks = 0;
        animationDuration = 10;
        animationDegrees = 1080.0F;
    }

    void finishRotateAnimation() {
        boolean validTarget = hasValidTarget();
        if (validTarget && owner().getRandom().nextBoolean() || !validTarget && owner().getRandom().nextFloat() < 0.1F) {
            animationState = SummonAnimation.SPIN_Y;
            animationTicks = 0;
            animationDuration = 30;
            animationDegrees = 720.0F;
            scale = 2.0F;
        }
    }

    public boolean isFollowingOwner() {
        return followingOwner;
    }

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(followingOwner, animationState, animationTicks, animationDuration,
                animationDegrees, scale, scaleY);
    }

    public SummonAnimation animationState() {
        return animationState;
    }

    public int animationTicks() {
        return animationTicks;
    }

    public int animationDuration() {
        return animationDuration;
    }

    public float animationDegrees() {
        return animationDegrees;
    }

    public float scale() {
        return scale;
    }

    public float scaleY() {
        return scaleY;
    }

    int slashCooldown() {
        return slashGoal.cooldown();
    }

    int rotateCooldown() {
        return rotateGoal.cooldown();
    }
}
