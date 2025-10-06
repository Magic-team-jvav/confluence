package org.confluence.mod.mixin.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class EntityTypeMixin implements Immunity {
    @Override
    public Type confluence$getImmunityType() {
        return Type.STATIC;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        if (damageSource.getDirectEntity() instanceof Immunity im) {
            return im.confluence$getImmunityDuration(damageSource);
        }
        return Immunity.super.confluence$getImmunityDuration(damageSource);
    }
}
