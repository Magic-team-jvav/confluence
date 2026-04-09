package org.confluence.mod.common.worldgen.carver;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import org.confluence.lib.util.VectorUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DrySeaCarver extends WorldCarver<CarverConfiguration> {
    public DrySeaCarver(Codec<CarverConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean carve(CarvingContext context, CarverConfiguration config, ChunkAccess chunk, Function<BlockPos, Holder<Biome>> biomeAccessor, RandomSource random, Aquifer aquifer, ChunkPos chunkPos, CarvingMask carvingMask) {
        int x = chunkPos.getBlockX(random.nextInt(0, 16));
        int z = chunkPos.getBlockZ(random.nextInt(0, 16));
        int y = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

        if (y < 20) return false;

        BlockPos basePos;
        Map<Long, Integer> newYMap = new HashMap<>();

        int newX = x;
        int newZ = z;
        List<BlockPos> posList = new ArrayList<>();
        int count = random.nextInt(5, 11);

        for (int c = 0; c < count; c++) {
            posList.clear();
            int radius = random.nextInt(5, 8);

            int finalY = chunk.getHeight(Heightmap.Types.WORLD_SURFACE_WG, newX, newZ);
            if (finalY < 20) continue;

            basePos = new BlockPos(newX, finalY, newZ);
            long currentKey = BlockPos.asLong(newX, 0, newZ);
            int height = Math.max(finalY, newYMap.getOrDefault(currentKey, Integer.MIN_VALUE)) + random.nextInt(6, 12) - finalY;
            int thirdHeight = height / 3;
            int xOffset = random.nextInt(-3, 4);
            int zOffset = random.nextInt(-3, 4);

            int[] layerRadii = {random.nextInt(2, 4), radius + 2, radius - 1, radius - 1, radius + 3, radius};

            int[] layerYOffsets = {-random.nextInt(5, 7), 0, thirdHeight, thirdHeight * 2, height - 2, height};

            int[] layerCount = {random.nextInt(4, 6), random.nextInt(6, 9), random.nextInt(4, 6), random.nextInt(4, 6), random.nextInt(7, 10), random.nextInt(7, 10)};

            List<Vector3d> groupDown = new ArrayList<>();
            List<Vector3d> groupMid  = new ArrayList<>();
            List<Vector3d> groupUp= new ArrayList<>();

            for (int i = 0; i < 6; i++) {
                int xOff = (i <= 3) ? 0 : xOffset;
                int zOff = (i <= 3) ? 0 : zOffset;

                List<Vector3d> targetGroup = randomRound(layerRadii[i], layerCount[i], random, basePos.offset(xOff, layerYOffsets[i], zOff));

                if (i < 2) {
                    groupDown.addAll(targetGroup);
                } else if (i == 2) {
                    groupDown.addAll(targetGroup);
                    groupMid.addAll(targetGroup);
                } else if (i == 3) {
                    groupMid.addAll(targetGroup);
                    groupUp.addAll(targetGroup);
                } else {
                    groupUp.addAll(targetGroup);
                }
            }

            posList.addAll(VectorUtils.getBlocksInConvexHull(groupDown));
            posList.addAll(VectorUtils.getBlocksInConvexHull(groupMid));
            posList.addAll(VectorUtils.getBlocksInConvexHull(groupUp));

            for (BlockPos pos : posList) {
                newYMap.merge(BlockPos.asLong(pos.getX(), 0, pos.getZ()), pos.getY(), Math::max);
                this.carveSinglePoint(
                        chunk,
                        pos.getX(),
                        pos.getY(),
                        pos.getZ(),
                        carvingMask
                );
            }

            float rotate = 2 * Mth.PI * random.nextFloat();
            int offsetDis = random.nextInt(10, 15);

            newX += (int) (Mth.sin(rotate) * offsetDis);
            newZ += (int) (Mth.cos(rotate) * offsetDis);
        }

        return true;
    }

    @Override
    public boolean isStartChunk(CarverConfiguration config, RandomSource random) {
        return random.nextFloat() < config.probability;
    }

    private static List<Vector3d> randomRound(int minRadius, int count, RandomSource random, BlockPos basePos) {
        float startRotate = 2 * Mth.PI * random.nextFloat();
        float stepRotate = 2 * Mth.PI / count;
        List<Vector3d> r = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float rotate = startRotate + stepRotate * i + stepRotate / 3 * random.nextFloat();
            float trueRadius = minRadius + 2 * random.nextFloat();
            r.add(VectorUtils.toVector3d(basePos.offset((int) (trueRadius * Mth.sin(rotate)), random.nextInt(-1, 2), (int) (trueRadius * Mth.cos(rotate)))));
        }
        return r;
    }

    private void carveSinglePoint(
            ChunkAccess chunk,
            double targetX,
            double targetY,
            double targetZ,
            CarvingMask carvingMask
    ) {
        ChunkPos chunkPos = chunk.getPos();

        int relX = Mth.floor(targetX) - chunkPos.getMinBlockX();
        int relZ = Mth.floor(targetZ) - chunkPos.getMinBlockZ();
        int relY = Mth.floor(targetY);

        if (relX < 0 || relX > 15 || relZ < 0 || relZ > 15) {
            return;
        }

        if (carvingMask.get(relX, relY, relZ)) {
            return;
        }

        carvingMask.set(relX, relY, relZ);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(
                chunkPos.getMinBlockX() + relX, relY, chunkPos.getMinBlockZ() + relZ
        );

        chunk.setBlockState(mutablePos, Blocks.END_STONE.defaultBlockState(), false);
    }
}
