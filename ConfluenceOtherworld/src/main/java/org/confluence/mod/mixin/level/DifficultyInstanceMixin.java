package org.confluence.mod.mixin.level;

import net.minecraft.world.DifficultyInstance;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyInstance.class)
public abstract class DifficultyInstanceMixin {
    @Inject(method = "calculateDifficulty", at = @At("RETURN"), cancellable = true)
    private void levelUp(CallbackInfoReturnable<Float> cir) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            cir.setReturnValue(cir.getReturnValue() + 1.0F);
        }
    }
}
