package org.confluence.mod.mixin.world.level.levelgen;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseBasedChunkGeneratorMixin {
    @Inject(method = "doCreateBiomes", at = @At("RETURN"))
    private void doCreateBiomes(CallbackInfo ci, @Local(argsOnly = true) StructureManager structureManager, @Local(argsOnly = true) ChunkAccess chunk) {
        HolderLookup.RegistryLookup<Biome> lookup = structureManager.registryAccess().lookupOrThrow(Registries.BIOME);
        for (LevelChunkSection section : chunk.getSections()) {
            ILevelChunkSection.of(section).confluence$setBackupBiome(DynamicBiomeUtils.judgeBackupBiome(section, lookup));
        }
    }
}
