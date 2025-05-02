package org.confluence.mod.mixin.level;

import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.GeodeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import org.confluence.mod.common.CommonConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GeodeFeature.class)
public abstract class GeodeFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void deny(FeaturePlaceContext<GeodeConfiguration> p_159836_, CallbackInfoReturnable<Boolean> cir) {
        if (CommonConfigs.REPLACE_VANILLA_GEODE_FEATURE.get()) cir.setReturnValue(false);
    }
}
