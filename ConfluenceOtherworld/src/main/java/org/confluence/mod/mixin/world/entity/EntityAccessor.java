package org.confluence.mod.mixin.world.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {
    @Invoker
    void callReadAdditionalSaveData(CompoundTag nbt);

    // 1.20 侧暂时通过访问器暴露实体碰撞计算；如果后续统一访问转换，再迁回对应的 AT 声明。
    @Invoker
    Vec3 callCollide(Vec3 motion);
}
