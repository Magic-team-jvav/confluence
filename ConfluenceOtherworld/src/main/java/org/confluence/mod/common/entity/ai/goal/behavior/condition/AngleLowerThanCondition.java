package org.confluence.mod.common.entity.ai.goal.behavior.condition;

import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.utils.TEUtils;

public class AngleLowerThanCondition extends AbstractConditionLeaf {
    final Mob mob;
    final double angle;

    public AngleLowerThanCondition(Mob mob, double angle) {
        this.mob = mob;
        this.angle = angle;
    }

    @Override
    public boolean check() {
        if (mob.getTarget() == null) {
            return false;
        }
        return TEUtils.angleBetween(mob.getDeltaMovement(), mob.getTarget().position().subtract(mob.position())) < angle;
    }
}
