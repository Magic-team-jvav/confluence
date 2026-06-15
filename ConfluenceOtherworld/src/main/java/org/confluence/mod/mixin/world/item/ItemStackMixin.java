package org.confluence.mod.mixin.world.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = ItemStack.class, priority = 1100)
public abstract class ItemStackMixin implements Immunity {
    @Shadow
    public abstract Item getItem();

    @Override
    public Type confluence$getImmunityType() {
        return Type.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource) {
        if (getItem() instanceof Immunity immunity) {
            return immunity.confluence$getImmunityDuration(damageSource);
        }
        return Immunity.super.confluence$getImmunityDuration(damageSource);
    }
}
