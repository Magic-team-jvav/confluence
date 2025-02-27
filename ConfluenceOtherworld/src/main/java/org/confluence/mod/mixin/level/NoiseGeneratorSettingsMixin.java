package org.confluence.mod.mixin.level;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.confluence.mod.common.init.ModSecretSeeds;
import org.confluence.mod.common.worldgen.secret_seed.DrunkWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin {
    /**
     * @see LevelMixin#modify(CallbackInfoReturnable)
     */
    @Inject(method = "seaLevel", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        if (ModSecretSeeds.DRUNK_WORLD.match()) {
            cir.setReturnValue(DrunkWorld.SEA_LEVEL);
        }
    }
}
