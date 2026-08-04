package org.confluence.mod.mixin.neoforge.items;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ItemStackHandler.class, priority = 1100)
public abstract class ItemStackHandlerMixin {
    @ModifyReturnValue(method = "getSlotLimit", at = @At("RETURN"))
    private int modify(int original) {
        return LibUtils.getMaxStackSize(original);
    }
}
