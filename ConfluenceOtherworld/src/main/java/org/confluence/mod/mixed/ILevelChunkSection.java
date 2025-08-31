package org.confluence.mod.mixed;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.confluence.mod.util.BlockCounts;

public interface ILevelChunkSection {
    BlockCounts confluence$getBlockCounts();

    PalettedContainerRO<Holder<Biome>> confluence$getBackupBiome();

    void confluence$setBackupBiome(PalettedContainerRO<Holder<Biome>> biome);

    void confluence$setBiomes(PalettedContainerRO<Holder<Biome>> biomes);

    boolean confluence$isGraveyard();

    static ILevelChunkSection of(LevelChunkSection section) {
        return (ILevelChunkSection) section;
    }
}
