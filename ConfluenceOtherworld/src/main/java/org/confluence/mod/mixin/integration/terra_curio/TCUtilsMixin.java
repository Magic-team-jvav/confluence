package org.confluence.mod.mixin.integration.terra_curio;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.confluence.mod.common.init.ModEffects;
import org.confluence.mod.util.PlayerUtils;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TCUtils.class, remap = false)
public abstract class TCUtilsMixin {
    @Inject(method = "isFluidWalkable", at = @At(value = "RETURN", ordinal = 3), cancellable = true)
    private static void waterWalkingEffect(LivingEntity living, FluidState fluidState, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && living.hasEffect(ModEffects.WATER_WALKING)) {
            cir.setReturnValue(true);
        }
    }

    @ModifyReturnValue(method = "applyFrozenImmune", at = @At(value = "RETURN", ordinal = 1))
    private static boolean modify(boolean original, @Local(argsOnly = true) LivingEntity living) {
        if (original && living instanceof Player player && PlayerUtils.hasPrimitiveType(player, TCItems.FROZEN$IMMUNE)) {
            return false;
        }
        return original;
    }
}
