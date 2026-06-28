package org.confluence.mod.common.entity.ai.bt.condition;

import org.confluence.mod.common.entity.monster.slime.BaseSlime;

public class CanBeHostileCondition extends Condition<BaseSlime> {
    public CanBeHostileCondition(BaseSlime slime) {
        super(slime);
    }

    @Override
    protected boolean test() {
        return mob.canBeHostile();
    }
}
