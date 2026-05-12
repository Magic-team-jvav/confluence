package org.confluence.mod.mixin.world.item;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @ModifyReturnValue(method = "isCharged", at = @At("RETURN"))
    private static boolean confluence$isCharged(boolean original, @Local(argsOnly = true) ItemStack crossbowStack) {
        return original || crossbowStack.getItem() instanceof BaseTerraRepeaterItem && BaseTerraRepeaterItem.isCharged(crossbowStack);
    }
}
