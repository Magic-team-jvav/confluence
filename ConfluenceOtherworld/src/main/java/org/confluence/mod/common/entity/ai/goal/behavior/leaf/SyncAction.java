package org.confluence.mod.common.entity.ai.goal.behavior.leaf;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;
import org.confluence.mod.common.entity.ai.goal.behavior.BTNode;

import java.util.function.Supplier;

/**
 * 同步实体数据
 * @param <T>
 */
public class SyncAction<T> extends BTNode {
    final EntityDataAccessor<T> data;
    final Supplier<T> dataSupplier;
    final Mob mob;

    public SyncAction(Mob mob, EntityDataAccessor<T> data, Supplier<T> dataSupplier) {
        this.mob = mob;
        this.data = data;
        this.dataSupplier = dataSupplier;
    }

    @Override
    public BTStatus execute() {
        mob.getEntityData().set(data, dataSupplier.get());
        return BTStatus.SUCCESS;
    }
}
