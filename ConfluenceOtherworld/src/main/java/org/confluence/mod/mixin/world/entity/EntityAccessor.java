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

    // todo AT
    @Invoker
    Vec3 callCollide(Vec3 motion);
}
