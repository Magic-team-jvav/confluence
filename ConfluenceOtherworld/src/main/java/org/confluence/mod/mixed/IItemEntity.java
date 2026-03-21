package org.confluence.mod.mixed;

import net.minecraft.world.entity.item.ItemEntity;
import org.confluence.lib.mixed.SelfGetter;

public interface IItemEntity extends SelfGetter<ItemEntity> {
    void confluence$item_setCoolDown(int ticks);

    static IItemEntity of(ItemEntity entity) {
        return (IItemEntity) entity;
    }
}
