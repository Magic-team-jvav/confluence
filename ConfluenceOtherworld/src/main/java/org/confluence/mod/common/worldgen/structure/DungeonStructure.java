package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.joml.Vector3d;
import org.joml.Vector3i;

import java.util.*;

import static org.confluence.lib.util.StructureUtils.lineSet;
import static org.confluence.lib.util.StructureUtils.rectangular;
import static org.confluence.lib.util.VectorUtils.mazePos;

public class DungeonStructure extends Structure {
    public static final MapCodec<DungeonStructure> CODEC = simpleCodec(DungeonStructure::new);

    protected DungeonStructure(StructureSettings settings) {
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
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Vector3d key;
            List<Boolean> value = new ArrayList<>();
            BlockPos mazeRotatePos;
            int outSize = 5;
            int outSizeHeight = 6;
            int inSize = 2;
            int inSizeHeight = 3;
            int outRoomSize = 15;
            int outRoomSizeHeight = 24;
            int inRoomSize = 12;
            int inRoomSizeHeight = 21;

            Rotation rotation = Util.getRandom(Rotation.values(), random);
            List<Vector3d> firstChannel = new ArrayList<>();
            Vector3d vct = new Vector3d(centerPos.getX(), centerPos.getY() - 4, centerPos.getZ());
            firstChannel.add(vct);
            vct = new Vector3d(centerPos.getX() + random.nextInt(-15, 16), random.nextInt(40, 45), centerPos.getZ() + random.nextInt(-15, 16));
            firstChannel.add(vct);
            vct = new Vector3d(centerPos.getX(), random.nextInt(-20, -10), centerPos.getZ());
            firstChannel.add(new Vector3d(vct.x, vct.y + outRoomSizeHeight + 10, vct.z));
            VectorUtils.lightningPathList(firstChannel, 2, 8, random);
            lineSet(firstChannel, 5.5, 5.5, 1, true, blockMap);

            Map<Vector3d, List<Boolean>> mazePos = mazePos(new Vector3d(centerPos.getX(), vct.y, centerPos.getZ()), 35, 3, random, 1.0F);

            List<BlockPos> roomPos = new ArrayList<>();
            BlockPos underCenter = new BlockPos(centerPos.getX(), (int) vct.y, centerPos.getZ());

            Map<Vector3d, List<Boolean>> littleMazePos = mazePos(new Vector3d(centerPos.getX(), vct.y + inRoomSizeHeight + 2, centerPos.getZ()), 2, 6, random, 1.0F);

            for (Map.Entry<Vector3d, List<Boolean>> entry : mazePos.entrySet()) {
                key = entry.getKey();
                List<Boolean> outList = new ArrayList<>();
                outList.addAll(entry.getValue());
                mazeRotatePos = new BlockPos((int) key.x, (int) key.y, (int) key.z);
                if (outList.get(0)) {
                    rectangular(mazeRotatePos.offset(-outSize, -outSizeHeight, -outSize), mazeRotatePos.offset(17, outSizeHeight, outSize), 1, blockMap, 0);
                }
                if (outList.get(1)) {
                    rectangular(mazeRotatePos.offset(-outSize, -outSizeHeight, -outSize), mazeRotatePos.offset(outSize, outSizeHeight, 17), 1, blockMap, 0);
                }
                if (outList.get(2)) {
                    rectangular(mazeRotatePos.offset(outSize, outSizeHeight, outSize), mazeRotatePos.offset(-17, -outSizeHeight, -outSize), 1, blockMap, 0);
                }
                if (outList.get(3)) {
                    rectangular(mazeRotatePos.offset(outSize, outSizeHeight, outSize), mazeRotatePos.offset(-outSize, -outSizeHeight, -17), 1, blockMap, 0);
                }
                if (0.5F > random.nextFloat()) {
                    roomPos.add(mazeRotatePos);
                    rectangular(mazeRotatePos.offset(-outRoomSize, -outSizeHeight, -outRoomSize), mazeRotatePos.offset(outRoomSize, outRoomSizeHeight, outRoomSize), 1, blockMap, 0);
                }
            }

            rectangular(underCenter.offset(-outRoomSize, -outSizeHeight, -outRoomSize), underCenter.offset(outRoomSize, outRoomSizeHeight + 4, outRoomSize), 1, blockMap, 0);

            rectangular(underCenter.offset(-8, outRoomSizeHeight, -8), underCenter.offset(8, outRoomSizeHeight + 17, 8), 1, blockMap, 0);

            for (Map.Entry<Vector3d, List<Boolean>> entry : mazePos.entrySet()) {
                key = entry.getKey();
                List<Boolean> outList = new ArrayList<>();
                outList.addAll(entry.getValue());
                mazeRotatePos = new BlockPos((int) key.x, (int) key.y, (int) key.z);
                if (outList.get(0)) {
                    rectangular(mazeRotatePos.offset(-inSize, -inSizeHeight, -inSize), mazeRotatePos.offset(17, inSizeHeight, inSize), 0, blockMap, 0);
                }
                if (outList.get(1)) {
                    rectangular(mazeRotatePos.offset(-inSize, -inSizeHeight, -inSize), mazeRotatePos.offset(inSize, inSizeHeight, 17), 0, blockMap, 0);
                }
                if (outList.get(2)) {
                    rectangular(mazeRotatePos.offset(inSize, inSizeHeight, inSize), mazeRotatePos.offset(-17, -inSizeHeight, -inSize), 0, blockMap, 0);
                }
                if (outList.get(3)) {
                    rectangular(mazeRotatePos.offset(inSize, inSizeHeight, inSize), mazeRotatePos.offset(-inSize, -inSizeHeight, -17), 0, blockMap, 0);
                }
            }
            for (Map.Entry<Vector3d, List<Boolean>> entry : littleMazePos.entrySet()) {
                key = entry.getKey();
                List<Boolean> outList = new ArrayList<>();
                outList.addAll(entry.getValue());
                mazeRotatePos = new BlockPos((int) key.x, (int) key.y, (int) key.z);
                if (outList.get(0)) {
                    rectangular(mazeRotatePos.offset(0, 0, 0), mazeRotatePos.offset(1, 3, 0), 0, blockMap, 0);
                }
                if (outList.get(1)) {
                    rectangular(mazeRotatePos.offset(0, 0, 0), mazeRotatePos.offset(0, 3, 1), 0, blockMap, 0);
                }
                if (outList.get(2)) {
                    rectangular(mazeRotatePos.offset(0, 0, 0), mazeRotatePos.offset(-1, 3, 0), 0, blockMap, 0);
                }
                if (outList.get(3)) {
                    rectangular(mazeRotatePos.offset(0,0,0), mazeRotatePos.offset(0, 3, -1), 0, blockMap, 0);
                }
            }
            rectangular(underCenter.offset(-inRoomSize, -inSizeHeight, -inRoomSize), underCenter.offset(inRoomSize, inRoomSizeHeight, inRoomSize), 0, blockMap, 0);
            rectangular(underCenter.offset(0, outRoomSizeHeight, 0), underCenter.offset(0, outRoomSizeHeight + 5, 0), 0, blockMap, 0);
            rectangular(underCenter.offset(-5, outRoomSizeHeight + 4, -5), underCenter.offset(5, outRoomSizeHeight + 14, 5), 0, blockMap, 0);
            rectangular(underCenter.offset(-inRoomSize, inRoomSizeHeight, -inRoomSize), underCenter.offset(-inRoomSize, outRoomSizeHeight, -inRoomSize), 0, blockMap, 0);
            for (BlockPos aPos : roomPos) {
                rectangular(aPos.offset(-inRoomSize, -inSizeHeight, -inRoomSize), aPos.offset(inRoomSize, inRoomSizeHeight, inRoomSize), 0, blockMap, 0);
            }
            lineSet(firstChannel, 2.5, 2.5, 0, true, blockMap);

            List<BlockPos> lavaRoom = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                lavaRoom.add(underCenter.offset(random.nextInt(-100, 101), -4, random.nextInt(-100, 101)));
            }
            for (BlockPos aPos : lavaRoom) {
                rectangular(aPos.offset(-outRoomSize, -20, -outRoomSize), aPos.offset(outRoomSize, -3, outRoomSize), 1, blockMap, 0);
                rectangular(aPos.offset(-inRoomSize, -2, -inRoomSize), aPos.offset(inRoomSize, 0, inRoomSize), 2, blockMap, 0);
            }
            for (BlockPos aPos : lavaRoom) {
                rectangular(aPos.offset(-inRoomSize, -17, -inRoomSize), aPos.offset(inRoomSize, -3, inRoomSize), 3, blockMap, 0);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    DecorativeBlocks.BLUE_BRICKS.get().defaultBlockState(),
                    DecorativeBlocks.CRACKED_BLUE_BRICK.get().defaultBlockState(),
                    Blocks.LAVA.defaultBlockState()
            ), builder);
            switch (rotation) {
                case CLOCKWISE_90 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(15, -3, -23), true, true, Rotation.CLOCKWISE_90));
                case CLOCKWISE_180 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(23, -3, 15), true, true, Rotation.CLOCKWISE_180));
                case COUNTERCLOCKWISE_90 -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(-15, -3, 23), true, true, Rotation.COUNTERCLOCKWISE_90));
                default -> builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "dungeon/dungeon_gate", centerPos.offset(-23, -3, -15), true, true, Rotation.NONE));
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DUNGEON.get();
    }
}
