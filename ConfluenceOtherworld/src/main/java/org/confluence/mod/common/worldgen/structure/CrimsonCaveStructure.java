package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
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
            List<Vector3d> VctList = new ArrayList<>();
            int fingerCount = random.nextInt(6, 10);
            int radius = random.nextInt(5, 6);
            int radiusEnd = random.nextInt(15, 18);
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            centerPos = centerPos.offset(0, radius - 1, 0);
            BlockPos endPos = centerPos.offset(random.nextInt(-20, 21), random.nextInt(-65, -60), random.nextInt(-20, 21));
            BlockPos pos;
            double layer0 = 5.0D;
            double layer1 = radius + 2;
            double xDis = (endPos.getX() - centerPos.getX()) / layer0;
            double yDis = (endPos.getY() - centerPos.getY()) / layer0;
            double zDis = (endPos.getZ() - centerPos.getZ()) / layer0;
            double xStart = centerPos.getX();
            double yStart = centerPos.getY();
            double zStart = centerPos.getZ();
            float rotate = random.nextFloat() * (Mth.PI * 2);
            float fingerRotate = random.nextFloat() * (Mth.PI * 2);
            float fingerRotateStep = (Mth.PI * 2) / fingerCount;
            Vector3d posPoint;
            Object2BooleanMap<BlockPos> blockMap = new Object2BooleanOpenHashMap<>();

            boll(radius, 2, centerPos, blockMap, 0.05F, random);
            boll(radiusEnd, 4, endPos, blockMap, 0.01F, random);
            for (int i = 0; i < layer0; i++) {
                posPoint = new Vector3d((i == 0) ? xStart : (xStart + i * xDis + random.nextInt(-20, 21)), yStart + i * yDis, (i == 0) ? zStart : (zStart + i * zDis + random.nextInt(-20, 21)));
                VctList.add(posPoint);
            }
            VectorUtils.lightningPathList(VctList, 1, 5, random);
            lineSet(VctList, 4, 8, true, false, blockMap);
            lineSet(VctList, 2, 6, false, true, blockMap);

            VctList.clear();
            xDis = (radius * 2 + 2) * Mth.cos(rotate) / layer1;
            zDis = (radius * 2 + 2) * Mth.sin(rotate) / layer1;
            for (int i = 0; i < layer1; i++) {
                posPoint = new Vector3d(xStart + i * xDis, yStart, zStart + i * zDis);
                VctList.add(posPoint);
            }
            lineSet(VctList, 3, 3, false, true, blockMap);

            for (int i = 0; i < fingerCount; i++) {
                VctList.clear();
                VctList.add(new Vector3d(endPos.getX() + (3.0D + 1.5D * random.nextDouble()) * radiusEnd * Mth.cos(fingerRotate + i * fingerRotateStep), endPos.getY() + (random.nextDouble() - 0.5D) * 4 * radiusEnd, endPos.getZ() + (3.0D + 1.5D * random.nextDouble()) * radiusEnd * Mth.sin(fingerRotate + i * fingerRotateStep)));
                VctList.add(new Vector3d(endPos.getX(), endPos.getY(), endPos.getZ()));
                VectorUtils.lightningPathList(VctList, 1, 5, random);
                lineSet(VctList, 4, 8, true, false, blockMap);
                lineSet(VctList, 2, 6, false, true, blockMap);
                pos = new BlockPos((int) VctList.getFirst().x, (int) VctList.getFirst().y, (int) VctList.getFirst().z);
                boll(4, pos, true, true, blockMap);
                boll(2, pos, false, true, blockMap);
            }

            Map<ChunkPos, Object2BooleanMap<BlockPos>> gridMap = GridPiece.sliceChunks(blockMap, startChunk);
            for (Map.Entry<ChunkPos, Object2BooleanMap<BlockPos>> entry : gridMap.entrySet()) {
                GridPiece piece = new GridPiece(entry.getKey(), lowestY, entry.getValue());
                piece.solid = NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState();
                piece.avoid = Blocks.AIR.defaultBlockState();
                builder.addPiece(piece);
            }
        });
    }

    private static boolean radiusCheck(double radius, BlockPos centerPos, BlockPos blockPos) {
        int x = (centerPos.getX() - blockPos.getX());
        int y = (centerPos.getY() - blockPos.getY());
        int z = (centerPos.getZ() - blockPos.getZ());
        float dis = x * x + y * y + z * z;
        return ((radius * radius) >= dis);
    }

    private static void boll(double radiusD, BlockPos centerPos, boolean blockState, boolean replace, Object2BooleanMap<BlockPos> blockMap) {
        int radius = (int) radiusD + 1;
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radiusD, centerPos, posCheck) && (replace || !blockMap.containsKey(posCheck))) {
                        blockMap.put(posCheck, blockState);
                    }
                }
            }
        }
    }

    private static void boll(int radius, int wall, BlockPos centerPos, Object2BooleanMap<BlockPos> blockMap, float chance, WorldgenRandom random) {
        BlockPos pos = centerPos.offset(-radius, -radius, -radius);
        BlockPos posCheck;
        List<BlockPos> posList = new ArrayList<>();
        for (int x = 0; x < (2 * radius); x++) {
            for (int y = 0; y < (2 * radius); y++) {
                for (int z = 0; z < (2 * radius); z++) {
                    posCheck = pos.offset(x, y, z);
                    if (radiusCheck(radius, centerPos, posCheck) && (chance >= random.nextFloat())) {
                        posList.add(posCheck);
                    }
                }
            }
        }
        for (int i = 0; i < posList.size(); i++) {
            boll(radius, posList.get(i), true, true, blockMap);
        }
        for (int i = 0; i < posList.size(); i++) {
            boll(radius - wall, posList.get(i), false, true, blockMap);
        }
    }

    private static void lineSet(List<Vector3d> VctList, double rStart, double rEnd, boolean blockstate, boolean replace, Object2BooleanMap<BlockPos> blockMap) {
        Vector3d posPoint;
        double step = (rEnd - rStart) / VctList.size();
        for (int i = 0; i < VctList.size(); i++) {
            posPoint = VctList.get(i);
            boll(rStart + step * i, new BlockPos((int) posPoint.x, (int) posPoint.y, (int) posPoint.z), blockstate, replace, blockMap);
        }
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRIMSON_CAVE.get();
    }
}
