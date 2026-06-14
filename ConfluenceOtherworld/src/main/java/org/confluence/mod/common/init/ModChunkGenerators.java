package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraftforge.registries.DeferredRegister;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;

import static org.confluence.mod.Confluence.MODID;

public final class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, MODID);

    static {
        GENERATORS.register("banned_biome", () -> BannedBiomeNoiseBasedChunkGenerator.CODEC);
    }
}
