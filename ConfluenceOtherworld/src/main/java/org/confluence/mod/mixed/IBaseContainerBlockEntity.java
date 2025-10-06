package org.confluence.mod.mixed;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;

public interface IBaseContainerBlockEntity {
    void confluence$setCustomName(Component name);

    static IBaseContainerBlockEntity of(BaseContainerBlockEntity entity) {
        return (IBaseContainerBlockEntity) entity;
    }
}
