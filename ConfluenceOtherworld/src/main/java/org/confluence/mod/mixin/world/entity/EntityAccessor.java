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

    /// 1.20 侧这份 accessor 就有这一条（注释写着 `todo AT`），1.21 侧漏了；
    /// `ReboundingFlyingMonster` 需要它来调用 `Entity` 的私有 `collide(Vec3)`
    /// （`Entity.java:901` 是 `private Vec3 collide(Vec3 vec)`）。
    @Invoker
    Vec3 callCollide(Vec3 motion);
}
