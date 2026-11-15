package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/// 检查当前有效目标是否进入指定距离。
public final class TargetWithinRangeCondition extends Condition<Mob> {
    private final double rangeSquared;

    public TargetWithinRangeCondition(Mob mob, double range) {
        super(mob);
        if (range < 0.0D) {
            throw new IllegalArgumentException("Target range must be non-negative");
        }
        this.rangeSquared = range * range;
    }

    @Override
    protected boolean test() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && mob.distanceToSqr(target) <= rangeSquared;
    }
}
