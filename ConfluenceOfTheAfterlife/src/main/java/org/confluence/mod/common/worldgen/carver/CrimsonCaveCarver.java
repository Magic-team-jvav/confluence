package org.confluence.mod.common.worldgen.carver;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.ModUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.function.Function;

@ParametersAreNonnullByDefault
public class CrimsonCaveCarver extends WorldCarver<CrimsonCaveCarver.Config> {
    private static final CarveSkipChecker CARVE_SKIP_CHECKER = (context1, relativeX, relativeY, relativeZ, y) -> false;

    public CrimsonCaveCarver(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        int radius = (int) config.hillRadius.sample(random);
        BlockPos hillPos = carveHill(context, config, chunk, biomeAccessor, random, chunkPos, carvingMask, radius);
        if (hillPos == null) return false;
        BlockPos pos = chunkPos.getMiddleBlockPosition(-8);
        carveEllipsoid(context, config, chunk, biomeAccessor, new SingleBlockAquifer(NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState(), false), pos.getX(), pos.getY(), pos.getZ(), 16, 8, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), CARVE_SKIP_CHECKER);
        carveEllipsoid(context, config, chunk, biomeAccessor, new SingleBlockAquifer(Blocks.AIR.defaultBlockState(), false), pos.getX(), pos.getY(), pos.getZ(), 14, 6, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), CARVE_SKIP_CHECKER);
        BlockPos tunnelPos = carveTunnel(context, config, chunk, biomeAccessor, random, hillPos, radius);
        if (tunnelPos == null) return false;
        boolean flag = false;
        for (int i = 0; i < 5; i++) {
            flag |= carveFinger(context, config, chunk, biomeAccessor, random, aquifer, chunkPos, carvingMask);
        }
        return flag;
    }

    private boolean carveFinger(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        return true;
    }

    private @Nullable BlockPos carveTunnel(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, BlockPos hillPos, int radius) {
        BlockState stone = NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        Aquifer outer = new SingleBlockAquifer(stone, false);
        Aquifer inner = new SingleBlockAquifer(air, true);
        int signX = random.nextBoolean() ? -7 : 7;
        int signZ = random.nextBoolean() ? -7 : 7;
        BlockPos pos = hillPos.offset(0, -radius + 2, 0);
        List<Vector3d> positions = Lists.newArrayList(ModUtils.toVector3d(pos));
        int angles = hillPos.getY() / 16 + 1;
        for (int i = 1; i < angles; i++) {
            pos = pos.offset(i * signX, -16, i * signZ);
            positions.add(ModUtils.toVector3d(pos));
            signX = -signX;
            signZ = -signZ;
        }
        ModUtils.lightningPathList(positions, 2.5, 8, random);
        for (Vector3d position : positions) {
            carveEllipsoid(context, config, chunk, biomeAccessor, outer, position.x, position.y, position.z, 5, 4, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), CARVE_SKIP_CHECKER);
        }
        int size = positions.size();
        for (int i = 0; i < size; i++) {
            Vector3d position = positions.get(i);
            int vr = 3;
            if (i==0) vr = 4;
            else if (i == size - 1) vr = 5;
            carveEllipsoid(context, config, chunk, biomeAccessor, inner, position.x, position.y, position.z, 3, vr, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), CARVE_SKIP_CHECKER);
        }
        return positions.isEmpty() ? null : ModUtils.fromVector3d(positions.getLast());
    }

    private @Nullable BlockPos carveHill(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, ChunkPos chunkPos, CarvingMask carvingMask, int radius) {
        int radiusSqr = radius * radius;
        int airRadiusSqr = Mth.square(radius - 4);
        BlockPos.MutableBlockPos blockPos = chunkPos.getMiddleBlockPosition(config.y.sample(random, context)).mutable();
        BlockState blockState;
        while ((blockState = chunk.getBlockState(blockPos)).isAir()) {
            blockPos.move(0, -1, 0);
        }
        if (!blockState.getFluidState().isEmpty()) return null;
        BlockPos finalPos = blockPos.immutable();
        BlockState stone = NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        Aquifer hillAquifer = new Aquifer() {
            @Override
            public @Nullable BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
                int x = context.blockX() - finalPos.getX();
                int y = context.blockY() - finalPos.getY();
                int z = context.blockZ() - finalPos.getZ();
                int sqr = x * x + y * y + z * z;
                if (sqr <= radiusSqr) {
                    if (sqr < airRadiusSqr) {
                        return air;
                    }
                    return stone;
                }
                return null;
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return true;
            }
        };
        boolean success = carveEllipsoid(context, config, chunk, biomeAccessor, hillAquifer, finalPos.getX(), finalPos.getY(), finalPos.getZ(), radius, radius, carvingMask, CARVE_SKIP_CHECKER);
        if (success) {
            Direction dir = Util.getRandom(ModUtils.HORIZONTAL, random);
            BlockPos firstPos = finalPos.relative(dir.getCounterClockWise(), 2).relative(dir, radius / 2);
            BlockPos secondPos = finalPos.relative(dir.getClockWise(), 2).above(4).relative(dir, radius);
            for (BlockPos pos : BlockPos.betweenClosed(firstPos, secondPos)) {
                if (chunk.getBlockState(pos).is(NatureBlocks.TR_CRIMSON_STONE)) {
                    chunk.setBlockState(pos, air, false);
                }
            }
            return firstPos;
        }
        return null;
    }

    @Override
    protected boolean canReplaceBlock(Config config, BlockState state) {
        return state.isAir() || super.canReplaceBlock(config, state);
    }

    @Override
    public boolean isStartChunk(Config config, RandomSource random) {
        return random.nextFloat() < config.probability;
    }

    public static class Config extends CarverConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                CarverConfiguration.CODEC.forGetter(config -> config),
                FloatProvider.codec(12, 16).fieldOf("hill_radius").forGetter(config -> config.hillRadius)
        ).apply(instance, Config::new));
        private final FloatProvider hillRadius;

        public Config(CarverConfiguration baseConfig, FloatProvider hillRadius) {
            super(baseConfig.probability, baseConfig.y, baseConfig.yScale, baseConfig.lavaLevel, baseConfig.debugSettings, baseConfig.replaceable);
            this.hillRadius = hillRadius;
        }
    }
}
