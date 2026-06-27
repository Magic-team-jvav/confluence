package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;

public class HasTargetCondition extends Condition<Mob> {
    public HasTargetCondition(Mob mob) { super(mob); }
    @Override
    protected boolean test() { return mob.getTarget() != null && mob.getTarget().isAlive(); }
}
