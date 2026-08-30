package org.confluence.mod.mixed;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.jetbrains.annotations.ApiStatus;

@Deprecated(since = "1.3.0", forRemoval = true)
@ApiStatus.ScheduledForRemoval(inVersion = "1.4.0")
public interface IBaseContainerBlockEntity {
    void confluence$setCustomName(Component name);

    static IBaseContainerBlockEntity of(BaseContainerBlockEntity entity) {
        return (IBaseContainerBlockEntity) entity;
    }
}
