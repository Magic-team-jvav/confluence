package org.confluence.mod.mixin.chunk;

import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NoiseBasedChunkGenerator.class)
public abstract class NoiseChunkGenMixin {
    @Inject(method = "doCreateBiomes", at = @At("RETURN"))
    private void doCreateBiomes(Blender pBlender, RandomState pRandom, StructureManager pStructureManager, ChunkAccess pChunk, CallbackInfo ci) {
        for (LevelChunkSection section : pChunk.getSections()) {
            // 激进寻找邪恶，如果区块内完全没有邪恶就是完全纯净的，设为备份，净化时还原成备份
            // 如果有邪恶则在净化时设为平原
            if (DynamicBiomeUtils.getTypicalBiome(section, true, null) == null) {
                ((IChunkSection) section).confluence$setBackupBiome(section.getBiomes());
            }
        }
    }
}
