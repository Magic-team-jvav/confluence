package org.confluence.mod.common.entity.npc.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

/// 为城镇 NPC 提供固定准备时间和冷却时间的远程防御行为。
public class NPCRangedAttackGoal extends Goal {
    private final Mob mob;
    private final RangedAttackMob rangedAttackMob;
    private final double speedModifier;
    private final float attackRangeSqr;
    private final int prepareTime;
    private final int cooldownTime;
    private int prepareTicks;
    private int cooldownTicks;

    public NPCRangedAttackGoal(Mob mob, double speedModifier, float attackRange, int prepareTime, int cooldownTime) {
        if (!(mob instanceof RangedAttackMob rangedAttackMob)) {
            throw new IllegalArgumentException("Mob must implement RangedAttackMob");
        }
        this.mob = mob;
        this.rangedAttackMob = rangedAttackMob;
        this.speedModifier = speedModifier;
        this.attackRangeSqr = attackRange * attackRange;
        this.prepareTime = prepareTime;
        this.cooldownTime = cooldownTime;
        setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;
        boolean coolingDown = cooldownTicks > 0;
        if (coolingDown) cooldownTicks--;
        double distanceSqr = mob.distanceToSqr(target);
        boolean visible = mob.getSensing().hasLineOfSight(target);
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (!visible || distanceSqr > attackRangeSqr) {
            mob.getNavigation().moveTo(target, speedModifier);
            prepareTicks = 0;
        } else {
            mob.getNavigation().stop();
            if (!coolingDown && ++prepareTicks >= prepareTime) {
                rangedAttackMob.performRangedAttack(target, 1.0F);
                prepareTicks = 0;
                cooldownTicks = cooldownTime;
            }
        }
    }

    @Override
    public void stop() {
        prepareTicks = 0;
        mob.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }
}
