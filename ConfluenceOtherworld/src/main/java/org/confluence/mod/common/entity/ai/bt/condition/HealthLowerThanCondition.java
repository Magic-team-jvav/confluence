package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;

/**
 * 血量低于百分比阈值。
 */
public class HealthLowerThanCondition extends Condition<Mob> {
    private final float threshold;

    public HealthLowerThanCondition(Mob mob, float threshold) {
        super(mob);
        this.threshold = threshold;
    }

    @Override
    protected boolean test() {
        return mob.getHealth() / mob.getMaxHealth() < threshold;
    }
}
