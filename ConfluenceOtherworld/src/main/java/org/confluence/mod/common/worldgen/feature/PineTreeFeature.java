package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.TreeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (chinese_style) {
            //TODO
        } else {
            Set<BlockPos> rootSet = new HashSet<>();
            Set<BlockPos> trunkSet = new HashSet<>();
            Set<BlockPos> leavesSet = new HashSet<>();
            List<BlockPos> checkPosList = new ArrayList<>();
            List<BlockPos> trunkPosXList = new ArrayList<>();
            List<BlockPos> trunkPosYList = new ArrayList<>();
            List<BlockPos> trunkPosZList = new ArrayList<>();
            List<BlockPos> leavesPosList = new ArrayList<>();
            List<BlockPos> vinePosList = new ArrayList<>();
            boolean placed = true;
            int maxSide = 0;
            for (int i = 0; i < (height) / 4; i++) {
                boolean directionRotate = (i % 2 == 0);
                int length = i * 2 + 1;
                maxSide = Math.max(maxSide, length);
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
                        rootSet.add(brunchPos);
                    }
                }
            }
            for (int i = 0; i < height; i++) {
                checkPosList.add(baseBlockPos.offset(0, i, 0));
                trunkPosYList.add(baseBlockPos.offset(0, i, 0));
                rootSet.add(baseBlockPos.offset(0, i, 0));
            }
            leavesPosList.add(baseBlockPos.offset(0, height, 0));
            leavesPosList.add(baseBlockPos.offset(0, height + 1, 0));
            for (BlockPos checkPos : checkPosList) {
                placed = placed && level.getBlockState(checkPos).canBeReplaced();
                if (!placed) break;
            }
            if (placed) {
                for (BlockPos vinePos : vinePosList) {
                    level.setBlock(vinePos, vineBlockState, 3);
                }
                for (BlockPos leavesPos : leavesPosList) {
                    level.setBlock(leavesPos, leavesBlockState, 3);
                }
                for (BlockPos trunkPos : trunkPosXList) {
                    level.setBlock(trunkPos, trunkBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X), 3);
                }
                for (BlockPos trunkPos : trunkPosZList) {
                    level.setBlock(trunkPos, trunkBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z), 3);
                }
                for (BlockPos trunkPos : trunkPosYList) {
                    level.setBlock(trunkPos, trunkBlockState, 3);
                }
                maxSide += 2;
                rootSet.add(baseBlockPos.offset(0, height, 0));
                BoundingBox box = new BoundingBox(baseBlockPos.getX() - maxSide, baseBlockPos.getY(), baseBlockPos.getZ() - maxSide, baseBlockPos.getX() + maxSide, baseBlockPos.getY() + height + 2, baseBlockPos.getZ() + maxSide);
                TreeFeature.updateLeaves(level, box, rootSet, trunkSet, leavesSet);
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
