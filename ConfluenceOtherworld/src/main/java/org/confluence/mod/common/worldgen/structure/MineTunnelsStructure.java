package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.confluence.lib.common.worldgen.structure.GridPiece;
import org.confluence.lib.common.worldgen.structure.SimpleTemplatePiece;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.*;

import static net.minecraft.world.level.block.PoweredRailBlock.SHAPE;
import static org.confluence.lib.util.StructureUtils.*;

public class MineTunnelsStructure extends Structure {
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_SUPPORT = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_support");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_BOULDER = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_boulder");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_DART = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_dart");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_STONE_BRICKS = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_stone_bricks");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_TUFF_BRICKS = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_tuff_bricks");
    public static final ResourceKey<ConfiguredFeature<?, ?>> RAIL_SPRUCE_LOG = Confluence.asResourceKey(Registries.CONFIGURED_FEATURE, "rail_spruce_log");
    public static final MapCodec<MineTunnelsStructure> CODEC = simpleCodec(MineTunnelsStructure::new);
    public static final Direction[] facing = new Direction[]{
            Direction.EAST,
            Direction.WEST,
            Direction.SOUTH,
            Direction.NORTH
    };
    public static final int[] railShape = new int[]{5, 4, 7, 6};

    protected MineTunnelsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        int lowestY = getHeight(x, z, context);
        if (x * x + z * z <= 160000 && lowestY < context.chunkGenerator().getSeaLevel() - 16) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = startChunk.getMiddleBlockPosition(lowestY);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();
            Object2IntMap<BlockPos> tunnelsMap = new Object2IntOpenHashMap<>();
            Object2IntMap<BlockPos> translationMap = new Object2IntOpenHashMap<>();
            List<BlockPos> switchMap = new ArrayList<>();
            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();

            boolean setGate = 0.1666666F >= random.nextFloat();
            //setGate = true;
            int gateType = random.nextInt(2);
            int maxY = 20;
            int minY = -40;
            //maxY = -30;
            //minY = -63;
            int length = 100;
            BlockPos underPos = startChunk.getMiddleBlockPosition(random.nextInt(-30, 10));
            //underPos = startChunk.getMiddleBlockPosition(random.nextInt(-45, -43));
            BlockPos tunnelPos;
            BlockPos translationPos;
            int tunnelFacing;
            int xSet;
            int zSet;
            int yOffset = -1;
            int facingType;
            int lengthGate = (int) ((centerPos.getY() - underPos.getY() - 4) * random.nextFloat());
            int type4;
            int facing2;

            blockMap.put(underPos, 0);
            tunnels(maxY, minY, 0, length, 4, 6, 20, tunnelsMap, translationMap, switchMap, random, underPos.offset(3, 0, 0));
            tunnels(maxY, minY, 0, length, 5, 6, 20, tunnelsMap, translationMap, switchMap, random, underPos.offset(-3, 0, 0));
            tunnels(maxY, minY, 0, length, 6, 6, 20, tunnelsMap, translationMap, switchMap, random, underPos.offset(0, 0, 3));
            tunnels(maxY, minY, 0, length, 7, 6, 20, tunnelsMap, translationMap, switchMap, random, underPos.offset(0, 0, -3));
            for (Object2IntMap.Entry<BlockPos> tunnel : tunnelsMap.object2IntEntrySet()) {
                tunnelPos = tunnel.getKey();
                ball(2.9D + 2.0D * random.nextDouble(), tunnelPos, 0, true, blockMap);
            }
            for (Object2IntMap.Entry<BlockPos> tunnel : tunnelsMap.object2IntEntrySet()) {
                tunnelPos = tunnel.getKey();
                xSet = tunnelPos.getX() - underPos.getX() + 5;
                zSet = tunnelPos.getZ() - underPos.getZ() + 5;
                tunnelFacing = tunnel.getIntValue();
                facingType = tunnelFacing % 4;
                if (facingType == 0 || facingType == 1) {
                    rectangular(tunnelPos.offset(0, yOffset, 1), tunnelPos.offset(0, yOffset, -1), 1, blockMap, 0);
                    if (tunnelFacing / 4 == 1) {
                        blockMap.put(tunnelPos, 3);
                    } else if (tunnelFacing / 4 == 2) {
                        blockMap.put(tunnelPos, 4 + facingType);
                    } else {
                        blockMap.put(tunnelPos.offset(0, yOffset + 1, 0), railShape[facingType]);
                    }
                    if (xSet % 10 == 0) {
                        featureMap.put(tunnelPos.offset(0, yOffset, 1), RAIL_SUPPORT.location());
                        featureMap.put(tunnelPos.offset(0, yOffset, -1), RAIL_SUPPORT.location());
                    }
                } else {
                    rectangular(tunnelPos.offset(1, yOffset, 0), tunnelPos.offset(-1, yOffset, 0), 1, blockMap, 0);
                    if (tunnelFacing / 4 == 1) {
                        blockMap.put(tunnelPos, 2);
                    } else if (tunnelFacing / 4 == 2) {
                        blockMap.put(tunnelPos, 4 + facingType);
                    } else {
                        blockMap.put(tunnelPos, railShape[facingType]);
                    }
                    if (zSet % 10 == 0) {
                        featureMap.put(tunnelPos.offset(1, yOffset, 0), RAIL_SUPPORT.location());
                        featureMap.put(tunnelPos.offset(-1, yOffset, 0), RAIL_SUPPORT.location());
                    }
                }
                if (tunnelFacing > 11) {
                    blockMap.put(tunnelPos, tunnelFacing - 4);
                    rectangular(tunnelPos.offset(1, yOffset, 1), tunnelPos.offset(-1, yOffset, -1), 1, blockMap, 0);
                }
            }
            for (BlockPos blockPos : switchMap) {
                blockMap.put(blockPos, 12);
                blockMap.put(blockPos.offset(0, 1, 0), 13);
            }
            for (Object2IntMap.Entry<BlockPos> translation : translationMap.object2IntEntrySet()) {
                translationPos = translation.getKey();
                type4 = translation.getIntValue() % 4;
                facing2 = translation.getIntValue() / 4;
                blockMap.put(translationPos, (facing2 == 0) ? 20 : 21);
                switch (type4) {
                    case 0:
                        blockMap.put(translationPos.offset(0, -1, 0), 24);
                        break;
                    case 1:
                        featureMap.put(translationPos, RAIL_BOULDER.location());
                        break;
                    case 3:
                        featureMap.put(translationPos, RAIL_DART.location());
                        break;
                    default:
                        ellipsoid(4.9, 10.9, 4.9, translationPos.offset(0, -6, 0), 0, true, blockMap);
                        ball(7.9, translationPos.offset(0, -12, 0), 0, 22, true, blockMap, translationPos.getY() - 12);
                        ball(6.4, translationPos.offset(0, -12, 0), 0, 23, true, blockMap, translationPos.getY() - 12);
                }
            }
            if (setGate) {
                rectangular(centerPos.offset(3, 0, 3), underPos.offset(-3, 1, -3), 0, blockMap, 0);

                rectangular(centerPos.offset(3, 0, 3), underPos.offset(-3, 3, -3), 15, blockMap, 0);

                rectangular(centerPos.offset(3, 0, 3), underPos.offset(2, 0, 2), 16, blockMap, 0);
                rectangular(centerPos.offset(3, 0, -3), underPos.offset(2, 0, -2), 16, blockMap, 0);
                rectangular(centerPos.offset(-3, 0, 3), underPos.offset(-2, 0, 2), 16, blockMap, 0);
                rectangular(centerPos.offset(-3, 0, -3), underPos.offset(-2, 0, -2), 16, blockMap, 0);
                rectangular(underPos.offset(3, -1, 3), underPos.offset(-3, -1, -3), 16, blockMap, 0);
                rectangular(underPos.offset(2, -1, 2), underPos.offset(-2, -1, -2), 15, blockMap, 0);
                rectangular(underPos.offset(2, 1, 2), underPos.offset(-2, 4, -2), 0, blockMap, 0);
                rectangular(underPos.offset(1, 1, 1), centerPos.offset(-1, 0, -1), 0, blockMap, 0);

                rectangular(centerPos.offset(3, 0, 3), underPos.offset(3, 0, 3), 14, blockMap, 0);
                rectangular(centerPos.offset(3, 0, -3), underPos.offset(3, 0, -3), 14, blockMap, 0);
                rectangular(centerPos.offset(-3, 0, 3), underPos.offset(-3, 0, 3), 14, blockMap, 0);
                rectangular(centerPos.offset(-3, 0, -3), underPos.offset(-3, 0, -3), 14, blockMap, 0);

                if (gateType == 0) {
                    rectangular(centerPos.offset(1, 0, 0), centerPos.offset(1, -lengthGate, 0), 19, blockMap, 0);
                    rectangular(centerPos.offset(-1, 0, 0), centerPos.offset(-1, -lengthGate, 0), 19, blockMap, 0);
                    rectangular(centerPos.offset(1, -lengthGate, 0), centerPos.offset(-1, -lengthGate, 0), 18, blockMap, 0);
                } else {
                    rectangular(centerPos.offset(0, 0, 1), centerPos.offset(0, -lengthGate, 1), 19, blockMap, 0);
                    rectangular(centerPos.offset(0, 0, -1), centerPos.offset(0, -lengthGate, -1), 19, blockMap, 0);
                    rectangular(centerPos.offset(0, -lengthGate, 1), centerPos.offset(0, -lengthGate, -1), 18, blockMap, 0);
                }
            } else {
                rectangular(underPos.offset(3, -1, 3), underPos.offset(-3, -1, -3), 16, blockMap, 0);
                rectangular(underPos.offset(2, -1, 2), underPos.offset(-2, -1, -2), 15, blockMap, 0);
            }

            for (int xF = -2; xF < 3; xF++) {
                for (int zF = -2; zF < 3; zF++) {
                    featureMap.put(underPos.offset(xF, -2, zF), RAIL_STONE_BRICKS.location());
                }
            }
            featureMap.put(underPos.offset(3, -2, 3), RAIL_SPRUCE_LOG.location());
            featureMap.put(underPos.offset(3, -2, -3), RAIL_SPRUCE_LOG.location());
            featureMap.put(underPos.offset(-3, -2, 3), RAIL_SPRUCE_LOG.location());
            featureMap.put(underPos.offset(-3, -2, -3), RAIL_SPRUCE_LOG.location());
            featureMap.put(underPos.offset(3, -2, 2), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(3, -2, -2), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(-3, -2, 2), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(-3, -2, -2), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(2, -2, 3), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(2, -2, -3), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(-2, -2, 3), RAIL_TUFF_BRICKS.location());
            featureMap.put(underPos.offset(-2, -2, -3), RAIL_TUFF_BRICKS.location());

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    /* 0  */  Blocks.AIR.defaultBlockState(),
                    /* 1  */  Blocks.OAK_PLANKS.defaultBlockState(),
                    /* 2  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.NORTH_SOUTH),
                    /* 3  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.EAST_WEST),
                    /* 4  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.ASCENDING_EAST),
                    /* 5  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.ASCENDING_WEST),
                    /* 6  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.ASCENDING_SOUTH),
                    /* 7  */  FunctionalBlocks.EVER_POWERED_RAIL.get().defaultBlockState().setValue(SHAPE, RailShape.ASCENDING_NORTH),
                    /* 8  */  Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_EAST),
                    /* 9  */  Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.SOUTH_WEST),
                    /* 10 */  Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.SOUTH_EAST),
                    /* 11 */  Blocks.RAIL.defaultBlockState().setValue(RailBlock.SHAPE, RailShape.NORTH_WEST),
                    /* 12 */  Blocks.CHISELED_TUFF_BRICKS.defaultBlockState(),
                    /* 13 */  Blocks.LEVER.defaultBlockState().setValue(FaceAttachedHorizontalDirectionalBlock.FACE, AttachFace.FLOOR),
                    /* 14 */  Blocks.SPRUCE_LOG.defaultBlockState(),
                    /* 15 */  Blocks.STONE_BRICKS.defaultBlockState(),
                    /* 16 */  Blocks.TUFF_BRICKS.defaultBlockState(),
                    /* 17 */  Blocks.LANTERN.defaultBlockState(),
                    /* 18 */  Blocks.SPRUCE_TRAPDOOR.defaultBlockState().setValue(TrapDoorBlock.HALF, Half.TOP),
                    /* 19 */  Blocks.CHAIN.defaultBlockState(),
                    /* 20 */  Blocks.DETECTOR_RAIL.defaultBlockState().setValue(DetectorRailBlock.SHAPE, RailShape.EAST_WEST),
                    /* 21 */  Blocks.DETECTOR_RAIL.defaultBlockState().setValue(DetectorRailBlock.SHAPE, RailShape.NORTH_SOUTH),
                    /* 22 */  Blocks.STONE.defaultBlockState(),
                    /* 23 */  Blocks.LAVA.defaultBlockState(),
                    /* 24 */  FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get().defaultBlockState()
            ), featureMap, builder);
            if (setGate) {
                if (gateType == 0) {
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "mine_gate", centerPos.offset(-4, 0, -4), true, true, Rotation.NONE));
                } else {
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "mine_gate", centerPos.offset(-4, 0, 4), true, true, Rotation.COUNTERCLOCKWISE_90));
                }
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.MINE_TUNNELS.get();
    }

    private static void tunnels(int maxY, int minY, int length, int maxLength, int ownFacing, int rotateCD, int translationCD, Object2IntMap<BlockPos> tunnelsMap, Object2IntMap<BlockPos> translationMap, List<BlockPos> switchMap, WorldgenRandom random, BlockPos ownPos) {

        float tnt = 0.005F; //瞬爆陷阱权重
        float boulder = 0.005F; //巨石权重
        float dart = 0.005F; //毒镖权重
        float lava = 0.0025F; //岩浆权重
        float rotate = 0.01F; //拐弯权重

        if (length + 10 > maxLength) {
            ownFacing = (ownFacing % 4) + 4;
        }
        int facing4 = ownFacing % 4;
        int upDown = ownFacing / 4;
        int setFacing;
        int face = (facing4 == 0 || facing4 == 1) ? 0 : 4;
        Direction offset = facing[facing4];
        if (length < maxLength) {
            if (upDown == 1) {
                if (translationCD <= 0) {
                    float randomTR = random.nextFloat();
                    if (tnt >= randomTR) {
                        translationMap.put(ownPos, face);
                        translationCD = 200;
                    } else if (tnt + boulder >= randomTR) {
                        translationMap.put(ownPos, 1 + face);
                        translationCD = 200;
                    } else if (tnt + boulder + dart >= randomTR) {
                        translationMap.put(ownPos, 2 + face);
                        translationCD = 200;
                    } else if (tnt + boulder + dart + lava >= randomTR) {
                        translationMap.put(ownPos, 3 + face);
                        translationCD = 200;
                    }
                } else if (rotateCD <= 0 && rotate >= random.nextFloat()) {
                    rotateCD = 5;
                    setFacing = random.nextInt(2);
                    if (facing4 == 0 || facing4 == 1) {
                        tunnels(maxY, minY, length + 1 + random.nextInt(-10, 11), maxLength, 6 + setFacing, rotateCD - 1, 2, tunnelsMap, translationMap, switchMap, random, ownPos.offset(0, 0, (setFacing == 0) ? 1 : -1));
                        switchMap.add(ownPos.offset(0, 0, (setFacing == 0) ? -1 : 1));
                        tunnelsMap.put(ownPos, 14 + setFacing);
                    } else {
                        tunnels(maxY, minY, length + 1 + random.nextInt(-10, 11), maxLength, 4 + setFacing, rotateCD - 1, 2, tunnelsMap, translationMap, switchMap, random, ownPos.offset((setFacing == 0) ? 1 : -1, 0, 0));
                        switchMap.add(ownPos.offset((setFacing == 0) ? -1 : 1, 0, 0));
                        tunnelsMap.put(ownPos, 12 + setFacing);
                    }
                }
                float randomCount = random.nextFloat();
                if (0.025F >= randomCount && ownPos.getY() <= maxY) {
                    tunnels(maxY, minY, length + 1, maxLength, facing4 + (upDown + 1) * 4, rotateCD - 1, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), 0, offset.getStepZ()));
                } else if (0.975F <= randomCount && ownPos.getY() >= minY) {
                    tunnels(maxY, minY, length + 1, maxLength, facing4, rotateCD - 1, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), 0, offset.getStepZ()));
                } else {
                    tunnels(maxY, minY, length + 1, maxLength, facing4 + upDown * 4, rotateCD - 1, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), 0, offset.getStepZ()));
                }
                tunnelsMap.put(ownPos, ownFacing);
            } else if (upDown == 0) {
                if (ownPos.getY() <= minY || 0.1F >= random.nextFloat()) {
                    tunnels(maxY, minY, length + 1, maxLength, facing4 + (upDown + 1) * 4, 2, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), -1, offset.getStepZ()));
                } else {
                    tunnels(maxY, minY, length + 1, maxLength, facing4, 2, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), -1, offset.getStepZ()));
                }
                tunnelsMap.put(ownPos.offset(0, -1, 0), ownFacing);
            } else if (upDown == 2) {
                if (ownPos.getY() >= maxY || 0.1F >= random.nextFloat()) {
                    tunnels(maxY, minY, length + 1, maxLength, facing4 + (upDown - 1) * 4, 2, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), 1, offset.getStepZ()));
                } else {
                    tunnels(maxY, minY, length + 1, maxLength, facing4 + upDown * 4, 2, translationCD - 1, tunnelsMap, translationMap, switchMap, random, ownPos.offset(offset.getStepX(), 1, offset.getStepZ()));
                }
                tunnelsMap.put(ownPos, ownFacing);
            }
        }
    }
}
