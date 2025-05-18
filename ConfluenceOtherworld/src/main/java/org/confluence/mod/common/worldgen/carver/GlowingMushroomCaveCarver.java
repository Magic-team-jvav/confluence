package org.confluence.mod.common.worldgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.function.Function;

public class GlowingMushroomCaveCarver extends WorldCarver<GlowingMushroomCaveCarver.Config> {
    public GlowingMushroomCaveCarver(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, Config config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        CarveSkipChecker checker = (context1, relativeX, relativeY, relativeZ, y) -> {
            if (relativeY < -0.25) return true;
            return relativeX * relativeX + relativeY * relativeY + relativeZ * relativeZ > 1;
        };
        Aquifer outer = new Aquifer() {
            final BlockState mud = Blocks.MUD.defaultBlockState();
            final BlockState grass = NatureBlocks.MUSHROOM_GRASS_BLOCK.get().defaultBlockState();
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            @Override
            public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
                pos.set(context.blockX(), context.blockY(), context.blockZ());
                if (chunk.getBlockState(pos).isAir()) return null;
                return chunk.getBlockState(pos.move(0, 1, 0)).isAir() ? grass : mud;
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
        float y = config.y.sample(random, context);
        float yScale = config.yScale.sample(random);
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState grass = NatureBlocks.MUSHROOM_GRASS_BLOCK.get().defaultBlockState();
        for (int i = 0; i < 4; i++) {
            y += random.nextFloat() * yScale;
            Vec3 position = chunkPos.getMiddleBlockPosition((int) y).getCenter().add(
                    Mth.nextDouble(random, -8, 8),
                    0,
                    Mth.nextDouble(random, -8, 8)
            );
            Aquifer inner = new Aquifer() {
                @Override
                public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
                    return context.blockY() >= position.y + 2 ? air : grass;
                }

                @Override
                public boolean shouldScheduleFluidUpdate() {
                    return true;
                }
            };
            double horizontalRadius = Mth.nextDouble(random, yScale + 2, (yScale + 2) * 2);
            double verticalRadius = Mth.nextDouble(random, yScale, yScale * 2);
            carveEllipsoid(context, config, chunk, biomeAccessor, outer, position.x, position.y, position.z, horizontalRadius, verticalRadius, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), checker);
            carveEllipsoid(context, config, chunk, biomeAccessor, inner, position.x, position.y + 2, position.z, horizontalRadius - 4, verticalRadius - 3, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), checker);
        }
        return true;
    }

    @Override
    public boolean isStartChunk(Config config, RandomSource random) {
        return random.nextFloat() < config.probability;
    }

    public static class Config extends CarverConfiguration {
        public static final Codec<Config> CODEC = CarverConfiguration.CODEC.codec().xmap(Config::new, Function.identity());

        public Config(CarverConfiguration configuration) {
            super(configuration.probability, configuration.y, configuration.yScale, configuration.lavaLevel, configuration.debugSettings, configuration.replaceable);
        }
    }
}
