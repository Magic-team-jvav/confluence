package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;
import org.confluence.mod.common.summon.projectile.SummonProjectileTypes;

/// 黄蜂召唤物的运行实例。
///
/// <p>这里保留悬停、瞄准和短间隔毒刺射击。
/// 新架构只负责取消真实实体依赖，不能改变玩家能观察到的战斗节奏。</p>
public final class HornetSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 8.0F;
    private static final int ATTACK_COOLDOWN = 10;
    private int attackCooldown;
    private int attackAnimationTicks;
    private int repositionCooldown;
    private Vec3 movementDestination;
    private boolean preparingShot;
    private boolean shotThisTick;
    private LivingEntity preparedTarget;

    public HornetSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("hornet_baby"), owner, slotCost, stats, initialPose);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new MomentumSummonIdleGoal<>(this, 2.0, 0.035, 0.70, 0));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 84.0);
    }

    @Override
    protected int ownerRecoveryInterval() {
        return 1;
    }

    @Override
    protected void beforeGoalTick() {
        repositionCooldown--;
        attackAnimationTicks = Math.max(0, attackAnimationTicks - 1);
        if (preparingShot) {
            preparingShot = false;
            LivingEntity target = preparedTarget;
            preparedTarget = null;
            if (target != null && target.isAlive()) shoot(target);
        }
    }

    private void combat(LivingEntity target) {
        if (shotThisTick) {
            shotThisTick = false;
            keepOnTarget();
            return;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
            keepOnTarget();
            return;
        }
        preparingShot = true;
        preparedTarget = target;
        keepOnTarget();
    }

    private void keepOnTarget() {
        if (repositionCooldown <= 0 || movementDestination == null || position().distanceToSqr(movementDestination) < 1.0) {
            movementDestination = findHoverDestination();
            repositionCooldown = 20;
        }
        moveToward(movementDestination, targetPosition(), 0.0525, 1.05, 10.0F, 89.0F);
    }

    private void shoot(LivingEntity target) {
        attackCooldown = ATTACK_COOLDOWN;
        shotThisTick = true;
        attackAnimationTicks = 6;
        SummonContainer.of(owner()).addProjectile(SummonProjectileTypes.HORNET_STINGER.create(this, target));
    }

    /// 在当前视线前方寻找新的悬停点，对应 1.21 侧 {@code HoverRandomPos} 的可观察行为。
    ///
    /// <p>新架构没有用于导航的世界实体，因此直接检查候选位置的方块碰撞。</p>
    private Vec3 findHoverDestination() {
        double facing = Math.toRadians(currentPose().yaw());
        Vec3 fallback = position().add(Vec3.directionFromRotation(0.0F, currentPose().yaw()).scale(4.0));
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = facing + Mth.nextDouble(owner().getRandom(), -Math.PI * 0.5, Math.PI * 0.5);
            double distance = Mth.nextDouble(owner().getRandom(), 3.0, 8.0);
            Vec3 candidate = position().add(-Math.sin(angle) * distance, Mth.nextDouble(owner().getRandom(), -3.0, 7.0), Math.cos(angle) * distance);
            if (owner().level().noCollision(new AABB(candidate.x - 0.3, candidate.y - 0.3, candidate.z - 0.3, candidate.x + 0.3, candidate.y + 0.3, candidate.z + 0.3))) {
                return candidate;
            }
        }
        return fallback;
    }

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(false, attackAnimationTicks > 0 ? SummonAnimation.MELEE_ATTACK : SummonAnimation.NONE,
                6 - attackAnimationTicks, 6, 0.0F, 1.0F, 1.0F);
    }

    private static final class AttackGoal extends SummonGoal<HornetSummon> {
        private AttackGoal(HornetSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return summon.target() != null;
        }

        @Override
        public void tick() {
            summon.combat(summon.target());
        }
    }

}
