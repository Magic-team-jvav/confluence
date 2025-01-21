package org.confluence.mod.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyInstance.class)
public abstract class DifficultyInstanceMixin {
    @Inject(method = "calculateDifficulty", at = @At("RETURN"), cancellable = true)
    private void levelUp(Difficulty difficulty, long levelTime, long chunkInhabitedTime, float moonPhaseFactor, CallbackInfoReturnable<Float> cir) {
        if (ModSecretSeeds.FOR_THE_WORTHY.match()) {
            cir.setReturnValue(cir.getReturnValue() + 1.0F);
        }
    }
}
