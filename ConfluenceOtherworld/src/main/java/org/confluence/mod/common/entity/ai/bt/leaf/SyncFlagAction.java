package org.confluence.mod.common.entity.ai.bt.leaf;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.bt.BTNode;
import org.confluence.mod.common.entity.ai.bt.BTStatus;

/**
 * 同步一个标记位到 EntityData（用于触发客户端动画/渲染），立即返回 SUCCESS。
 */
public class SyncFlagAction<T extends Mob> extends BTNode {
    private final T mob;
    private final EntityDataAccessor<Integer> accessor;
    private final int flagIndex;
    private final boolean value;

    public SyncFlagAction(T mob, EntityDataAccessor<Integer> accessor, int flagIndex, boolean value) {
        this.mob = mob;
        this.accessor = accessor;
        this.flagIndex = flagIndex;
        this.value = value;
    }

    @Override
    public void start() {
        int current = mob.getEntityData().get(accessor);
        int updated = value ? (current | (1 << flagIndex)) : (current & ~(1 << flagIndex));
        mob.getEntityData().set(accessor, updated);
    }

    @Override
    public BTStatus execute() {
        return BTStatus.SUCCESS;
    }
}
