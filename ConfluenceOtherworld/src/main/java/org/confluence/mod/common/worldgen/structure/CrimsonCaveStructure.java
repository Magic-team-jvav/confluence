package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.util.VectorUtils;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CrimsonCaveStructure extends Structure {
    public static final MapCodec<CrimsonCaveStructure> CODEC = simpleCodec(CrimsonCaveStructure::new);

    protected CrimsonCaveStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int lowestY = getLowestY(context, 16, 16);
        if (lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            ChunkPos startChunk = context.chunkPos();
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            int radius = random.nextInt(12, 16);
            Object2BooleanMap<BlockPos> blockMap = new Object2BooleanOpenHashMap<>();

            generateHill(blockMap, radius, centerPos);
            Pair<BlockPos, Direction> armEndNode = generateArm(blockMap, centerPos, random, radius, startChunk);
            List<Pair<BlockPos, Vector3d>> fingerList = generatePalm(blockMap, random, armEndNode.getFirst(), armEndNode.getSecond());
            fingerList.forEach(pair -> generateFinger(blockMap, random, pair.getFirst(), pair.getSecond()));

            Map<ChunkPos, Object2BooleanMap<BlockPos>> gridMap = GridPiece.sliceChunks(blockMap, startChunk);
            for (Map.Entry<ChunkPos, Object2BooleanMap<BlockPos>> entry : gridMap.entrySet()) {
                GridPiece piece = new GridPiece(entry.getKey(), lowestY, entry.getValue());
                piece.solid = NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState();
                piece.avoid = Blocks.AIR.defaultBlockState();
                builder.addPiece(piece);
            }
        });
    }

    private static void generateFinger(Object2BooleanMap<BlockPos> blockMap, WorldgenRandom random, BlockPos startPos, Vector3d facing) {
        // todo
    }

    private static List<Pair<BlockPos, Vector3d>> generatePalm(Object2BooleanMap<BlockPos> blockMap, WorldgenRandom random, BlockPos armEndPos, Direction armEndFace) {
        List<Pair<BlockPos, Vector3d>> fingerList = new ArrayList<>();
        // todo
        return fingerList;
    }

    private static Pair<BlockPos, Direction> generateArm(Object2BooleanMap<BlockPos> blockMap, BlockPos startPos, WorldgenRandom random, int radius, ChunkPos startChunk) {
        BlockPos armStart = startPos.offset(random.nextInt(radius) - radius / 2, -radius + 4, random.nextInt(radius) - radius / 2);
        List<Vector3d> armNodes = Lists.newArrayList(VectorUtils.toVector3d(armStart));
        int depth = startPos.getY() / 16 - 1;
        if (depth % 2 == 0) depth++;
        int stepX = random.nextBoolean() ? -7 : 7;
        int stepZ = random.nextBoolean() ? -7 : 7;
        for (int i = 1; i < depth; i++) {
            armStart = armStart.offset(i * stepX, -16, i * stepZ);
            armNodes.add(VectorUtils.toVector3d(armStart));
            stepX = -stepX;
            stepZ = -stepZ;
        }
        armNodes.add(VectorUtils.toVector3d(startChunk.getMiddleBlockPosition(0)));
        VectorUtils.lightningPathList(armNodes, 2.5, 8, random);
        List<BlockPos> list = armNodes.stream().map(VectorUtils::fromVector3d).toList();
        for (BlockPos nodePos : list) {
            for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-3, -2, -3), nodePos.offset(3, 2, 3))) {
                blockMap.put(blockPos.immutable(), true);
            }
        }
        for (BlockPos nodePos : list) {
            for (BlockPos blockPos : BlockPos.betweenClosed(nodePos.offset(-2, -1, -2), nodePos.offset(2, 2, 2))) {
                blockMap.put(blockPos.immutable(), false);
            }
        }
        Vector3d delta = new Vector3d(armNodes.getLast()).sub(armNodes.get(armNodes.size() - 2));
        Direction armEndFace = Direction.getNearest(delta.x, delta.y, delta.z);
        return new Pair<>(VectorUtils.fromVector3d(armNodes.getLast()), armEndFace);
    }

    private static void generateHill(Object2BooleanMap<BlockPos> blockMap, int radius, BlockPos centerPos) {
        int airRadius = radius - 4;
        int diameter = radius * 2 + 1;
        int radiusSqr = radius * radius;
        int airRadiusSqr = airRadius * airRadius;
        for (int i = 0; i < diameter; i++) {
            int x = i - radius;
            for (int j = 0; j < diameter; j++) {
                int y = j - radius;
                for (int k = 0; k < diameter; k++) {
                    int z = k - radius;
                    int sqr = x * x + y * y + z * z;
                    if (sqr <= radiusSqr) {
                        blockMap.put(centerPos.offset(x, y, z), sqr >= airRadiusSqr);
                    }
                }
            }
        }
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRIMSON_CAVE.get();
    }
}
