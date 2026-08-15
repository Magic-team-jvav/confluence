package org.confluence.mod.common.summon;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.api.summon.SummonTargetCache;

/**
 * 地面近战召唤物的通用运行基类。
 *
 * <p>这里沉淀寻路、跟随、近战判定和攻击节奏；具体召唤物仍然通过子类提供体型、
 * 搜索范围、移动速度和命中反馈，避免把铁傀儡、雪怪等行为差异硬塞进同一组分支判断。</p>
 */
public abstract class GroundMeleeSummon extends PhysicalSummon {
    private static final double FOLLOW_START_DISTANCE_SQR = 32.0 * 32.0;
    private final double searchRange;
    private final double combatMoveSpeed;
    private final double followMoveSpeed;
    private int attackCooldown;
    private int attackAnimationTicks;

    protected GroundMeleeSummon(ResourceLocation type, ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot,
                                SummonPose initialPose, double width, double height, double searchRange,
                                double combatMoveSpeed, double followMoveSpeed) {
        super(type, owner, slotCost, snapshot, initialPose, width, height);
        this.searchRange = searchRange;
        this.combatMoveSpeed = combatMoveSpeed;
        this.followMoveSpeed = followMoveSpeed;
        addGoal(1, new AttackGoal(this));
        addGoal(2, new FollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), searchRange);
    }

    @Override
    protected void beforeGoalTick() {
        attackCooldown = Math.max(0, attackCooldown - 1);
        attackAnimationTicks = Math.max(0, attackAnimationTicks - 1);
        beforeGroundGoalTick();
    }

    protected void beforeGroundGoalTick() {}

    protected void moveInCombat(LivingEntity target) {
        navigateGround(targetBasePosition(), combatMoveSpeed, 0.5);
    }

    protected void onAttackAttempt(LivingEntity target) {}

    protected void onSuccessfulHit(LivingEntity target) {}

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(false, attackAnimationTicks > 0 ? SummonAnimation.MELEE_ATTACK : SummonAnimation.NONE,
                10 - attackAnimationTicks, 10, 0.0F, 1.0F, 1.0F);
    }

    private void tryAttack(LivingEntity target) {
        if (attackCooldown > 0 || position().distanceToSqr(targetBounds().getCenter()) > meleeAttackRangeSqr()
                || !hasAttackLineOfSight()) {
            return;
        }
        attackCooldown = 20;
        attackAnimationTicks = 10;
        onAttackAttempt(target);
        if (hurtTarget(target, 1.0F)) {
            onSuccessfulHit(target);
        }
    }

    /**
     * 使用原版近战距离公式，让召唤物和目标的体型都参与判定。
     */
    private double meleeAttackRangeSqr() {
        double ownReach = width() * 2.0;
        return ownReach * ownReach + targetBounds().getXsize();
    }

    /**
     * 近战命中保留 1.21 的视线限制，避免召唤物隔着完整方块直接结算伤害。
     */
    private boolean hasAttackLineOfSight() {
        Vec3 start = position().add(0.0, height() * 0.5, 0.0);
        Vec3 end = targetBounds().getCenter();
        return owner().level().clip(new ClipContext(start, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, owner())).getType() == HitResult.Type.MISS;
    }

    private static final class AttackGoal extends SummonGoal<GroundMeleeSummon> {
        private AttackGoal(GroundMeleeSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return summon.target() != null;
        }

        @Override
        public void tick() {
            summon.moveInCombat(summon.target());
            summon.tryAttack(summon.target());
        }
    }

    private static final class FollowOwnerGoal extends SummonGoal<GroundMeleeSummon> {
        private FollowOwnerGoal(GroundMeleeSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            if (summon.position().distanceToSqr(summon.owner().position()) >= FOLLOW_START_DISTANCE_SQR) {
                summon.navigateGround(summon.owner().position(), summon.followMoveSpeed, 0.5);
            } else {
                summon.moveWithCollision(new Vec3(summon.velocity().x * 0.6, summon.velocity().y - 0.08, summon.velocity().z * 0.6));
            }
        }
    }
}
