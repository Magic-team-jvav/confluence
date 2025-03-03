package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.world.level.block.LeavesBlock.PERSISTENT;
import static org.confluence.mod.util.StructureUtils.*;
import static org.confluence.mod.util.VectorUtils.lightningPathList;

public class LivingTreeStructure extends Structure {
    public static final MapCodec<LivingTreeStructure> CODEC = simpleCodec(LivingTreeStructure::new);
    public static BlockPos centerPos = new BlockPos(0, 0, 0);
    public static int rotate = 0;

    protected LivingTreeStructure(StructureSettings settings) {
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
            centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            List<Vector3d> locationList = new ArrayList<>();
            Vector3d locationStart = new Vector3d();
            Vector3d locationEnd = new Vector3d();

            locationStart.x = (centerPos.getX());
            locationStart.y = (centerPos.getY());
            locationStart.z = (centerPos.getZ());
            locationEnd.x = (centerPos.getX() + random.nextInt(-4, 5));
            locationEnd.y = (centerPos.getY() + random.nextInt(55, 65));
            locationEnd.z = (centerPos.getZ() + random.nextInt(-4, 5));

            locationList.add(locationStart);
            locationList.add(locationEnd);
            List<Vector3d> leavesTop = ellipsoidPos(30, 15, 30, new BlockPos((int) locationEnd.x, (int) locationEnd.y, (int) locationEnd.z), 0.01F, random);
            lineSetEllipsoid(leavesTop, 4, 2, 4, 2, false, blockMap, 0.75F, random);
            lightningPathList(locationList, 1, 8, random);
            lineSet(locationList, 5.9, 1.0, 1, true, blockMap);
            stick(random, locationList, blockMap, true);
            stick(random, locationList, blockMap, false);
            locationList.clear();
            locationEnd.x = (centerPos.getX() + random.nextInt(-4, 5));
            locationEnd.y = (centerPos.getY() + random.nextInt(-95, -65));
            locationEnd.z = (centerPos.getZ() + random.nextInt(-4, 5));
            locationList.add(locationStart);
            locationList.add(locationEnd);
            lightningPathList(locationList, 1, 12, random);
            lineSet(locationList, 4.9, 1.0, 1, true, blockMap);
            boll(4.9, centerPos, 0, true, blockMap);
            lineSet(locationList, 1.9, 0.9, 0, true, blockMap);
            Vector3d room = locationList.get(locationList.size() / 2 + random.nextInt(-20, 21));
            centerPos = new BlockPos((int) room.x, (int) room.y, (int) room.z);
            rotate = random.nextInt(4);

            List<BlockState> blockList = Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getWood().get().defaultBlockState(),
                    NatureBlocks.LIVING_LOG_BLOCKS.getLeaves().get().defaultBlockState().setValue(PERSISTENT, Boolean.TRUE)
            );
            Map<ChunkPos, Object2IntMap<BlockPos>> gridMap = GridPiece.sliceChunks(blockMap, startChunk);
            for (Map.Entry<ChunkPos, Object2IntMap<BlockPos>> entry : gridMap.entrySet()) {
                GridPiece piece = new GridPiece(entry.getKey(), lowestY, entry.getValue());
                piece.blockList = blockList;
                builder.addPiece(piece);
            }
            if (rotate == 0) {
                builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(1, 0, -5), true, true, Rotation.NONE));
            } else if (rotate == 1) {
                builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(5, 0, 1), true, true, Rotation.CLOCKWISE_90));
            } else if (rotate == 2) {
                builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(-1, 0, 5), true, true, Rotation.CLOCKWISE_180));
            } else if (rotate == 3) {
                builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "living_room", centerPos.offset(-5, 0, -1), true, true, Rotation.COUNTERCLOCKWISE_90));
            }
        });
    }

    private static void stick(WorldgenRandom random, List<Vector3d> locationList, Object2IntMap<BlockPos> blockMap, boolean branch) {
        List<Vector3d> leavesTop = new ArrayList<>();
        int stickCount = random.nextInt(5, 10);
        for (int stickPlace = 0; stickPlace < stickCount; stickPlace++) {
            double anCs = 360.0 / stickCount;
            double everyA = anCs * stickPlace / 180 * Math.PI;
            double everyB = ((((double) random.nextInt(110) - 20) * Math.pow((double) random.nextInt(101) / 100, 3)) / 180 * Math.PI);
            int len = 20 + random.nextInt(15);
            double endX = len * Math.cos(everyA) * Math.cos(everyB);
            double endY = len * Math.sin(everyB);
            double endZ = len * Math.sin(everyA) * Math.cos(everyB);
            Vector3d stickStart = locationList.get(branch ? (locationList.size() - (locationList.size() / 11 * 7) - random.nextInt(10)) : (random.nextInt(10)));
            Vector3d stickEnd = new Vector3d();
            stickEnd.x = branch ? (locationList.getLast().x + endX) : (locationList.getFirst().x + endX / 2);
            stickEnd.y = branch ? (locationList.getLast().y + endY - 20) : (locationList.getFirst().y - endY -10);
            stickEnd.z = branch ? (locationList.getLast().z + endZ) : (locationList.getFirst().z + endZ / 2);
            List<Vector3d> stickList = new ArrayList<>();
            stickList.add(stickStart);
            stickList.add(stickEnd);
            lightningPathList(stickList, 1.0, 8, random);
            lineSet(stickList, random.nextInt(2, 4), 1, 1, true, blockMap);
            if (branch) {
                leavesTop.clear();
                leavesTop = ellipsoidPos(20, 10, 20, new BlockPos((int) stickEnd.x, (int) stickEnd.y, (int) stickEnd.z), 0.01F, random);
                lineSetEllipsoid(leavesTop, 4, 2, 4, 2, false, blockMap, 0.75F, random);
            }
        }
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.LIVING_TREE.get();
    }
}