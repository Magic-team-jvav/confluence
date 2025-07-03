package org.confluence.mod.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixin.accessor.PalettedContainerAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements IChunkSection {
    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;
    @Unique
    public int confluence$crimsonCount;
    @Unique
    public int confluence$corruptCount;
    @Unique
    public int confluence$hallowCount;
    @Unique
    public int confluence$sunflowerCount;
    @Unique
    public int confluence$tombCount;
    @Unique
    private PalettedContainerRO<Holder<Biome>> confluence$backupBiome;

    @Override
    public void confluence$countCrimson(int count) {
        confluence$crimsonCount += count;
    }

    @Override
    public void confluence$countCorrupt(int count) {
        confluence$corruptCount += count;
    }

    @Override
    public void confluence$countHallow(int count) {
        confluence$hallowCount += count;
    }

    @Override
    public void confluence$countSunflower(int count) {
        confluence$sunflowerCount += count;
    }

    @Override
    public void confluence$countTomb(int count) {
        confluence$tombCount += count;
    }

    @Override
    public int confluence$getSunflower() {
        return confluence$sunflowerCount;
    }

    @Override
    public int confluence$getTomb() {
        return confluence$tombCount;
    }

    @Override
    public boolean confluence$isGraveyard() {
        return confluence$getTomb() - confluence$getSunflower() >= 7;
    }

    @Override
    public int confluence$getCrimson() {
        return confluence$crimsonCount;
    }

    @Override
    public int confluence$getCorrupt() {
        return confluence$corruptCount;
    }

    @Override
    public int confluence$getHallow() {
        return confluence$hallowCount;
    }

    @Override
    public PalettedContainerRO<Holder<Biome>> confluence$getBackupBiome() {
        return confluence$backupBiome;
    }

    @Override
    public void confluence$setBackupBiome(PalettedContainerRO<Holder<Biome>> biome) {
        confluence$backupBiome = biome;
    }

    @Override
    public void confluence$setBiomes(PalettedContainerRO<Holder<Biome>> biomes) {
        this.biomes = biomes;
    }

    @Inject(method = "<init>(Lnet/minecraft/core/Registry;)V", at = @At("RETURN"))
    private void constr(Registry<Biome> pBiomeRegistry, CallbackInfo ci) {
        confluence$backupBiome = biomes.recreate();
    }

    // 世界生成的方块放置也是调这个方法
    @Inject(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"))
    private void setBlockState(int pX, int pY, int pZ, BlockState targetState, boolean pUseLocks, CallbackInfoReturnable<BlockState> cir, @Local(ordinal = 1) BlockState beforeState) {
        if (beforeState.getBlock() instanceof ISpreadable spreadable) {
            switch (spreadable.getSpreadType()) {
                case CRIMSON -> confluence$crimsonCount--;
                case CORRUPT -> confluence$corruptCount--;
                case HALLOW -> confluence$hallowCount--;
            }
        }
        if (targetState.getBlock() instanceof ISpreadable spreadable) {
            switch (spreadable.getSpreadType()) {
                case CRIMSON -> confluence$crimsonCount++;
                case CORRUPT -> confluence$corruptCount++;
                case HALLOW -> confluence$hallowCount++;
            }
        }
        if (beforeState.is(Blocks.SUNFLOWER)) {
            confluence$sunflowerCount--;
        }
        if (targetState.is(Blocks.SUNFLOWER)) {
            confluence$sunflowerCount++;
        }
        if (beforeState.is(ModTags.Blocks.TOMBSTONE)) {
            confluence$tombCount--;
        }
        if (targetState.is(ModTags.Blocks.TOMBSTONE)) {
            confluence$tombCount++;
        }
    }

    public Holder<Biome> confluence$getBiomeByKey(ResourceKey<Biome> key) {
        IdMap<Holder<Biome>> biomeReg = ((PalettedContainerAccessor<Holder<Biome>>) biomes).getRegistry();
        for (Holder<Biome> biome : biomeReg) {
            if (biome.unwrapKey().orElse(null) == key) {
                return biome;
            }
        }
        throw new IllegalArgumentException("Getting unregistered biome " + key);
    }
}
