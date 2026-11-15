package org.confluence.mod.mixed;

import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.SurfaceRules;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;
import org.jetbrains.annotations.Nullable;

public interface IInjectChunkGenerator {
    SurfaceRules.RuleSource confluence$getOriginalSurfaceRules();

    void confluence$setOverrideSurfaceRules(SurfaceRules.RuleSource rules);

    BiomeSource confluence$getGenerationBiomeSource();

    default @Nullable BannedBiomeNoiseBasedChunkGenerator confluence$getBannedGenerator() {
        return this instanceof BannedBiomeNoiseBasedChunkGenerator generator ? generator : null;
    }
}
