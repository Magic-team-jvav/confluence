package org.confluence.mod.mixed;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.confluence.mod.util.BlockCounts;

public interface IChunkSection {
    BlockCounts confluence$getBlockCounts();

    PalettedContainerRO<Holder<Biome>> confluence$getBackupBiome();

    void confluence$setBackupBiome(PalettedContainerRO<Holder<Biome>> biome);

    void confluence$setBiomes(PalettedContainerRO<Holder<Biome>> biomes);

    Holder<Biome> confluence$getBiomeByKey(ResourceKey<Biome> key);

    boolean confluence$isEctoMist();

    default boolean confluence$isGraveyard() {
        return confluence$isEctoMist();
    }
}
