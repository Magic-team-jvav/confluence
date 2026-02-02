package org.confluence.terra_curio.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.util.TCUtils;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public abstract class TargetingConditionsMixin {
    @Inject(method = "test", at = @At(value = "RETURN", ordinal = 7), cancellable = true)
    private void unTest(@Nullable LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() && attacker != null && attacker.getType().is(TCUtils.getValue(target, TCItems.MOB$IGNORE))) {
            cir.setReturnValue(false);
        }
    }
}
