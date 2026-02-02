package org.confluence.terraentity.entity.ai.goal.behavior.condition;

import net.minecraft.world.entity.Mob;

/**
 * 目标存在条件
 */
public class TargetExistCondition extends AbstractConditionLeaf {
    final Mob mob;
    public TargetExistCondition(Mob mob) {
        this.mob = mob;
    }
    @Override
    public boolean check() {
        return mob.getTarget() != null;
    }

    @Override
    public String getDesc() {
        return "Target Exist";
    }
}
