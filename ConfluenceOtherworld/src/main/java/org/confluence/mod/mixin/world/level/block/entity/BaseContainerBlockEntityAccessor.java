package org.confluence.mod.mixin.world.level.block.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/// 泰拉箱子界面的“重命名”：原版只有铁砧能改容器名字，没有公开的设置方法。
@Mixin(BaseContainerBlockEntity.class)
public interface BaseContainerBlockEntityAccessor {
    @Accessor("name")
    void confluence$setName(net.minecraft.network.chat.Component name);
}
