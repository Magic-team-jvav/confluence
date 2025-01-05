package org.confluence.mod.mixin.chunk;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.levelgen.BelowZeroRetrogen;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.lighting.LevelLightEngine;
import org.confluence.mod.mixed.IChunkSection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ChunkSerializer.class)
public abstract class ChunkSerializerMixin {
    @Shadow
    private static void logErrors(ChunkPos pChunkPos, int pChunkSectionY, String pErrorMessage){
    }

    @Inject(method = "write",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/chunk/LevelChunkSection;getBiomes()Lnet/minecraft/world/level/chunk/PalettedContainerRO;"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void write(ServerLevel pLevel, ChunkAccess pChunk, CallbackInfoReturnable<CompoundTag> cir, ChunkPos chunkpos, CompoundTag compoundtag, BlendingData blendingdata, BelowZeroRetrogen belowzeroretrogen, UpgradeData upgradedata, LevelChunkSection[] alevelchunksection, ListTag listtag, LevelLightEngine levellightengine, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean flag, int i, int j, boolean flag1, DataLayer datalayer, DataLayer datalayer1, CompoundTag sectionTag, LevelChunkSection levelchunksection){
        sectionTag.put("backup_biome", codec.encodeStart(NbtOps.INSTANCE,((IChunkSection)levelchunksection).confluence$getBackupBiome()).getOrThrow());
    }

    @Inject(method = "read",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;checkConsistencyWithBlocks(Lnet/minecraft/core/SectionPos;Lnet/minecraft/world/level/chunk/LevelChunkSection;)V"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void read(ServerLevel level, PoiManager poiManager, RegionStorageInfo regionStorageInfo, ChunkPos pos, CompoundTag tag, CallbackInfoReturnable<ProtoChunk> cir, ChunkPos chunkpos, UpgradeData upgradedata, boolean flag, ListTag listtag, int i, LevelChunkSection[] alevelchunksection, boolean flag1, ChunkSource chunksource, LevelLightEngine levellightengine, Registry<Biome> registry, Codec<PalettedContainerRO<Holder<Biome>>> codec, boolean flag2, int j, CompoundTag compoundtag, int k, int l, PalettedContainer palettedcontainer, PalettedContainerRO palettedcontainerro, LevelChunkSection levelchunksection, SectionPos sectionpos){
        // 从原来的方法里面抄的
        PalettedContainerRO<Holder<Biome>> bakBiome;
        if (compoundtag.contains("backup_biome", 10)) {
            bakBiome = codec.parse(NbtOps.INSTANCE, compoundtag.getCompound("backup_biome")).promotePartial((p_188274_) -> {
                logErrors(pos, k, p_188274_);
            }).getOrThrow(ChunkSerializer.ChunkReadException::new);
        } else {
            bakBiome = new PalettedContainer<>(registry.asHolderIdMap(), registry.getHolderOrThrow(Biomes.PLAINS), PalettedContainer.Strategy.SECTION_BIOMES);
        }
        ((IChunkSection) levelchunksection).confluence$setBackupBiome(bakBiome);
    }
}
