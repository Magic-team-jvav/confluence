package org.confluence.mod.mixin.chunk;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.confluence.mod.mixed.IChunkSection;
import org.confluence.mod.mixin.accessor.PalettedContainerAccessor;
import org.confluence.mod.util.BlockCounts;
import org.spongepowered.asm.mixin.Final;
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
    @Shadow @Final private PalettedContainer<BlockState> states;

    @Shadow public abstract void recalcBlockCounts();

    @Unique
    private PalettedContainerRO<Holder<Biome>> confluence$backupBiome;
    @Unique
    private final BlockCounts confluence$blockCounts = new BlockCounts();

    @Override
    public BlockCounts confluence$getBlockCounts() {
        return confluence$blockCounts;
    }

    @Override
    public boolean confluence$isGraveyard() {
        return confluence$blockCounts.isGraveyard();
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

    // 我忘记为什么要写这个了，不写就崩溃
    @Inject(method = "<init>(Lnet/minecraft/core/Registry;)V", at = @At("RETURN"))
    private void constr(Registry<Biome> pBiomeRegistry, CallbackInfo ci) {
        confluence$backupBiome = biomes.recreate();
    }

    @Inject(method = "read",at = @At("RETURN"))
    private void read(FriendlyByteBuf buffer, CallbackInfo ci) {
        recalcBlockCounts();
    }

    // 世界生成的方块放置也是调这个方法
    @Inject(method = "setBlockState(IIILnet/minecraft/world/level/block/state/BlockState;Z)Lnet/minecraft/world/level/block/state/BlockState;", at = @At("RETURN"))
    private void setBlockState(int pX, int pY, int pZ, BlockState targetState, boolean pUseLocks, CallbackInfoReturnable<BlockState> cir, @Local(ordinal = 1) BlockState beforeState) {
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
