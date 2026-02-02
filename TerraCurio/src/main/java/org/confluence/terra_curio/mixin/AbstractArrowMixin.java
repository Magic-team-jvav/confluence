package org.confluence.terra_curio.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.confluence.lib.mixed.SelfGetter;
import org.confluence.terra_curio.common.init.TCAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin implements SelfGetter<AbstractArrow> {
    @ModifyVariable(method = "doKnockback", at = @At(value = "STORE", ordinal = 0), ordinal = 0)
    private double modify(double original) {
        return TCAttributes.applyArrowKnockback(confluence$self().getOwner(), original);
    }
}
