package org.confluence.mod.mixin;

import net.minecraft.world.food.FoodData;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {
    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float doubleExhaustion(float exhaustion) {
        if (ModSecretSeeds.THE_CONSTANT.match()) {
            return exhaustion * 2;
        }
        return exhaustion;
    }
}
