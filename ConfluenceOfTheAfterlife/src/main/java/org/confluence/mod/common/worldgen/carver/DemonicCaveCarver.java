package org.confluence.mod.common.worldgen.carver;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.mod.util.ModUtils;
import org.joml.Vector3d;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class DemonicCaveCarver extends WorldCarver<DemonicCaveCarver.Config> {
    public DemonicCaveCarver(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        RandomSource random1 = RandomSource.create(random.nextLong());
        int x1 = chunkPos.getBlockX((int) config.verticalLength.sample(random1) * (random1.nextBoolean() ? -1 : 1));
        int y1 = config.y.sample(random1, context) + 8;
        int z1 = chunkPos.getBlockZ((int) config.verticalLength.sample(random1) * (random1.nextBoolean() ? -1 : 1));
        int x2 = chunkPos.getBlockX((int) config.verticalLength.sample(random1) * (random1.nextBoolean() ? -1 : 1));
        int y2 = config.y.sample(random1, context) + 8;
        int z2 = chunkPos.getBlockZ((int) config.verticalLength.sample(random1) * (random1.nextBoolean() ? -1 : 1));
        float yScale = config.yScale.sample(random1);

        List<Vector3d> positions = Lists.newArrayList(new Vector3d(x1, y1, z1), new Vector3d(x2, y2, z2));
        ModUtils.lightningPathList(positions, 2.5, 8, random1);
        int size = positions.size();
        for (int i = 0; i < size; i++) {
            Vector3d position = positions.get(i);
            float delta = Math.abs(i - size * 0.5F) / size;
            for (int j = -4; j < 13; j++) {
                int maxRadius = random1.nextInt(9) + 4;
                int radius = maxRadius - Mth.lerpInt(delta, 8, maxRadius);
                carveEllipsoid(context, config, chunk, biomeAccessor, aquifer, position.x, position.y - j * yScale, position.z, radius, yScale + 4, carvingMask, (context1, relativeX, relativeY, relativeZ, y) -> false);
            }
        }
        return true;
    }

    @Override
    public boolean isStartChunk(Config config, RandomSource random) {
        return random.nextFloat() < config.probability;
    }

    public static class Config extends CarverConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CarverConfiguration.CODEC.forGetter(config -> config),
                FloatProvider.codec(0, 127).fieldOf("vertical_length").forGetter(config -> config.verticalLength)
        ).apply(instance, Config::new));
        private final FloatProvider verticalLength;

        public Config(CarverConfiguration baseConfig, FloatProvider verticalLength) {
            super(baseConfig.probability, baseConfig.y, baseConfig.yScale, baseConfig.lavaLevel, baseConfig.debugSettings, baseConfig.replaceable);
            this.verticalLength = verticalLength;
        }
    }
}
