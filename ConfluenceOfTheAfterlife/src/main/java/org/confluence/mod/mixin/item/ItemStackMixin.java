package org.confluence.mod.mixin.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.mixed.Immunity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements Immunity {
    @Shadow public abstract Item getItem();

    @Override
    public Types confluence$getImmunityType(){
        return Types.LOCAL;
    }

    @Override
    public int confluence$getImmunityDuration(DamageSource damageSource){
        if(getItem() instanceof Immunity immunity){
            return immunity.confluence$getImmunityDuration(damageSource);
        }
        return Immunity.super.confluence$getImmunityDuration(damageSource);
    }
}
