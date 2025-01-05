package org.confluence.mod.mixed;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.PalettedContainerRO;

public interface IChunkSection {
    void confluence$countCrimson(int count);
    void confluence$countCorrupt(int count);
    void confluence$countHallow(int count);
    int confluence$getCrimson();
    int confluence$getCorrupt();
    int confluence$getHallow();

    PalettedContainerRO<Holder<Biome>> confluence$getBackupBiome();
    void confluence$setBackupBiome(PalettedContainerRO<Holder<Biome>> biome);
    void confluence$setBiomes(PalettedContainerRO<Holder<Biome>> biomes);
    Holder<Biome> confluence$getBiomeByKey(ResourceKey<Biome> key);
}
