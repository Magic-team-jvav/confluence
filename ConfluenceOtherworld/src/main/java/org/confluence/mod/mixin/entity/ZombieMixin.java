package org.confluence.mod.mixin.entity;

import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.item.MaterialItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Zombie.class)
public abstract class ZombieMixin {
    @Inject(method = "wantsToPickUp", at = @At("HEAD"), cancellable = true)
    private void deny(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(MaterialItems.GEL)) {
            cir.setReturnValue(false);
        }
    }
}
