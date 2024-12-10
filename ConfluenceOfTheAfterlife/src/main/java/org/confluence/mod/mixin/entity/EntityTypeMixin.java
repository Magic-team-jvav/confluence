package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.EntityType;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EntityType.class)
public class EntityTypeMixin implements Immunity {
    @Override
    public Types confluence$getImmunityType(){
        return Types.STATIC;
    }
}
