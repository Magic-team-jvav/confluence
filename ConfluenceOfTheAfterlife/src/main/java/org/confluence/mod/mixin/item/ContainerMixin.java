package org.confluence.mod.mixin.item;

import net.minecraft.world.Container;
import org.confluence.mod.util.ModUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Container.class)
public interface ContainerMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(ModUtils.getMaxStackSize(cir.getReturnValue()));
    }
}
