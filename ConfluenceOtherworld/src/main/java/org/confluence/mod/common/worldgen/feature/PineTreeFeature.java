package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.confluence.lib.util.FeatureUtils;
import org.confluence.lib.util.VectorUtils;
import org.confluence.mod.common.block.natural.BaseDroopingPlantsHeadBlock;
import org.joml.Vector3d;

import java.util.*;

import static org.confluence.lib.util.FeatureUtils.updateLeavesCustom;
import static org.confluence.lib.util.FeatureUtils.updateLeavesCustomCheck;

public class PineTreeFeature extends Feature<PineTreeFeature.Config> {
    public PineTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, baseBlockPos);
        BlockState vineBlockState = config.vine().getState(random, baseBlockPos);
        BlockState leavesBlockState = config.leaves().getState(random, baseBlockPos);
        int height = config.height() + random.nextInt(config.heightMore() + 1);
        boolean chinese_style = config.chineseStyle();
        Set<BlockPos> trunkSet = new HashSet<>();
        Set<BlockPos> leavesSet = new HashSet<>();
        List<BlockPos> checkPosList = new ArrayList<>();
        List<BlockPos> trunkPosXList = new ArrayList<>();
        List<BlockPos> trunkPosYList = new ArrayList<>();
        List<BlockPos> trunkPosZList = new ArrayList<>();
        List<BlockPos> leavesPosList = new ArrayList<>();
        List<BlockPos> vinePosList = new ArrayList<>();
        TagKey<Block> SPALING_TAG = TagKey.create(Registries.BLOCK, Objects.requireNonNull(ResourceLocation.tryParse("minecraft:saplings")));

        if (chinese_style) {
            List<Vector3d> trunkList = new ArrayList<>();
            int offset = height / 3 * 2;
            int xOffset = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);
            int zOffset = random.nextInt(offset / 2, offset + 1) * (random.nextBoolean() ? 1 : -1);
            BlockPos mainBrunchPos = baseBlockPos.offset(xOffset, height, zOffset);
            BlockPos midPos1 = baseBlockPos.offset(xOffset / 4, height / 2, zOffset / 4);
            BlockPos midPos2 = baseBlockPos.offset(xOffset / 4 * 3, height / 2, zOffset / 4 * 3);
            BlockPos midPos3 = baseBlockPos.offset(xOffset, height / 2 - 1, zOffset);
            BlockPos midPos4 = baseBlockPos.offset(xOffset / 2 * 3, height / 2, zOffset / 2 * 3);
            BlockPos brunchPos1 = baseBlockPos.offset(xOffset / 8, height / 4, zOffset / 8);
            BlockPos brunchPos2 = baseBlockPos.offset(-xOffset / 3, height / 4, -zOffset / 3);
            BlockPos brunchPos3 = baseBlockPos.offset(-xOffset / 2, height / 4 * 3, -zOffset / 2);
            trunkList.add(VectorUtils.toVector3d(baseBlockPos));
            trunkList.add(VectorUtils.toVector3d(midPos1));
            trunkList.add(VectorUtils.toVector3d(midPos2));
            trunkList.add(VectorUtils.toVector3d(midPos3));
            trunkList.add(VectorUtils.toVector3d(midPos4));
            List<List<Vector3d>> trunkListList = VectorUtils.lightningPathList(trunkList, 1, 0.2F, random, random.nextInt(2,5), 0.6F);
            trunkList.clear();
            trunkList.add(VectorUtils.toVector3d(midPos2));
            trunkList.add(VectorUtils.toVector3d(mainBrunchPos));
            VectorUtils.lightningPathList(trunkList, 1, 0.2F, random);
            trunkListList.add(new ArrayList<>(trunkList));
            trunkList.clear();
            trunkList.add(VectorUtils.toVector3d(brunchPos1));
            trunkList.add(VectorUtils.toVector3d(brunchPos2));
            trunkList.add(VectorUtils.toVector3d(brunchPos3));
            trunkListList.addAll(VectorUtils.lightningPathList(trunkList, 1, 0.2F, random, random.nextInt(2,5), 0.6F));

            for (List<Vector3d> posList : trunkListList) {
                for (Vector3d posVct : posList) {
                    level.setBlock(BlockPos.containing(posVct.x, posVct.y, posVct.z), trunkBlockState, 3);
                }
            }
            return true;
        } else {
            boolean placed = true;
            for (int i = 0; i < (height) / 4; i++) {
                boolean directionRotate = (i % 2 == 0);
                int length = i * 2 + 1;
                int brunchHeight = height - i * 4;
                if (brunchHeight < ((double) height / 3)) break;
                for (int j = 0; j < 4; j++) {
                    Direction direction = Direction.from2DDataValue(j);
                    Direction direction1 = Direction.from2DDataValue((j + 1) % 4);
                    boolean treeFacing = (j % 2 == 0);
                    float randomOffset = random.nextFloat();
                    int TrueBrunchHeight = brunchHeight;
                    if (randomOffset < 0.2) TrueBrunchHeight--;
                    if (randomOffset > 0.8) TrueBrunchHeight++;
                    for (int k = 0; k < length; k++) {
                        BlockPos brunchPos = baseBlockPos.offset(0, TrueBrunchHeight - 1 - (int) (Mth.sqrt(k) * 1.5), 0).relative(direction, directionRotate ? k : (int) (k * 0.72)).relative(direction1, directionRotate ? 0 : (int) (k * 0.72));
                        int leavesSide = (int) (Mth.sqrt(length - k) / 3 + 1);
                        for (int x = -leavesSide; x <= leavesSide; x++) {
                            for (int z = -leavesSide; z <= leavesSide; z++) {
                                int dis = Mth.abs(x) + Mth.abs(z);
                                if ((dis <= leavesSide) && level.getBlockState(brunchPos.offset(x, 0, z)).canBeReplaced() && (random.nextFloat() >= 0.5F * (1 + ((float) (dis - leavesSide) / leavesSide)))) {
                                    BlockPos leavesPos = brunchPos.offset(x, 0, z);
                                    leavesPosList.add(leavesPos);
                                    leavesSet.add(leavesPos);
                                    if (random.nextFloat() < ((float) (length - k) / length) * 0.5) {
                                        int vineLength = random.nextInt(1, (int) ((float) (length - k) / length * 2.5 + 2));
                                        for (int l = 0; l < vineLength; l++) {
                                            if (level.getBlockState(leavesPos.offset(0, -1 - l, 0)).canBeReplaced()) {
                                                vinePosList.add(leavesPos.offset(0, -1 - l, 0));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (level.getBlockState(brunchPos.offset(0, -1, 0)).canBeReplaced()) {
                            BlockPos leavesPos = brunchPos.offset(0, -1, 0);
                            leavesPosList.add(leavesPos);
                            leavesSet.add(leavesPos);
                            if (random.nextFloat() < ((float) (length - k) / length) * 0.5) {
                                int vineLength = random.nextInt(1, (int) ((float) (length - k) / length * 2.5 + 2));
                                for (int l = 0; l < vineLength; l++) {
                                    if (level.getBlockState(leavesPos.offset(0, -1 - l, 0)).canBeReplaced()) {
                                        vinePosList.add(leavesPos.offset(0, -1 - l, 0));
                                    }
                                }
                            }
                        }
                        if (k == (length - 1)) break;
                        if ((k == (length - 2)) && (random.nextFloat() > 0.5)) continue;
                        checkPosList.add(brunchPos);
                        if (treeFacing) trunkPosZList.add(brunchPos);
                        else trunkPosXList.add(brunchPos);
                        trunkSet.add(brunchPos);
                    }
                }
            }
            for (int i = 0; i < height; i++) {
                checkPosList.add(baseBlockPos.offset(0, i, 0));
                trunkPosYList.add(baseBlockPos.offset(0, i, 0));
                trunkSet.add(baseBlockPos.offset(0, i, 0));
            }
            leavesPosList.add(baseBlockPos.offset(0, height, 0));
            leavesPosList.add(baseBlockPos.offset(0, height + 1, 0));
            leavesSet.add(baseBlockPos.offset(0, height, 0));
            leavesSet.add(baseBlockPos.offset(0, height + 1, 0));
            for (BlockPos checkPos : checkPosList) {
                placed = level.getBlockState(checkPos).canBeReplaced() || level.getBlockState(checkPos).is(SPALING_TAG);
                if (!placed) break;
            }
            if (placed) {
                int placeFlags = 19;
                for (BlockPos vinePos : vinePosList) {
                    level.setBlock(vinePos, vineBlockState, placeFlags);
                }
                for (BlockPos leavesPos : leavesPosList) {
                    level.setBlock(leavesPos, leavesBlockState, placeFlags);
                }
                for (BlockPos trunkPos : trunkPosXList) {
                    level.setBlock(trunkPos, trunkBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X), placeFlags);
                }
                for (BlockPos trunkPos : trunkPosZList) {
                    level.setBlock(trunkPos, trunkBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z), placeFlags);
                }
                for (BlockPos trunkPos : trunkPosYList) {
                    level.setBlock(trunkPos, trunkBlockState, placeFlags);
                }
                for (BlockPos vinePos : vinePosList) {
                    if (level.getBlockState(vinePos).is(vineBlockState.getBlock()) && level.getBlockState(vinePos.offset(0, -1, 0)).is(vineBlockState.getBlock())) {
                        if (level.getBlockState(vinePos.offset(0, 1, 0)).is(vineBlockState.getBlock())) level.setBlock(vinePos, vineBlockState.trySetValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.BODY), placeFlags);
                        else level.setBlock(vinePos, vineBlockState.trySetValue(BaseDroopingPlantsHeadBlock.PART, BaseDroopingPlantsHeadBlock.VinePart.TAIL), placeFlags);
                    }
                }
                updateLeavesCustom(level, trunkSet, leavesSet, true);
                //updateLeavesCustomCheck(level, trunkSet, leavesSet, true);
                return true;
            }
        }
        return false;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider vine,
                         BlockStateProvider leaves, int height, int heightMore,
                         boolean chineseStyle) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("vine_block").forGetter(Config::vine),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                Codec.INT.fieldOf("height").forGetter(PineTreeFeature.Config::height),
                Codec.INT.fieldOf("height_more").forGetter(PineTreeFeature.Config::heightMore),
                Codec.BOOL.fieldOf("chinese_style").forGetter(PineTreeFeature.Config::chineseStyle)
        ).apply(instance, Config::new));
    }
}
