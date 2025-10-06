package org.confluence.mod.mixin.block;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.confluence.mod.mixed.IBaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(BaseContainerBlockEntity.class)
public abstract class BaseContainerBlockEntityMixin implements IBaseContainerBlockEntity {
    @Shadow
    @Nullable
    private Component name;

    @Override
    public void confluence$setCustomName(Component name) {
        this.name = name;
    }
}
