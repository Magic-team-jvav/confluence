package org.confluence.mod.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixed.IPalettedContainer;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static org.confluence.mod.util.DynamicBiomeUtils.PRIORITY;
import static org.confluence.mod.util.DynamicBiomeUtils.judgeSection;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
    @Shadow
    @Final
    Level level;

    private LevelChunkMixin(ChunkPos pChunkPos, UpgradeData pUpgradeData, LevelHeightAccessor pLevelHeightAccessor, Registry<Biome> pBiomeRegistry, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable BlendingData pBlendingData) {
        super(pChunkPos, pUpgradeData, pLevelHeightAccessor, pBiomeRegistry, pInhabitedTime, pSections, pBlendingData);
    }

    @Inject(method = "<init>(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ProtoChunk;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;)V", at = @At("RETURN"))
    private void protoToLevel(ServerLevel level, ProtoChunk chunk, LevelChunk.PostLoadProcessor postLoad, CallbackInfo ci) {
        DynamicBiomeUtils.applyDynamicBiome(chunk);
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"/*这个位置才开始真正的放方块流程*/))
    private void setBlock(BlockPos pPos, BlockState targetState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir, @Local LevelChunkSection section, @Local(ordinal = 1) BlockState beforeState) {
//        if (confluence$serverLevel == null) return;
        DynamicBiomeUtils.COUNTER.forEach((predicate, consumer) -> {
            boolean before = predicate.test(beforeState);
            boolean after = predicate.test(targetState);
            if(before==after) return;
            if(before){
                consumer.accept(((IChunkSection)section).confluence$getBlockCounts(), -1);
            }
            if(after){
                consumer.accept(((IChunkSection)section).confluence$getBlockCounts(), 1);
            }
        });

        Holder<Biome> resultBiome = DynamicBiomeUtils.judgeSection(section);
        LevelChunkSection aboveSection = confluence$getAboveSection(pPos);
        Holder<Biome> aboveResult = confluence$checkAbove(pPos);
        if (resultBiome != null) {
            confluence$infect(section, resultBiome);
            if (aboveSection == null) return;
            if (aboveResult == null || PRIORITY.getInt(resultBiome.getKey()) < PRIORITY.getInt(aboveResult.getKey())) {
                confluence$infect(aboveSection, resultBiome);
            }
        } else {
            boolean purified = false;
            if (aboveSection != null && aboveResult == null) {
                confluence$purify(aboveSection);
                purified = true;
            }
            Holder<Biome> belowResult = confluence$checkBelow(pPos);
            if (belowResult != null) {
                confluence$infect(section, belowResult);
            } else {
                confluence$purify(section);
                purified = true;
            }
            if (purified && level instanceof ServerLevel serverLevel) {
                serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(List.of(this));
            }
        }
    }

    @Unique
    @Nullable
    private LevelChunkSection confluence$getAboveSection(BlockPos pPos) {
        BlockPos belowPos = pPos.offset(0, 16, 0);
        if (level.isOutsideBuildHeight(belowPos.getY())) return null;
        return getSection(getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(belowPos.getY())));
    }

    @Unique
    @Nullable
    private Holder<Biome> confluence$checkAbove(BlockPos pPos) {
        LevelChunkSection aboveSection = confluence$getAboveSection(pPos);
        if (aboveSection == null) return null;
        return judgeSection(aboveSection);
    }

    @Unique
    @Nullable
    private Holder<Biome> confluence$checkBelow(BlockPos pPos) {
        BlockPos belowPos = pPos.offset(0, -16, 0);
        if (level.isOutsideBuildHeight(belowPos.getY())) return null;
        LevelChunkSection belowSection = getSection(getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(belowPos.getY())));
        return judgeSection(belowSection);
    }

    @Unique
    private void confluence$purify(LevelChunkSection section) {
        if (!(level instanceof ServerLevel)) return;
        if (section.getBiomes().maybeHas(biome -> biome.is(ModTags.Biomes.SPREADABLE))) {
            PalettedContainerRO<Holder<Biome>> backup = ((IChunkSection) section).confluence$getBackupBiome();
            ((IChunkSection) section).confluence$setBiomes(backup);
        }
    }

    @SuppressWarnings("unchecked")
    @Unique
    private void confluence$infect(LevelChunkSection section, @NotNull Holder<Biome> biome) {
        if (((PalettedContainer<Holder<Biome>>) section.getBiomes()).data.palette().getSize() == 1 && section.getBiomes().maybeHas(b -> b == biome)) {
            return;
        }
        // section.setBiomes(section.getBiomes().recreateSingle(biome))
        ((IChunkSection) section).confluence$setBiomes(((IPalettedContainer<Holder<Biome>>) section.getBiomes()).confluence$recreateSingle(biome));
    }
}
