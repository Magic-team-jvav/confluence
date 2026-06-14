package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibUtils;

import java.util.ArrayList;
import java.util.List;

import static net.minecraft.world.level.block.PipeBlock.PROPERTY_BY_DIRECTION;

public class BranchTreeFeature extends Feature<BranchTreeFeature.Config> {
    // 常量
    private static final int HEIGHT_VARIATION = 3;          // 高度随机变化范围
    private static final int BRANCH_HEIGHT_VARIATION = 2;   // 树枝高度随机变化
    private static final int BRANCH_HEIGHT_MIN = 3;         // 树枝最小高度

    public BranchTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, basePos);
        BlockState branchBlockState = config.branch().getState(random, basePos);
        Block branchBlock = branchBlockState.getBlock();
        Block trunkBlock = trunkBlockState.getBlock();
        int height = config.height + random.nextInt(HEIGHT_VARIATION);
        int branchY = height / 2;
        int branchLayer = config.branchLayer;
        List<BlockPos> branchRoot = new ArrayList<>();
        List<BlockPos> branchList = new ArrayList<>();
        branchRoot.add(basePos.above(branchY));
        int maxHeight = height;
        int minBranchHeight = BRANCH_HEIGHT_MIN - 3;

        // 检查生成空间是否充足
        for (int y = 0; y < height; y++) {
            BlockState blockState = level.getBlockState(basePos.above(y));
            if (!(blockState.canBeReplaced() || blockState.is(BlockTags.LEAVES))) return false;
        }

        // 放置树干
        for (int y = 0; y < height; y++) {
            level.setBlock(basePos.above(y), trunkBlockState, 3);
        }

        // 计算并放置所有侧面树枝
        for (int i = branchLayer; i > 0; i--) {
            int branchLength = 1 << (i - 1);
            int branchHeight = 2 * i + minBranchHeight;
            List<BlockPos> nextBranchRoot = new ArrayList<>();
            for (BlockPos blockPos : branchRoot) {
                for (Direction direction : LibUtils.HORIZONTAL) {
                    BlockPos rootPos = blockPos.above(random.nextInt(BRANCH_HEIGHT_VARIATION));
                    for (int j = 1; j <= branchLength; j++) {
                        rootPos = rootPos.relative(direction);
                        if (level.getBlockState(rootPos).canBeReplaced()) {
                            level.setBlock(rootPos, branchBlockState, 3);
                            branchList.add(rootPos);
                            if (j == branchLength) nextBranchRoot.add(rootPos);
                        }
                    }
                    for (int k = 1; k <= branchHeight; k++) {
                        rootPos = rootPos.offset(0, 1, 0);
                        if (level.getBlockState(rootPos).canBeReplaced()) {
                            level.setBlock(rootPos, branchBlockState, 3);
                            branchList.add(rootPos);
                            maxHeight = Math.max(maxHeight, rootPos.getY() - basePos.getY());
                        }
                    }
                }
            }
            branchRoot = nextBranchRoot;
        }

        // 为树干顶部补充树枝
        if (maxHeight > height) {
            for (int y = height; y <= maxHeight; y++) {
                BlockPos blockPos = basePos.above(y);
                level.setBlock(blockPos, branchBlockState, 3);
                branchList.add(blockPos);
            }
        }

        // 更新所有树枝的状态
        for (BlockPos blockPos : branchList) {
            BlockState blockState = branchBlockState;
            blockState = blockState.trySetValue(BlockStateProperties.UP, canConnect(level.getBlockState(blockPos.above()), branchBlock, trunkBlock));
            boolean haveDown = canConnect(level.getBlockState(blockPos.below()), branchBlock, trunkBlock);
            blockState = blockState.trySetValue(BlockStateProperties.DOWN, haveDown);
            for (Direction direction : LibUtils.HORIZONTAL) {
                BlockPos nextPos = blockPos.relative(direction);
                if (canConnect(level.getBlockState(nextPos), branchBlock, trunkBlock) && (!haveDown || !canConnect(level.getBlockState(nextPos.below()), branchBlock, trunkBlock))) {
                    blockState = blockState.trySetValue(PROPERTY_BY_DIRECTION.get(direction), true);
                }
            }
            level.setBlock(blockPos, blockState, 3);
        }

        return true;
    }

    private boolean canConnect(BlockState blockState, Block branch, Block trunk) {
        return blockState.is(branch) || blockState.is(trunk);
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, int height, int branchLayer) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("branch_layer").forGetter(Config::branchLayer)
        ).apply(instance, Config::new));
    }
}
