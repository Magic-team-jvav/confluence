package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.lib.api.projectile.ProjectileCombatSnapshot;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.entity.projectile.summon.SummonBoltEntity;
import org.confluence.mod.common.init.entity.ModEntities;
import org.confluence.mod.common.summon.FlyingSummon;
import org.confluence.mod.common.summon.SummonAnimation;
import org.confluence.mod.common.summon.SummonGoal;
import org.confluence.mod.common.summon.SummonPose;
import org.confluence.mod.common.summon.SummonVisualState;

/**
 * 小鬼召唤物的运行实例。
 *
 * <p>行为保留 1.21 侧的施法延迟、火焰弹命中效果和悬停射击节奏；这里只把真实实体 AI
 * 改成玩家召唤容器驱动的逻辑实例。</p>
 */
public final class ImpSummon extends FlyingSummon {
    public static final int SLOT_COST = 1;
    public static final float BASE_DAMAGE = 14.0F;
    private static final double SEARCH_RANGE = 84.0;
    private static final int ATTACK_COOLDOWN = 20;
    private static final int ATTACK_DELAY = 15;
    private static final int ATTACK_ANIMATION_TICKS = 18;
    private int attackCooldown;
    private int attackAnimationTicks;
    private int delayedAttackTicks = -1;
    private LivingEntity delayedTarget;

    public ImpSummon(ServerPlayer owner, int slotCost, ProjectileCombatSnapshot snapshot, SummonPose initialPose) {
        super(Confluence.asResource("summon_imp"), owner, slotCost, snapshot, initialPose);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new FollowOwnerGoal(this));
    }

    @Override
    protected LivingEntity findTarget() {
        return SummonTargetCache.acquire(owner().serverLevel(), owner(), uuid(), position(), SEARCH_RANGE);
    }

    @Override
    protected void beforeGoalTick() {
        attackAnimationTicks = Math.max(0, attackAnimationTicks - 1);
        if (delayedAttackTicks >= 0 && --delayedAttackTicks == 0) {
            LivingEntity target = delayedTarget;
            delayedTarget = null;
            delayedAttackTicks = -1;
            if (target != null && target.isAlive()) {
                fire(target);
            }
        }
    }

    private void fire(LivingEntity target) {
        SummonBoltEntity projectile = ModEntities.SUMMON_BOLT.get().create(owner().level());
        if (projectile == null) {
            throw new IllegalStateException("Summon projectile type returned null");
        }
        projectile.configure(this, target, 0xFF632E, SummonBoltEntity.HitEffect.IGNITE, 1.0F, 1.0F);
        owner().level().addFreshEntity(projectile);
    }

    private void combat(LivingEntity target) {
        hoverNear(targetBasePosition(), targetPosition(), 5.0, 3.0, 5.0, 0.08, 0.03, 0.75);
        if (--attackCooldown > 0) {
            return;
        }
        attackCooldown = ATTACK_COOLDOWN;
        attackAnimationTicks = ATTACK_ANIMATION_TICKS;
        delayedTarget = target;
        delayedAttackTicks = ATTACK_DELAY;
    }

    @Override
    public SummonVisualState visualState() {
        return new SummonVisualState(false, attackAnimationTicks > 0 ? SummonAnimation.MELEE_ATTACK : SummonAnimation.NONE,
                ATTACK_ANIMATION_TICKS - attackAnimationTicks, ATTACK_ANIMATION_TICKS, 0.0F, 1.0F, 1.0F);
    }

    private static final class AttackGoal extends SummonGoal<ImpSummon> {
        private AttackGoal(ImpSummon summon) {
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

    private static final class FollowOwnerGoal extends SummonGoal<ImpSummon> {
        private FollowOwnerGoal(ImpSummon summon) {
            super(summon);
        }

        @Override
        public boolean canUse() {
            return true;
        }

        @Override
        public void tick() {
            summon.followOwner(32.0, 0.10, 0.80);
        }
    }
}
