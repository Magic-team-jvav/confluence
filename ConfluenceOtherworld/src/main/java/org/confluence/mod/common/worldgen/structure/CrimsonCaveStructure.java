package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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

import static org.confluence.mod.util.StructureUtils.boll;
import static org.confluence.mod.util.StructureUtils.lineSet;

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
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            boll(radius, 2, centerPos, blockMap, 0.05F, random, 1, 0);
            boll(radiusEnd, 4, endPos, blockMap, 0.01F, random, 1, 0);
            for (int i = 0; i < layer0; i++) {
                posPoint = new Vector3d((i == 0) ? xStart : (xStart + i * xDis + random.nextInt(-20, 21)), yStart + i * yDis, (i == 0) ? zStart : (zStart + i * zDis + random.nextInt(-20, 21)));
                VctList.add(posPoint);
            }
            VectorUtils.lightningPathList(VctList, 1, 5, random);
            lineSet(VctList, 4, 8, 1, false, blockMap);
            lineSet(VctList, 2, 6, 0, true, blockMap);

            VctList.clear();
            xDis = (radius * 2 + 2) * Mth.cos(rotate) / layer1;
            zDis = (radius * 2 + 2) * Mth.sin(rotate) / layer1;
            for (int i = 0; i < layer1; i++) {
                posPoint = new Vector3d(xStart + i * xDis, yStart, zStart + i * zDis);
                VctList.add(posPoint);
            }
            lineSet(VctList, 3, 3, 0, true, blockMap);

            for (int i = 0; i < fingerCount; i++) {
                VctList.clear();
                VctList.add(new Vector3d(endPos.getX() + (3.0D + 1.5D * random.nextDouble()) * radiusEnd * Mth.cos(fingerRotate + i * fingerRotateStep), endPos.getY() + (random.nextDouble() - 0.5D) * 4 * radiusEnd, endPos.getZ() + (3.0D + 1.5D * random.nextDouble()) * radiusEnd * Mth.sin(fingerRotate + i * fingerRotateStep)));
                VctList.add(new Vector3d(endPos.getX(), endPos.getY(), endPos.getZ()));
                VectorUtils.lightningPathList(VctList, 1, 5, random);
                lineSet(VctList, 4, 8, 1, false, blockMap);
                lineSet(VctList, 2, 6, 0, true, blockMap);
                pos = new BlockPos((int) VctList.getFirst().x, (int) VctList.getFirst().y, (int) VctList.getFirst().z);
                boll(4, pos, 1, true, blockMap);
                boll(2, pos, 0, true, blockMap);
                blockMap.put(pos, 2);
            }

            List<BlockState> blockList = Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.TR_CRIMSON_STONE.get().defaultBlockState(),
                    NatureBlocks.CRIMSON_HEART.get().defaultBlockState()
            );
            Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = GridPiece.sliceChunks(blockMap, startChunk);
            for (Map.Entry<ChunkPos, Object2IntMap<BlockPos>> entry : gridMap.entrySet()) {
                GridPiece piece = new GridPiece(entry.getKey(), lowestY, entry.getValue());
                piece.blockList = blockList;
                builder.addPiece(piece);
            }
            //builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "test", startChunk.getMiddleBlockPosition(-40), false, true, Rotation.NONE));
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.CRIMSON_CAVE.get();
    }
}
