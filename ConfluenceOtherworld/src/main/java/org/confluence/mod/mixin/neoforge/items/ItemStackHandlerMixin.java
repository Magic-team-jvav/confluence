package org.confluence.mod.mixin.neoforge.items;

import net.minecraftforge.items.ItemStackHandler;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemStackHandler.class, priority = 1100)
public abstract class ItemStackHandlerMixin {
    @Inject(method = "getSlotLimit", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(LibUtils.getMaxStackSize(cir.getReturnValue()));
    }
}
