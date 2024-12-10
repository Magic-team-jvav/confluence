package org.confluence.mod.mixin.entity;

import net.minecraft.world.damagesource.DamageType;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(DamageType.class)
public class DamageTypeMixin implements Immunity {
    @Override
    public Types confluence$getImmunityType(){
        return Types.STATIC;
    }
}
