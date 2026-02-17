package org.confluence.mod.mixin.chunk;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.confluence.mod.Confluence;
import org.confluence.mod.mixed.ILevelChunkSection;
import org.confluence.mod.util.BlockCounts;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunkSection.class)
public abstract class LevelChunkSectionMixin implements ILevelChunkSection {
    @Shadow
    private PalettedContainerRO<Holder<Biome>> biomes;

    @Shadow
    public abstract void recalcBlockCounts();

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

    @Inject(method = "read", at = @At("RETURN"))
    private void read(FriendlyByteBuf buffer, CallbackInfo ci) {
        try {
            recalcBlockCounts();
        } catch (Exception e) {
            Confluence.LOGGER.warn("Failed to recalc block counts");
        }
    }
}
