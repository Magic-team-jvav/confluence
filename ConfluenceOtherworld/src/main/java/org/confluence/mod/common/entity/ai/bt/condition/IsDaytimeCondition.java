package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;

public class IsDaytimeCondition extends Condition<Mob> {
    public IsDaytimeCondition(Mob mob) { super(mob); }
    @Override
    protected boolean test() { return mob.level().isDay(); }
}
