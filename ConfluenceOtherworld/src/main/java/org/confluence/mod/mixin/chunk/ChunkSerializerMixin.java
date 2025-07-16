package org.confluence.mod.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Shadow
    private static void logErrors(ChunkPos pChunkPos, int pChunkSectionY, String pErrorMessage) {}

    @Inject(method = "write", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBiomes()Lnet/minecraft/world/level/chunk/PalettedContainerRO;"))
    private static void write(ServerLevel pLevel, ChunkAccess pChunk, CallbackInfoReturnable<CompoundTag> cir, @Local Codec<PalettedContainerRO<Holder<Biome>>> codec, @Local(ordinal = 1) CompoundTag sectionTag, @Local LevelChunkSection levelchunksection) {
        sectionTag.put("backup_biome", codec.encodeStart(NbtOps.INSTANCE, ((IChunkSection) levelchunksection).confluence$getBackupBiome()).getOrThrow());
    }

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"))
    private static void read(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, @Local Codec<PalettedContainerRO<Holder<Biome>>> codec, @Local(ordinal = 1) CompoundTag compoundtag, @Local(ordinal = 2) int k, @Local Registry<Biome> registry, @Local LevelChunkSection levelchunksection) {
        // 从原来的方法里面抄的
        PalettedContainerRO<Holder<Biome>> bakBiome;
        if (compoundtag.contains("backup_biome", 10)) {
            bakBiome = codec.parse(NbtOps.INSTANCE, compoundtag.getCompound("backup_biome")).promotePartial((p_188274_) -> {
                logErrors(pos, k, p_188274_);
            }).getOrThrow(ChunkSerializer.ChunkReadException::new);
        } else {
            bakBiome = DynamicBiomeUtils.judgeBackupBiome(levelchunksection);
        }
        ((IChunkSection) levelchunksection).confluence$setBackupBiome(bakBiome);
    }
}
