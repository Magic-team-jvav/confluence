package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.projectile.Projectile;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Projectile.class)
public abstract class ProjectileMixin implements Immunity {
    @Override
    public Types confluence$getImmunityType() {
        return Types.LOCAL;
    }
}
