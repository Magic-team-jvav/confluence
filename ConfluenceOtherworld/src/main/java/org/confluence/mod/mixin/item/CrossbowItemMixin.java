package org.confluence.mod.mixin.item;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
    @WrapMethod(method = "isCharged")
    private static boolean confluence$isCharged(ItemStack crossbowStack, Operation<Boolean> original) {
        return crossbowStack.getItem() instanceof BaseTerraRepeaterItem baseTerraRepeaterItem ? BaseTerraRepeaterItem.isCharged(crossbowStack) : original.call(crossbowStack);
    }
}
