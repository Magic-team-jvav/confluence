package org.confluence.mod.mixin.level;

import net.minecraft.world.level.Level;
import org.confluence.mod.common.worldgen.secret_seed.DrunkWorld;
import org.confluence.mod.common.worldgen.secret_seed.ModSecretSeeds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Unique
    private Boolean confluence$isDrunkWorld;

    /**
     * @see NoiseGeneratorSettingsMixin#modify(CallbackInfoReturnable)
     */
    @Inject(method = "getSeaLevel", at = @At("RETURN"), cancellable = true)
    private void modify(CallbackInfoReturnable<Integer> cir) {
        if (confluence$isDrunkWorld == null) {
            this.confluence$isDrunkWorld = ModSecretSeeds.DRUNK_WORLD.match();
        }
        if (confluence$isDrunkWorld) {
            cir.setReturnValue(DrunkWorld.SEA_LEVEL);
        }
    }
}
