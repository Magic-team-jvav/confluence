package org.confluence.mod.common.worldgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class CrimsonCaveCarver extends WorldCarver<CrimsonCaveCarver.Config> {
    public CrimsonCaveCarver(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        return false;
    }

    @Override
    public boolean isStartChunk(Config config, RandomSource random) {
        return random.nextFloat() < config.probability;
    }

    public static class Config extends CarverConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CarverConfiguration.CODEC.forGetter(config -> config)
        ).apply(instance, Config::new));

        public Config(CarverConfiguration baseConfig) {
            super(baseConfig.probability, baseConfig.y, baseConfig.yScale, baseConfig.lavaLevel, baseConfig.debugSettings, baseConfig.replaceable);
        }
    }
}
