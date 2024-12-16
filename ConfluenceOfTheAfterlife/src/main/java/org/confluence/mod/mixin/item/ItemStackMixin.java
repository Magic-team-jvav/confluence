package org.confluence.mod.mixin.item;

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
    public int confluence$getImmunityDuration(){
        // 只有Item确定是Immunity了才会调这里
        return ((Immunity)getItem()).confluence$getImmunityDuration();
    }
}
