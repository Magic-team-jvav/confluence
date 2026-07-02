package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;

/**
 * 与目标距离小于指定值。
 */
public class DistanceLowerThanCondition extends Condition<Mob> {
    private final double distance;

    public DistanceLowerThanCondition(Mob mob, double distance) {
        super(mob);
        this.distance = distance;
    }

    @Override
    protected boolean test() {
        return mob.getTarget() != null && mob.distanceToSqr(mob.getTarget()) < distance * distance;
    }
}
