package org.confluence.mod.mixin.world;

import net.minecraft.world.Container;
import org.confluence.lib.util.LibUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Container.class, priority = 1100)
public interface ContainerMixin {
    @Inject(method = "getMaxStackSize()I", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(LibUtils.getMaxStackSize(cir.getReturnValue()));
    }
}
