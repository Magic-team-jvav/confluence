package org.confluence.mod.mixin.chunk;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseChunkGenMixin {
    @Inject(method = "doCreateBiomes", at = @At("RETURN"))
    private void doCreateBiomes(Blender blender, RandomState random, StructureManager structureManager, ChunkAccess chunk, CallbackInfo ci) {
        HolderLookup.RegistryLookup<Biome> lookup = structureManager.registryAccess().lookupOrThrow(Registries.BIOME);
        for (LevelChunkSection section : chunk.getSections()) {
            ILevelChunkSection.of(section).confluence$setBackupBiome(DynamicBiomeUtils.judgeBackupBiome(section, lookup));
        }
    }
}
