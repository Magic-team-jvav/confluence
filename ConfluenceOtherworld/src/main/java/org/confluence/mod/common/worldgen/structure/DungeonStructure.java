package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.lib.util.BooleanStorage4;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.lib.util.StructureUtils.*;
import static org.confluence.lib.util.VectorUtils.listRandom;
import static org.confluence.lib.util.VectorUtils.mazePos;

public class DungeonStructure extends Structure {
    public static final MapCodec<DungeonStructure> CODEC = simpleCodec(DungeonStructure::new);
    public static final String[] houses = new String[]{
            "dungeon/blue_house_0",
            "dungeon/blue_house_1",
            "dungeon/blue_house_2",
            "dungeon/blue_house_3",
            "dungeon/blue_house_4",
            "dungeon/blue_house_5",
            "dungeon/blue_house_6"
    };
    public static final String[] corners = new String[]{
            "dungeon/blue_house_corner_0",
            "dungeon/blue_house_corner_1"
    };
    public static final String[] corners_in = new String[]{
            "dungeon/blue_house_corner_in_0",
            "dungeon/blue_house_corner_in_1",
            "dungeon/blue_house_corner_in_2",
            "dungeon/blue_house_corner_in_3",
            "dungeon/blue_house_corner_in_4",
            "dungeon/blue_house_corner_in_5"
    };
    public static final String[] bridge = new String[]{
            "dungeon/blue_bridge_0",
            "dungeon/blue_bridge_1"
    };
    public static final String stairs = "dungeon/blue_stairs";
    public static final String stairs_down = "dungeon/blue_stairs_down";
    public static final String gate = "dungeon/dungeon_gate";

    protected DungeonStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        if ((x * x + z * z) <= 5760000 || lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Vector3d key;
            BooleanStorage4 value;
            BooleanStorage4 valueB;
            Map<BlockPos, Integer> houseMap = new HashMap<>();
            Map<BlockPos, Integer> cornersInMap = new HashMap<>();
            Map<BlockPos, Integer> bridgeMap = new HashMap<>();
            BlockPos mazeRotatePos;
            BlockPos houseKey;
            int houseValue;
            int outRoomSizeHeight = 24;
            int stairsFacing = 0;

            Rotation rotation = Util.getRandom(Rotation.values(), random);
            List<Vector3d> firstChannel = new ArrayList<>();
            List<Integer> housesList = new ArrayList<>();
            int goldenCount = random.nextInt(7, 9);
            int commonCount = random.nextInt(5, 7);
            int clockCount = random.nextInt(4, 7);

            for (int i = 0; i < goldenCount; i++) {
                housesList.add(2);
            }
            for (int i = 0; i < commonCount; i++) {
                housesList.add(random.nextInt(housesList.size()), 0);
            }
            for (int i = 0; i < clockCount; i++) {
                housesList.add(random.nextInt(housesList.size()), 1);
            }

            Vector3d vct = new Vector3d(centerPos.getX(), centerPos.getY() - 4, centerPos.getZ());
            firstChannel.add(vct);
            vct = new Vector3d(centerPos.getX(), random.nextInt(-25, -10), centerPos.getZ());
            firstChannel.add(new Vector3d(vct.x, vct.y + outRoomSizeHeight + 10, vct.z));
            VectorUtils.lightningPathList(firstChannel, 2, 8, random);
            lineSet(firstChannel, 5.5, 5.5, 1, true, blockMap);

            BlockPos underCenter = new BlockPos(centerPos.getX(), (int) vct.y, centerPos.getZ());

            Map<Vector3d, BooleanStorage4> mazeMap = mazePos(new Vector3d(underCenter.getX(), underCenter.getY(), underCenter.getZ()), 40, 2, random, 1.0F);

            rectangular(underCenter.offset(-103, -4, -103), underCenter.offset(103, 49, 103), 1, blockMap, 0);
            rectangular(underCenter.offset(-10, 49, -10), underCenter.offset(10, 55, 10), 1, blockMap, 0);
            rectangular(underCenter.offset(-99, 0, -99), underCenter.offset(99, 45, 99), 0, blockMap, 0);
            rectangular(underCenter.offset(-6, 45, -6), underCenter.offset(6, 51, 6), 0, blockMap, 0);

            for (Map.Entry<Vector3d, BooleanStorage4> entry : mazeMap.entrySet()) {
                key = entry.getKey();

                value = entry.getValue().copy();
                valueB = new BooleanStorage4((byte) ~value.getValue());

                mazeRotatePos = new BlockPos((int) key.x, (int) key.y, (int) key.z);

                if ((mazeRotatePos.getX() == underCenter.getX()) && (mazeRotatePos.getZ() == underCenter.getZ())) stairsFacing = listRandom(valueB, random);

                rectangular(mazeRotatePos.offset(-6, -1, -6), mazeRotatePos.offset(6, -1, 6), 4, blockMap, 0);
                rectangular(mazeRotatePos.offset(-5, -1, -5), mazeRotatePos.offset(5, -1, 5), 5, blockMap, 0);
                if (value.get(0)) {
                    rectangular(mazeRotatePos.offset(7, -1, -6), mazeRotatePos.offset(20, -1, 6), 4, blockMap, 0);
                    rectangular(mazeRotatePos.offset(6, -1, -5), mazeRotatePos.offset(20, -1, 5), 5, blockMap, 0);
                }
                if (value.get(1)) {
                    rectangular(mazeRotatePos.offset(-6, -1, 7), mazeRotatePos.offset(6, -1, 20), 4, blockMap, 0);
                    rectangular(mazeRotatePos.offset(-5, -1, 6), mazeRotatePos.offset(5, -1, 20), 5, blockMap, 0);
                }
                if (value.get(2)) {
                    rectangular(mazeRotatePos.offset(-7, -1, -6), mazeRotatePos.offset(-20, -1, 6), 4, blockMap, 0);
                    rectangular(mazeRotatePos.offset(-6, -1, -5), mazeRotatePos.offset(-20, -1, 5), 5, blockMap, 0);
                }
                if (value.get(3)) {
                    rectangular(mazeRotatePos.offset(-6, -1, -7), mazeRotatePos.offset(6, -1, -20), 4, blockMap, 0);
                    rectangular(mazeRotatePos.offset(-5, -1, -6), mazeRotatePos.offset(5, -1, -20), 5, blockMap, 0);
                }

                if (!value.get(0)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, -4), 0);
                    houseMap.put(mazeRotatePos.offset(12, -1, -12), 0);
                    houseMap.put(mazeRotatePos.offset(12, -1, 4), 0);
                }
                if (!value.get(1)) {
                    houseMap.put(mazeRotatePos.offset(-4, -1, 12), 1);
                    houseMap.put(mazeRotatePos.offset(12, -1, 12), 1);
                    houseMap.put(mazeRotatePos.offset(4, -1, 12), 1);
                }
                if (!value.get(2)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, -4), 2);
                    houseMap.put(mazeRotatePos.offset(-12, -1, 12), 2);
                    houseMap.put(mazeRotatePos.offset(-12, -1, 4), 2);
                }
                if (!value.get(3)) {
                    houseMap.put(mazeRotatePos.offset(-4, -1, -12), 3);
                    houseMap.put(mazeRotatePos.offset(-12, -1, -12), 3);
                    houseMap.put(mazeRotatePos.offset(4, -1, -12), 3);
                }

                if (!value.get(0) && value.get(1)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, 12), 0);
                }
                if (!value.get(0) && value.get(3)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, -20), 0);
                }
                if (!value.get(1) && value.get(2)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, 12), 1);
                }
                if (!value.get(1) && value.get(0)) {
                    houseMap.put(mazeRotatePos.offset(20, -1, 12), 1);
                }
                if (!value.get(2) && value.get(3)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, -12), 2);
                }
                if (!value.get(2) && value.get(1)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, 20), 2);
                }
                if (!value.get(3) && value.get(0)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, -12), 3);
                }
                if (!value.get(3) && value.get(2)) {
                    houseMap.put(mazeRotatePos.offset(-20, -1, -12), 3);
                }

                if (value.get(0) && value.get(1)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, 12), 4);
                }
                if (value.get(1) && value.get(2)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, 12), 5);
                }
                if (value.get(2) && value.get(3)) {
                    houseMap.put(mazeRotatePos.offset(-12, -1, -12), 6);
                }
                if (value.get(3) && value.get(0)) {
                    houseMap.put(mazeRotatePos.offset(12, -1, -12), 7);
                }

                if (!value.get(0) && !value.get(1)) {
                    cornersInMap.put(mazeRotatePos.offset(12, -1, 12), 0);
                }
                if (!value.get(1) && !value.get(2)) {
                    cornersInMap.put(mazeRotatePos.offset(-12, -1, 12), 1);
                }
                if (!value.get(2) && !value.get(3)) {
                    cornersInMap.put(mazeRotatePos.offset(-12, -1, -12), 2);
                }
                if (!value.get(3) && !value.get(0)) {
                    cornersInMap.put(mazeRotatePos.offset(12, -1, -12), 3);
                }

                if (!value.get(0) && !value.get(2) && !(value.get(0) && value.get(2) && value.get(1) && value.get(3)) && random.nextBoolean() && ((mazeRotatePos.getX() != underCenter.getX()) || (mazeRotatePos.getX() != underCenter.getX()))) {
                    bridgeMap.put(mazeRotatePos.offset(13, -1, 5), 1);
                }
                if (!value.get(1) && !value.get(3) && !(value.get(0) && value.get(2) && value.get(1) && value.get(3)) && random.nextBoolean() && ((mazeRotatePos.getX() != underCenter.getX()) || (mazeRotatePos.getX() != underCenter.getX()))) {
                    bridgeMap.put(mazeRotatePos.offset(-5, 0, 13), 0);
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : houseMap.entrySet()) {
                if ((entry.getValue() < 4) && (housesList.size() < houseMap.size())) {
                    housesList.add(random.nextInt(housesList.size() + 1), random.nextInt(3, houses.length));
                }
            }

            lineSet(firstChannel, 2.5, 2.5, 0, true, blockMap);
            rectangular(underCenter.offset(-2, 48, -2), underCenter.offset(2, 48, 2), 6, blockMap, 0);
            rectangular(underCenter.offset(-1, 48, -1), underCenter.offset(1, 48, 1), 1, blockMap, 0);
            rectangular(underCenter.offset(-7, -11, -7), underCenter.offset(7, 0, 7), 0, blockMap, 0);
            rectangular(underCenter.offset(-7, -12, -7), underCenter.offset(7, -12, 7), 1, blockMap, 0);

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    DecorativeBlocks.BLUE_BRICKS.get().defaultBlockState(),
                    DecorativeBlocks.CRACKED_BLUE_BRICKS.get().defaultBlockState(),
                    Blocks.LAVA.defaultBlockState(),
                    Blocks.POLISHED_DEEPSLATE.defaultBlockState(),
                    Blocks.DEEPSLATE_TILES.defaultBlockState(),
                    DecorativeBlocks.CHISELED_BLUE_BRICKS.get().defaultBlockState()
            ), builder);
            StructureTemplateManager manager = context.structureTemplateManager();
            switch (rotation) {
                case CLOCKWISE_90 ->
                        builder.addPiece(new SimpleTemplatePiece(manager, gate, centerPos.offset(15, -3, -23), true, true, Rotation.CLOCKWISE_90));
                case CLOCKWISE_180 ->
                        builder.addPiece(new SimpleTemplatePiece(manager, gate, centerPos.offset(23, -3, 15), true, true, Rotation.CLOCKWISE_180));
                case COUNTERCLOCKWISE_90 ->
                        builder.addPiece(new SimpleTemplatePiece(manager, gate, centerPos.offset(-15, -3, 23), true, true, Rotation.COUNTERCLOCKWISE_90));
                default -> builder.addPiece(new SimpleTemplatePiece(manager, gate, centerPos.offset(-23, -3, -15), true, true, Rotation.NONE));
            }

            int listCount = 0;

            for (Map.Entry<BlockPos, Integer> entry : houseMap.entrySet()) {
                houseKey = entry.getKey();
                houseValue = entry.getValue();
                switch (houseValue) {
                    case 4:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners, random), houseKey.offset(-1, 0, -1), false, false, Rotation.NONE));
                        break;
                    case 5:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners, random), houseKey.offset(1, 0, -1), false, false, Rotation.CLOCKWISE_90));
                        break;
                    case 6:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners, random), houseKey.offset(1, 0, 1), false, false, Rotation.CLOCKWISE_180));
                        break;
                    case 7:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners, random), houseKey.offset(-1, 0, 1), false, false, Rotation.COUNTERCLOCKWISE_90));
                        break;
                    case 8:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 0, -1), true, false, Rotation.NONE));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 6, -1), true, false, Rotation.NONE));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 12, -1), true, false, Rotation.NONE));
                        break;
                    case 9:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 0, -1), true, false, Rotation.CLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 6, -1), true, false, Rotation.CLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 12, -1), true, false, Rotation.CLOCKWISE_90));
                        break;
                    case 10:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 0, 1), true, false, Rotation.CLOCKWISE_180));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 6, 1), true, false, Rotation.CLOCKWISE_180));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 12, 1), true, false, Rotation.CLOCKWISE_180));
                        break;
                    case 11:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 0, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 6, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 12, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        break;
                    case 0:
                        builder.addPiece(new SimpleTemplatePiece(manager, houses[housesList.get(listCount)], houseKey.offset(-1, 0, -1), false, false, Rotation.NONE));
                        listCount++;
                        break;
                    case 1:
                        builder.addPiece(new SimpleTemplatePiece(manager, houses[housesList.get(listCount)], houseKey.offset(1, 0, -1), false, false, Rotation.CLOCKWISE_90));
                        listCount++;
                        break;
                    case 2:
                        builder.addPiece(new SimpleTemplatePiece(manager, houses[housesList.get(listCount)], houseKey.offset(1, 0, 1), false, false, Rotation.CLOCKWISE_180));
                        listCount++;
                        break;
                    default:
                        builder.addPiece(new SimpleTemplatePiece(manager, houses[housesList.get(listCount)], houseKey.offset(-1, 0, 1), false, false, Rotation.COUNTERCLOCKWISE_90));
                        listCount++;
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : cornersInMap.entrySet()) {
                houseKey = entry.getKey();
                houseValue = entry.getValue();
                switch (houseValue) {
                    case 0:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 18, -1), true, false, Rotation.NONE));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 12, -1), true, false, Rotation.NONE));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 6, -1), true, false, Rotation.NONE));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 0, -1), true, false, Rotation.NONE));
                        break;
                    case 1:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 18, -1), true, false, Rotation.CLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 12, -1), true, false, Rotation.CLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 6, -1), true, false, Rotation.CLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 0, -1), true, false, Rotation.CLOCKWISE_90));
                        break;
                    case 2:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 18, 1), true, false, Rotation.CLOCKWISE_180));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 12, 1), true, false, Rotation.CLOCKWISE_180));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 6, 1), true, false, Rotation.CLOCKWISE_180));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(1, 0, 1), true, false, Rotation.CLOCKWISE_180));
                        break;
                    default:
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 18, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 12, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 6, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                        builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(corners_in, random), houseKey.offset(-1, 0, 1), true, false, Rotation.COUNTERCLOCKWISE_90));
                }
            }

            for (Map.Entry<BlockPos, Integer> entry : bridgeMap.entrySet()) {
                houseKey = entry.getKey();
                houseValue = entry.getValue();
                if (houseValue == 0) {
                    builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(bridge, random), houseKey.offset(0, 3 + 6 * random.nextInt(1, 3), 0), true, true, Rotation.COUNTERCLOCKWISE_90));
                } else {
                    builder.addPiece(new SimpleTemplatePiece(manager, Util.getRandom(bridge, random), houseKey.offset(0, 3 + 6 * random.nextInt(1, 3), 0), true, true, Rotation.CLOCKWISE_180));
                }
            }

            switch (stairsFacing) {
                case 0:
                    builder.addPiece(new SimpleTemplatePiece(manager, stairs, underCenter.offset(13, 0, 13), false, true, Rotation.CLOCKWISE_180));
                    break;
                case 1:
                    builder.addPiece(new SimpleTemplatePiece(manager, stairs, underCenter.offset(-13, 0, 13), false, true, Rotation.COUNTERCLOCKWISE_90));
                    break;
                case 2:
                    builder.addPiece(new SimpleTemplatePiece(manager, stairs, underCenter.offset(-13, 0, -13), false, true, Rotation.NONE));
                    break;
                default:
                    builder.addPiece(new SimpleTemplatePiece(manager, stairs, underCenter.offset(13, 0, -13), false, true, Rotation.CLOCKWISE_90));
            }
            builder.addPiece(new SimpleTemplatePiece(manager, stairs_down, underCenter.offset(-13, -11, -13), false, false, Rotation.NONE));

        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.DUNGEON.get();
    }
}
