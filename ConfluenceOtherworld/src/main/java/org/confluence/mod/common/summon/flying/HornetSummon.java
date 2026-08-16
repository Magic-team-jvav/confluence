package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.entity.projectile.summon.SummonBoltEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.summon.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;

/// 黄蜂召唤物的运行实例。
///
/// <p>这里保留 1.21 侧的悬停、瞄准、短间隔毒刺射击和蜂巢背包攻速加成。
/// 新架构只负责取消真实实体依赖，不能改变玩家能观察到的战斗节奏。</p>
public final class HornetSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 8.0F;
    private static final int ATTACK_COOLDOWN = 10;
    private static final int HIVE_PACK_ATTACK_COOLDOWN = 7;
    private int attackCooldown;
    private int attackAnimationTicks;
    private int repositionCooldown;
    private Vec3 movementDestination;

    public HornetSummon(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(Confluence.asResource("hornet_baby"), owner, slotCost, snapshot, initialPose);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new FollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), 84.0);
    }

    @Override
    protected void beforeGoalTick() {
        repositionCooldown--;
        attackAnimationTicks = Math.max(0, attackAnimationTicks - 1);
    }

    private void combat(LivingEntity target) {
        if (attackCooldown > 0 && --attackCooldown > 0) {
            keepOnTarget();
            return;
        }
        keepOnTarget();
        shoot(target);
    }

    private void keepOnTarget() {
        if (repositionCooldown <= 0 || movementDestination == null || position().distanceToSqr(movementDestination) < 1.0) {
            movementDestination = findHoverDestination();
            repositionCooldown = 20;
        }
        moveToward(movementDestination, targetPosition(), 0.10, 1.5);
    }

    private void shoot(LivingEntity target) {
        attackCooldown = attackCooldownAfterShot();
        attackAnimationTicks = 10;
        SummonBoltEntity projectile = ModEntities.SUMMON_BOLT.get().create(owner().level());
        if (projectile == null) {
            throw new IllegalStateException("Summon projectile type returned null");
        }
        projectile.configure(this, target, 0xE8C83A, SummonBoltEntity.HitEffect.POISON, 1.0F, 0.0F);
        owner().level().addFreshEntity(projectile);
    }

    private int attackCooldownAfterShot() {
        return TCUtils.hasType(owner(), TCItems.HIVE$PACK) ? HIVE_PACK_ATTACK_COOLDOWN : ATTACK_COOLDOWN;
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
            if (owner().level().noCollision(new AABB(candidate.x - 0.3, candidate.y - 0.3, candidate.z - 0.3,
                    candidate.x + 0.3, candidate.y + 0.3, candidate.z + 0.3))) {
                return candidate;
            }
        }
        return fallback;
    }

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(false, attackAnimationTicks > 0 ? SummonAnimation.MELEE_ATTACK : SummonAnimation.NONE,
                10 - attackAnimationTicks, 10, 0.0F, 1.0F, 1.0F);
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

    private static final class FollowOwnerGoal extends SummonGoal<HornetSummon> {
        private FollowOwnerGoal(HornetSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            summon.followOwner(32.0, 0.10, 0.80, 2.0);
        }
    }
}
