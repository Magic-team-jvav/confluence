package org.confluence.mod.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixed.IPalettedContainer;
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

import static org.confluence.mod.util.DynamicBiomeUtils.*;

/**
 * @author voila
 */
@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin extends ChunkAccess {
    @Shadow
    @Final
    Level level;

    @Unique
    private ServerLevel confluence$serverLevel;

    private LevelChunkMixin(ChunkPos pChunkPos, UpgradeData pUpgradeData, LevelHeightAccessor pLevelHeightAccessor, Registry<Biome> pBiomeRegistry, long pInhabitedTime, @Nullable LevelChunkSection[] pSections, @Nullable BlendingData pBlendingData) {
        super(pChunkPos, pUpgradeData, pLevelHeightAccessor, pBiomeRegistry, pInhabitedTime, pSections, pBlendingData);
    }

    @Inject(method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/chunk/UpgradeData;Lnet/minecraft/world/ticks/LevelChunkTicks;Lnet/minecraft/world/ticks/LevelChunkTicks;J[Lnet/minecraft/world/level/chunk/LevelChunkSection;Lnet/minecraft/world/level/chunk/LevelChunk$PostLoadProcessor;Lnet/minecraft/world/level/levelgen/blending/BlendingData;)V", at = @At("RETURN"))
    private void constr(Level pLevel, ChunkPos pPos, UpgradeData pData, LevelChunkTicks pBlockTicks, LevelChunkTicks pFluidTicks, long pInhabitedTime, LevelChunkSection[] pSections, LevelChunk.PostLoadProcessor pPostLoad, BlendingData pBlendingData, CallbackInfo ci) {
        if (!(level instanceof ServerLevel)) return;
        confluence$serverLevel = (ServerLevel) level;
        if (BIOME_CORRUPT == null) {
            BIOME_CRIMSON = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ModBiomes.TR_CRIMSON);
            BIOME_CORRUPT = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ModBiomes.THE_CORRUPTION);
            BIOME_HALLOW = level.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(ModBiomes.THE_HALLOW);
        }
    }

    @Inject(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getBlock()Lnet/minecraft/world/level/block/Block;"))
    private void setBlock(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir, @Local LevelChunkSection section) {
        if (confluence$serverLevel == null) return;
        IChunkSection counter = (IChunkSection) section;
        int[] i = {counter.confluence$getCrimson(), counter.confluence$getCorrupt(), counter.confluence$getHallow(), counter.confluence$getSunflower()};
        Holder<Biome> targetBiome = balanceEvil(i, counter);

        if (targetBiome != null) {
            confluence$infect(section, pPos, targetBiome, true);
        } else {
            confluence$checkBelow(section, pPos);
        }
    }

    /**
     * @return 返回十字上方的该有的邪恶群系，null则是净化，让infect方法决定用平原还是恢复原群系
     */
    @Unique
    private Holder<Biome> confluence$checkCross(LevelChunkSection centerSection, BlockPos centerPos) {
        Holder<Biome> centerBiome = getTypicalBiome(centerSection, true, null);
        if (centerBiome == null) {
            return null;
        }
        for (LevelChunk c : List.of(level.getChunkAt(centerPos.south(16)),
                level.getChunkAt(centerPos.north(16)),
                level.getChunkAt(centerPos.east(16)),
                level.getChunkAt(centerPos.west(16)))) {
            Holder<Biome> sideBiome = getTypicalBiome(c.getSection(getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(centerPos.getY()))), true, centerBiome);
            if (!confluence$serverLevel.getChunkSource().isPositionTicking(c.getPos().toLong()) || sideBiome != centerBiome) {
                return null;
            }
        }
        return centerBiome;
    }

    @Unique
    private void confluence$checkBelow(LevelChunkSection selfSection, BlockPos pPos) {
        BlockPos belowPos = pPos.offset(0, -16, 0);
        if (level.isOutsideBuildHeight(belowPos.getY())) return;
        LevelChunkSection belowSection = getSection(getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(belowPos.getY())));
        Holder<Biome> targetBiome = confluence$checkCross(belowSection, belowPos);
        confluence$infect(selfSection, pPos, targetBiome, true);
    }

    @SuppressWarnings("unchecked")
    @Unique
    private void confluence$infect(LevelChunkSection section, BlockPos pPos, @Nullable Holder<Biome> biome, boolean checkAbove) {
        Holder<Biome> beforeBiome = getTypicalBiome(section, biome == null, biome); // 传播的话一点纯净都不能放过，净化的话一点邪恶都不能放过
        if (biome == beforeBiome) {
            return;
        }

        if (biome == null) { // 判定为纯净
            IChunkSection counter = (IChunkSection) section;
            counter.confluence$setBiomes(counter.confluence$getBackupBiome());
        } else {
            ((IChunkSection) section).confluence$setBiomes(((IPalettedContainer<Holder<Biome>>) section.getBiomes()).confluence$recreateSingle(biome));
        }
        BlockPos abovePos = pPos.above(16);
        if (checkAbove && !level.isOutsideBuildHeight(abovePos)) {
            LevelChunkSection aboveSection = getSection(getSectionIndexFromSectionY(SectionPos.blockToSectionCoord(abovePos.getY())));
            Holder<Biome> aboveTargetBiome = confluence$checkCross(section, pPos); // 根据十字决定的顶部群系
            Holder<Biome> supposeAboveBiome = judgeSection(aboveSection); // 顶部根据邪恶方块数量决定的应有的群系
            Holder<Biome> finalBiome = supposeAboveBiome != null ? supposeAboveBiome : aboveTargetBiome;  // 自身邪恶块足够就听自己的，否则听下面的
            confluence$infect(aboveSection, abovePos, finalBiome, false);
        }
        if (checkAbove) {
            confluence$serverLevel.getChunkSource().chunkMap.resendBiomesForChunks(List.of(this));
        }
    }
}
