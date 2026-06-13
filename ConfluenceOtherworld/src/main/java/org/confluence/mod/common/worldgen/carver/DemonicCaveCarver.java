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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.lib.util.LibMathUtils;
import org.confluence.mod.common.init.ModBiomes;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class DemonicCaveCarver extends WorldCarver<DemonicCaveCarver.Config> {
    public DemonicCaveCarver(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        int x1 = chunkPos.getBlockX((int) config.verticalLength.sample(random) * (random.nextBoolean() ? -1 : 1));
        int z1 = chunkPos.getBlockZ((int) config.verticalLength.sample(random) * (random.nextBoolean() ? -1 : 1));
        int y1 = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x1, z1);
        if (!biomeAccessor.apply(new BlockPos(x1, y1, z1)).is(ModBiomes.THE_CORRUPTION) ||
                !chunk.getFluidState(new BlockPos(x1, chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x1, z1) - 1, z1)).isEmpty())
            return false;
        int x2 = chunkPos.getBlockX((int) config.verticalLength.sample(random) * (random.nextBoolean() ? -1 : 1));
        int z2 = chunkPos.getBlockZ((int) config.verticalLength.sample(random) * (random.nextBoolean() ? -1 : 1));
        int y2 = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x2, z2);
        if (!biomeAccessor.apply(new BlockPos(x2, y2, z2)).is(ModBiomes.THE_CORRUPTION) ||
                !chunk.getFluidState(new BlockPos(x2, chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x2, z2) - 1, z2)).isEmpty())
            return false;
        float yScale = config.yScale.sample(random);

        Aquifer noWater = new Aquifer() {
            final BlockState air = Blocks.AIR.defaultBlockState();

            @Override
            public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
                return air;
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return true;
            }
        };

        List<Vector3f> positions = Lists.newArrayList(new Vector3f(x1, y1, z1), new Vector3f(x2, y2, z2));
        LibGeometryUtils.lightningPathList(positions, 2.5F, 0.125F, random);
        int size = positions.size();
        for (int i = 0; i < size; i++) {
            Vector3f position = positions.get(i);
            float delta = Math.abs(i - size * 0.5F) / size;
            for (int j = -4; j < 13; j++) {
                int maxRadius = random.nextInt(9) + 4;
                int radius = maxRadius - Mth.lerpInt(delta, 8, maxRadius);
                boolean b = carveEllipsoid(context, config, chunk, biomeAccessor, noWater, position.x, position.y - j * yScale, position.z, radius, yScale + 4, carvingMask, (context1, relativeX, relativeY, relativeZ, y) -> false);
                if (b && j == 12 && random.nextFloat() < 0.25F) {
                    position.add(0, -14 * yScale - 4, 0);
                    b = carveEllipsoid(context, config, chunk, biomeAccessor, noWater, position.x, position.y, position.z, 2, 2, carvingMask, (context1, relativeX, relativeY, relativeZ, y) -> relativeX * relativeX + relativeY * relativeY + relativeZ * relativeZ > 1.0);
                    if (b) {
                        chunk.setBlockState(LibMathUtils.fromVector3f(position).above(), NatureBlocks.SHADOW_ORB.get().defaultBlockState(), false);
                    }
                }
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
