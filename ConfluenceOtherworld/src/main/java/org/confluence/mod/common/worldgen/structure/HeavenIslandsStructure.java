package org.confluence.mod.common.worldgen.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
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
import org.confluence.lib.util.LibGeometryUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModStructures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.lib.util.LibStructureUtils.*;

public class HeavenIslandsStructure extends Structure {
    public static final MapCodec<HeavenIslandsStructure> CODEC = simpleCodec(HeavenIslandsStructure::new);
    private static final String[] sideMap = new String[]{
            "heaven_islands/sky_island_house_side_0",
            "heaven_islands/sky_island_house_side_1"
    };
    private static final ResourceLocation[] feature = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("patch_grass"),
            ResourceLocation.withDefaultNamespace("patch_tall_grass"),
            ResourceLocation.withDefaultNamespace("flower_flower_forest")
    };
    private static final ResourceLocation[] featureTree = new ResourceLocation[]{
            ResourceLocation.withDefaultNamespace("oak"),
            ResourceLocation.withDefaultNamespace("cherry"),
            Confluence.asResource("yellow_willow")
    };

    protected HeavenIslandsStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos startChunk = context.chunkPos();
        int x = startChunk.getMiddleBlockX();
        int z = startChunk.getMiddleBlockZ();
        if (x * x + z * z <= 40000) {
            return Optional.empty();
        }
        return onTopOfChunkCenter(context, Heightmap.Types.WORLD_SURFACE_WG, builder -> {
            WorldgenRandom random = context.random();
            BlockPos centerPos = new BlockPos(x, random.nextInt(260, 280), z);
            Object2IntMap<BlockPos> blockMap = new Object2IntOpenHashMap<>();

            Map<BlockPos, ResourceLocation> featureMap = new HashMap<>();

            int height = 2;
            int type = random.nextInt(2);
            //type = 0;
            int typeSide0 = random.nextInt(2);
            int typeSide1 = random.nextInt(2);
            int houseFace0 = random.nextInt(2);
            //houseFace0 = 1;
            int houseFace1 = random.nextInt(2);
            int radiusXZ = random.nextInt(24, 30);
            int radiusY = random.nextInt(16, 20);
            int rotateFace = (random.nextInt(2) == 1) ? 1 : -1;
            int rotateCount = random.nextInt(5, 7);
            float rotateStart = 2 * Mth.PI * random.nextFloat();
            float rotateStepFirst = 2 * Mth.PI / rotateCount;
            float rotateStep = (0.12F + 0.05F * random.nextFloat()) * rotateFace;
            double radiusCloud;
            List<Vector3d> vctPosList = new LinkedList<>();

            if (type == 0) {
                ellipsoid(radiusXZ - 4, radiusY - 2, radiusXZ - 4, centerPos, 0, 3, true, blockMap, centerPos.getY() - 2);
                lineSetEllipsoid(LibGeometryUtils.ellipsoidPos((double) radiusXZ / 2.0D, 1.99, (double) radiusXZ / 2.0D, centerPos, 0.2F, random), (double) radiusXZ / 2.0D - 3, 1.9, (double) radiusXZ / 2.0D - 3, 4, true, blockMap);

                LibGeometryUtils.roundPos(centerPos.offset(0, 3, 0), (double) radiusXZ / 2.0D + 2, random, vctPosList, 7, 32, 0.0F);
                lineSetFeature(vctPosList, featureMap, feature, random);

                vctPosList.clear();
                LibGeometryUtils.roundPos(centerPos.offset(0, 3, 0), (double) radiusXZ / 2.0D + 5, random, vctPosList, 1, 4, 0.78539815F);
                lineSetFeature(vctPosList, featureMap, featureTree, random);

                rectangular(centerPos.offset(-7, 2, -5), centerPos.offset(7, 2, 5), 3, blockMap, 0);
                rectangular(centerPos.offset(-5, 2, -7), centerPos.offset(5, 2, 7), 3, blockMap, 0);
                if (houseFace0 == 0) {
                    if (typeSide0 == 0) {
                        rectangular(centerPos.offset(-16, 2, -3), centerPos.offset(-8, 2, 3), 3, blockMap, 0);
                    }
                    if (typeSide1 == 0) {
                        rectangular(centerPos.offset(8, 2, -3), centerPos.offset(16, 2, 3), 3, blockMap, 0);
                    }
                } else {
                    if (typeSide0 == 0) {
                        rectangular(centerPos.offset(-3, 2, -16), centerPos.offset(3, 2, -8), 3, blockMap, 0);
                    }
                    if (typeSide1 == 0) {
                        rectangular(centerPos.offset(-3, 2, 8), centerPos.offset(3, 2, 16), 3, blockMap, 0);
                    }
                }
            } else {
                ellipsoid(radiusXZ - 4, radiusY - 2, radiusXZ - 4, centerPos, 0, 2, true, blockMap, centerPos.getY());
            }

            List<Vector3d> cloudPos = LibGeometryUtils.ellipsoidPos(radiusXZ - 5, radiusY - 3, radiusXZ - 5, radiusXZ, radiusY, radiusXZ, centerPos, 0.4F, random, centerPos.getY());
            lineSet(cloudPos, 3.2, 3.2, 1, true, blockMap);
            for (int i = 0; i < rotateCount; i++) {
                radiusCloud = 3 + 2 * random.nextDouble();
                lineSet(LibGeometryUtils.rotateCloudPos(rotateStart + i * rotateStepFirst, rotateStep, radiusXZ, (double) radiusXZ / 20.0D, random.nextInt(10, 16), centerPos), radiusCloud, 0, 1, true, blockMap);
                lineSet(LibGeometryUtils.rotateCloudPos(rotateStart + i * rotateStepFirst, -rotateStep, radiusXZ, (double) radiusXZ / -15.0D, random.nextInt(10, 14), centerPos), radiusCloud, 0, 1, true, blockMap);
            }
            vctPosList.clear();
            Vector3d littleCloudPos;
            BlockPos littleCloudPosInt;
            LibGeometryUtils.roundPos(centerPos.offset(0, 25, 0), (double) radiusXZ / 2.0D + 8, random, vctPosList, 5, random.nextInt(2, 7), 0.78539815F);
            for (Vector3d vector3d : vctPosList) {
                littleCloudPos = vector3d;
                littleCloudPosInt = new BlockPos(Mth.floor(littleCloudPos.x), Mth.floor((int) littleCloudPos.y + random.nextInt(-5, 6)), Mth.floor(littleCloudPos.z));
                radiusXZ = random.nextInt(5, 8);
                radiusY = random.nextInt(3, 6);
                cloudPos.clear();
                ellipsoid(radiusXZ - 2, radiusY - 2, radiusXZ - 2, littleCloudPosInt.offset(0, 1, 0), 6, 2, true, blockMap, littleCloudPosInt.getY());
                cloudPos = LibGeometryUtils.ellipsoidPos(radiusXZ - 2, radiusY - 2, radiusXZ - 2, radiusXZ, radiusY, radiusXZ, littleCloudPosInt, 0.7F, random, littleCloudPosInt.getY());
                lineSet(cloudPos, 1.5, 1.5, 1, true, blockMap);
            }

            GridPiece.addPieces(blockMap, Lists.newArrayList(
                    Blocks.AIR.defaultBlockState(),
                    NatureBlocks.CLOUD_BLOCK.get().defaultBlockState(),
                    Blocks.WATER.defaultBlockState(),
                    Blocks.STONE.defaultBlockState(),
                    Blocks.GRASS_BLOCK.defaultBlockState(),
                    Blocks.DIRT.defaultBlockState(),
                    NatureBlocks.EVAPORATIVE_CLOUD_BLOCK.get().defaultBlockState()
            ), featureMap, builder);
            if (type == 0) {
                if (houseFace0 == 0) {
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "heaven_islands/sky_island_house", centerPos.offset(((houseFace1 == 0) ? 6 : -6), height, ((houseFace1 == 0) ? -12 : 12)), false, true, ((houseFace1 == 0) ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90)));
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), sideMap[typeSide0], centerPos.offset(-7, height, 9), false, true, Rotation.CLOCKWISE_180));
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), sideMap[typeSide1], centerPos.offset(7, height, -9), false, true, Rotation.NONE));
                } else {
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), "heaven_islands/sky_island_house", centerPos.offset(((houseFace1 == 0) ? 12 : -12), height, ((houseFace1 == 0) ? 6 : -6)), false, true, ((houseFace1 == 0) ? Rotation.CLOCKWISE_180 : Rotation.NONE)));
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), sideMap[typeSide0], centerPos.offset(-9, height, -7), false, true, Rotation.COUNTERCLOCKWISE_90));
                    builder.addPiece(new SimpleTemplatePiece(context.structureTemplateManager(), sideMap[typeSide1], centerPos.offset(9, height, 7), false, true, Rotation.CLOCKWISE_90));
                }
            }
        });
    }

    @Override
    public StructureType<?> type() {
        return ModStructures.HEAVEN_ISLANDS.get();
    }
}
