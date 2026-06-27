package org.confluence.mod.common.entity.ai.bt.condition;

import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 条件节点：本身无状态，只做一次判断。
 */
public abstract class Condition<T extends Mob> extends BTNode {
    protected final T mob;

    protected Condition(T mob) {
        this.mob = mob;
    }

    @Override
    public BTStatus execute() {
        return test() ? BTStatus.SUCCESS : BTStatus.FAILURE;
    }

    protected abstract boolean test();
}
