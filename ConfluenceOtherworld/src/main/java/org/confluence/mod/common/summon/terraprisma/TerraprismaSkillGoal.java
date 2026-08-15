package org.confluence.mod.common.summon.terraprisma;

import org.confluence.mod.common.summon.SummonGoal;

/**
 * 为泰拉棱镜技能提供持续时间与冷却。
 */
abstract class TerraprismaSkillGoal extends SummonGoal<TerraprismaSummon> {
    private final int duration;
    private final int baseCooldown;
    protected int elapsedTicks;
    private int cooldown;

    TerraprismaSkillGoal(TerraprismaSummon summon, int duration, int baseCooldown) {
        super(summon);
        this.duration = duration;
        this.baseCooldown = baseCooldown;
    }

    @Override
    public boolean canUse() {
        return cooldown == 0 && summon.hasValidTarget();
    }

    @Override
    public boolean canContinueToUse() {
        return summon.hasValidTarget() && elapsedTicks < duration;
    }

    @Override
    public boolean isInterruptible() {
        return false;
    }

    @Override
    public void start() {
        elapsedTicks = 0;
        summon.setSkillDamageMultiplier(1.3F);
    }

    @Override
    public void stop() {
        elapsedTicks = 0;
        summon.setSkillDamageMultiplier(1.0F);
        int randomRange = Math.max(1, (int) (baseCooldown * 0.3F));
        cooldown = baseCooldown + summon.owner().getRandom().nextInt(randomRange);
    }

    final void updateCooldown() {
        cooldown = Math.max(0, cooldown - 1);
    }

    final int cooldown() {
        return cooldown;
    }
}
