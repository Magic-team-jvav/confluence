package org.confluence.mod.mixin.level;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.ModTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TreeFeature.class)
public abstract class TreeFeatureMixin {
    @Inject(method = "place", at = @At("HEAD"), cancellable = true)
    private void replace(FeaturePlaceContext<TreeConfiguration> context, CallbackInfoReturnable<Boolean> cir) {
        if (context.level().getBiome(context.origin()).is(ModTags.Biomes.YELLOW_WILLOW_REPLACEABLE) && context.random().nextFloat() < 0.05F) {
            boolean placed = context.level().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE)
                    .getHolder(ModFeatures.CONFIGURED_YELLOW_WILLOW).orElseThrow().value()
                    .place(context.level(), context.chunkGenerator(), context.random(), context.origin());
            if (placed) cir.setReturnValue(true);
        }
    }
}
