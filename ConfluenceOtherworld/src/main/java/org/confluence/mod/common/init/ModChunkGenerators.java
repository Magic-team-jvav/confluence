package org.confluence.mod.common.init;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.confluence.mod.common.worldgen.BannedBiomeNoiseBasedChunkGenerator;

import java.util.function.Supplier;

import static org.confluence.mod.Confluence.MODID;

public final class ModChunkGenerators {
    public static final DeferredRegister<MapCodec<? extends ChunkGenerator>> GENERATORS = DeferredRegister.create(BuiltInRegistries.CHUNK_GENERATOR, MODID);

    public static final Supplier<MapCodec<BannedBiomeNoiseBasedChunkGenerator>> BANNED_BIOME = GENERATORS.register("banned_biome", () -> BannedBiomeNoiseBasedChunkGenerator.CODEC);
}
