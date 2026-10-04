package org.confluence.mod.mixin.integration.terracurio;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TCUtils.class, remap = false)
public abstract class TCUtilsMixin {
    @Inject(method = "isFluidWalkable", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private static void waterWalkingEffect(CallbackInfoReturnable<Boolean> cir, @Local(argsOnly = true) LivingEntity living) {
        if (!cir.getReturnValue() && living.hasEffect(ModEffects.WATER_WALKING)) {
            cir.setReturnValue(true);
        }
    }
}
