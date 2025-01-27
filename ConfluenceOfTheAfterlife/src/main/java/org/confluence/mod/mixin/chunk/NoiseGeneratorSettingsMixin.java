package org.confluence.mod.mixin.chunk;

import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import org.confluence.mod.common.worldgen.secret_seed.DrunkWorld;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NoiseGeneratorSettings.class)
public abstract class NoiseGeneratorSettingsMixin {
    @Unique
    private Boolean confluence$isDrunkWorld;

    /**
     * @see org.confluence.mod.mixin.level.LevelMixin#modify(CallbackInfoReturnable)
     */
    @Inject(method = "seaLevel", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        if (confluence$isDrunkWorld == null) {
            this.confluence$isDrunkWorld = ModSecretSeeds.DRUNK_WORLD.match();
        }
        if (confluence$isDrunkWorld) {
            cir.setReturnValue(DrunkWorld.SEA_LEVEL);
        }
    }
}
