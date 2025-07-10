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
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DesertCaveCarver extends WorldCarver<CarverConfiguration> {
    public DesertCaveCarver(Codec<CarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, CarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        float y = config.y.sample(random, context);
        float yScale = config.yScale.sample(random);
        BlockPos position = chunkPos.getMiddleBlockPosition((int) y);
        BlockState blockState = chunk.getBlockState(position);
        if (blockState.isAir() || blockState.liquid()) {
            return false;
        }
        blockState = chunk.getBlockState(position.above((int) yScale));
        if (blockState.isAir() || blockState.liquid()) {
            return false;
        }

        Aquifer inner = new Aquifer() {
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
        Aquifer outer = new Aquifer() {
            final BlockState stone = NatureBlocks.HARDENED_SAND_BLOCK.get().defaultBlockState();
            final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            @Override
            public BlockState computeSubstance(DensityFunction.FunctionContext context, double substance) {
                if (chunk.getBlockState(pos.set(context.blockX(), context.blockY(), context.blockX())).isAir()) return null;
                return context.getBlender().blendDensity(context, substance) < substance * 0.3 ? null : stone;
            }

            @Override
            public boolean shouldScheduleFluidUpdate() {
                return false;
            }
        };
        CarveSkipChecker ichecker = (context1, relativeX, relativeY, relativeZ, y1) -> {
            return relativeX * relativeX + relativeY * relativeY + relativeZ * relativeZ > 1.5;
        };
        CarveSkipChecker ochecker = (context1, relativeX, relativeY, relativeZ, y1) -> {
            double ry = Math.abs(relativeY);
            return ry > 1 && relativeX * relativeX + relativeZ * relativeZ > ry * 0.6;
        };
        int j = (int) ((y - context.getMinGenY()) / yScale);

        for (int k = 0; k < 4; k++) {
            List<Vec3> positions = new ArrayList<>();
            List<Vec2> radius = new ArrayList<>();
            for (int i = 0; i < j; i++) {
                Vec3 vec3 = new Vec3(
                        chunkPos.getMiddleBlockX() + Mth.nextDouble(random, -16, 16),
                        y - i * yScale - Mth.nextDouble(random, -yScale, yScale),
                        chunkPos.getMiddleBlockZ() + Mth.nextDouble(random, -16, 16)
                );
                Vec2 vec2 = new Vec2(
                        Mth.nextFloat(random, 6, 8),
                        Mth.nextFloat(random, 2, 3)
                );
                positions.add(vec3);
                radius.add(vec2);
                carveEllipsoid(context, config, chunk, biomeAccessor, outer, vec3.x, vec3.y, vec3.z, vec2.x + 1, vec2.y + 1, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), ochecker);
            }
            for (int i = 0; i < j; i++) {
                Vec3 vec3 = positions.get(i);
                Vec2 vec2 = radius.get(i);
                carveEllipsoid(context, config, chunk, biomeAccessor, inner, vec3.x, vec3.y, vec3.z, vec2.x - 1, vec2.y, new CarvingMask(chunk.getHeight(), chunk.getMinBuildHeight()), ichecker);
            }
        }
        return true;
    }

    @Override
    public boolean isStartChunk(CarverConfiguration config, RandomSource random) {
        return true;
    }
}
