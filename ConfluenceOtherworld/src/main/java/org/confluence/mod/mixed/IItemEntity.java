package org.confluence.mod.mixed;

import net.minecraft.world.entity.item.ItemEntity;

public interface IItemEntity {
    void confluence$item_setCoolDown(int ticks);

    static IItemEntity of(ItemEntity entity) {
        return (IItemEntity) entity;
    }
}
