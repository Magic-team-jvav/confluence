package org.confluence.mod.common.summon.flying;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.summon.SummonTargetCache;
import org.confluence.mod.common.summon.*;
import org.confluence.mod.common.summon.projectile.SummonProjectileTypes;

/// 小鬼召唤物的运行实例。
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

    public ImpSummon(ServerPlayer owner, int slotCost, SummonStats stats, SummonPose initialPose) {
        super(Confluence.asResource("summon_imp"), owner, slotCost, stats, initialPose, 1.0, 1.0);
        addGoal(1, new AttackGoal(this));
        addGoal(9, new MomentumSummonIdleGoal<>(this, 1.8, 0.035, 0.70));
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
            if (target != null) {
                fire(target);
            }
        }
    }

    private void fire(LivingEntity target) {
        SummonContainer.of(owner()).addProjectile(SummonProjectileTypes.IMP_FIREBALL.create(this, target));
    }

    private void combat(LivingEntity target) {
        hoverNear(targetBasePosition(), targetPosition(), 5.0, 3.0, 5.0, 0.0525, 0.03, 1.05);
        if (--attackCooldown > 0) {
            return;
        }
        attackCooldown = ATTACK_COOLDOWN;
        attackAnimationTicks = ATTACK_ANIMATION_TICKS;
        delayedTarget = target;
        delayedAttackTicks = ATTACK_DELAY - 1;
    }

    @Override
    public SummonVisualState visualState() {
        return attackAnimationTicks > 0
                ? new SummonVisualState(false, SummonAnimation.MELEE_ATTACK,
                ATTACK_ANIMATION_TICKS - attackAnimationTicks, ATTACK_ANIMATION_TICKS, 0.0F, 1.0F, 1.0F)
                : SummonVisualState.DEFAULT;
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

}
