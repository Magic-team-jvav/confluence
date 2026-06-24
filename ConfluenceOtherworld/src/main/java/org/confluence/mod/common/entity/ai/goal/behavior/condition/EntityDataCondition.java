package org.confluence.mod.common.entity.ai.goal.behavior.condition;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.Mob;

import java.util.function.Predicate;

/**
 * 实体数据值条件
 */
public class EntityDataCondition<T>  extends AbstractConditionLeaf {

    final Mob mob;
    final EntityDataAccessor<T> dataAccessor;
    final Predicate<T> predicate;

    public EntityDataCondition(Mob mob, EntityDataAccessor<T> dataAccessor, Predicate<T> predicate) {
        this.mob = mob;
        this.dataAccessor = dataAccessor;
        this.predicate = predicate;
    }

    @Override
    public boolean check() {
        return predicate.test(mob.getEntityData().get(dataAccessor));
    }
}
