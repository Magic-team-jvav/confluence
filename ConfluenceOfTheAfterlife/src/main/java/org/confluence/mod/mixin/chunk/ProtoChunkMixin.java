package org.confluence.mod.mixin.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixed.IPalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static org.confluence.mod.util.DynamicBiomeUtils.balanceEvil;
import static org.confluence.mod.util.DynamicBiomeUtils.getTypicalBiome;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkMixin {
    @Inject(method = "setBlockState",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/level/chunk/status/ChunkStatus;isOrAfter(Lnet/minecraft/world/level/chunk/status/ChunkStatus;)Z"),locals = LocalCapture.CAPTURE_FAILSOFT)
    private void setBlock(BlockPos pPos, BlockState pState, boolean pIsMoving, CallbackInfoReturnable<BlockState> cir, int $$3, int $$4, int $$5, int $$6, LevelChunkSection section, boolean $$8, int $$9, int $$10, int $$11, BlockState beforeState){
        IChunkSection counter = (IChunkSection) section;
        int[] i = {counter.confluence$getCrimson(), counter.confluence$getCorrupt(), counter.confluence$getHallow(), counter.confluence$getSunflower()};
        Holder<Biome> targetBiome = balanceEvil(i, counter);
        if(targetBiome != null){
            confluence$infect(section, targetBiome);
        }

    }

    // 生成阶段只管传播不管净化
    @SuppressWarnings("unchecked")
    @Unique
    private void confluence$infect(LevelChunkSection section, Holder<Biome> biome){
        Holder<Biome> beforeBiome = getTypicalBiome(section, false,null);
        if(biome==beforeBiome){
            return;
        }
        ((IChunkSection)section).confluence$setBiomes(((IPalettedContainer<Holder<Biome>>)section.getBiomes()).confluence$recreateSingle(biome));
    }
}
