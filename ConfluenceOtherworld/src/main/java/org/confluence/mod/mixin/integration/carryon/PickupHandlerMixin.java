package org.confluence.mod.mixin.integration.carryon;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.confluence.mod.integration.carryon.CarryOnHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "tschipp.carryon.common.carry.PickupHandler", remap = false)
public abstract class PickupHandlerMixin {
    @Inject(method = "tryPickUpBlock", at = @At("HEAD"), cancellable = true)
    private static void deny(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Level level) {
        if (CarryOnHelper.shouldDeny(level.getBlockState(pos))) {
            cir.setReturnValue(false);
        }
    }
}
