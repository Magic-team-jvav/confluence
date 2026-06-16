package org.confluence.mod.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;

import static org.confluence.mod.Confluence.MODID;

public final class ModChunkGenerators {
    public static final DeferredRegister<Codec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(Registries.CHUNK_GENERATOR, MODID);

    static {
        GENERATORS.register("banned_biome", () -> BannedBiomeNoiseBasedChunkGenerator.CODEC);
    }
}
