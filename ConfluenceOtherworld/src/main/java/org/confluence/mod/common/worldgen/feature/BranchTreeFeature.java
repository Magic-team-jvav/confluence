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
        int branchLayers = config.branchHeight;
        List<BlockPos> branchRoot = new ArrayList<>();
        List<BlockPos> branchList = new ArrayList<>();
        branchRoot.add(basePos.above(branchY));
        int maxHeight = height;
        int minBranchHeight = BRANCH_HEIGHT_MIN - 3;

        // 检查生成空间是否充足
        if (!checkSpace(level, basePos, height)) {
            return false;
        }

        // 放置树干
        placeTrunk(level, basePos, height, trunkBlockState);

        // 计算并放置所有侧面树枝
        maxHeight = placeBranches(level, basePos, branchRoot, branchList, branchBlockState,
                branchLayers, minBranchHeight, random, maxHeight);

        // 为树干顶部补充树枝
        if (maxHeight > height) {
            placeTrunkTopBranches(level, basePos, height, maxHeight, branchBlockState, branchList);
        }

        // 更新所有树枝的状态
        updateBranchStates(level, branchList, branchBlockState, branchBlock, trunkBlock);

        return true;
    }

    /**
     * 检查生成空间是否充足
     */
    private boolean checkSpace(WorldGenLevel level, BlockPos basePos, int height) {
        for (int y = 0; y < height; y++) {
            BlockState blockState = level.getBlockState(basePos.above(y));
            if (!(blockState.canBeReplaced() || blockState.is(BlockTags.LEAVES))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 放置树干
     */
    private void placeTrunk(WorldGenLevel level, BlockPos basePos, int height, BlockState trunkBlockState) {
        for (int y = 0; y < height; y++) {
            level.setBlock(basePos.above(y), trunkBlockState, 3);
        }
    }

    /**
     * 计算并放置所有侧面树枝
     *
     * @return 更新后的最大高度
     */
    private int placeBranches(WorldGenLevel level, BlockPos basePos, List<BlockPos> branchRoot,
                              List<BlockPos> branchList, BlockState branchBlockState,
                              int branchLayers, int minBranchHeight, RandomSource random, int maxHeight) {
        for (int i = branchLayers; i > 0; i--) {
            int branchLength = 1 << (i - 1);
            int branchVerticalHeight = 2 * i + minBranchHeight;  // 每一层的垂直高度
            List<BlockPos> nextBranchRoot = new ArrayList<>();

            for (BlockPos blockPos : branchRoot) {
                for (Direction direction : LibUtils.HORIZONTAL) {
                    BlockPos rootPos = blockPos.above(random.nextInt(BRANCH_HEIGHT_VARIATION));

                    // 水平延伸 - 遇到不可替换方块时停止
                    boolean canExtend = true;
                    for (int j = 1; j <= branchLength && canExtend; j++) {
                        rootPos = rootPos.relative(direction);
                        if (level.getBlockState(rootPos).canBeReplaced()) {
                            level.setBlock(rootPos, branchBlockState, 3);
                            branchList.add(rootPos);
                            if (j == branchLength) {
                                nextBranchRoot.add(rootPos);
                            }
                        } else {
                            canExtend = false;  // 遇到障碍停止延伸
                        }
                    }

                    // 垂直延伸 - 遇到不可替换方块时停止
                    for (int k = 1; k <= branchVerticalHeight; k++) {
                        rootPos = rootPos.offset(0, 1, 0);
                        if (level.getBlockState(rootPos).canBeReplaced()) {
                            level.setBlock(rootPos, branchBlockState, 3);
                            branchList.add(rootPos);
                            maxHeight = Math.max(maxHeight, rootPos.getY() - basePos.getY());
                        } else {
                            break;  // 遇到障碍停止延伸
                        }
                    }
                }
            }
            branchRoot = nextBranchRoot;
        }
        return maxHeight;
    }

    /**
     * 为树干顶部补充树枝
     */
    private void placeTrunkTopBranches(WorldGenLevel level, BlockPos basePos, int height,
                                       int maxHeight, BlockState branchBlockState, List<BlockPos> branchList) {
        for (int y = height; y <= maxHeight; y++) {
            BlockPos blockPos = basePos.above(y);
            level.setBlock(blockPos, branchBlockState, 3);
            branchList.add(blockPos);
        }
    }

    /**
     * 更新所有树枝的状态（连接方向）
     */
    private void updateBranchStates(WorldGenLevel level, List<BlockPos> branchList,
                                    BlockState branchBlockState, Block branchBlock, Block trunkBlock) {
        for (BlockPos blockPos : branchList) {
            BlockState blockState = branchBlockState;

            // 缓存垂直方向状态
            BlockState aboveState = level.getBlockState(blockPos.above());
            BlockState belowState = level.getBlockState(blockPos.below());

            boolean haveDown = canConnect(belowState, branchBlock, trunkBlock);
            blockState = blockState
                    .trySetValue(BlockStateProperties.UP, canConnect(aboveState, branchBlock, trunkBlock))
                    .trySetValue(BlockStateProperties.DOWN, haveDown);

            // 处理水平方向连接
            for (Direction direction : LibUtils.HORIZONTAL) {
                BlockPos nextPos = blockPos.relative(direction);
                BlockState nextState = level.getBlockState(nextPos);
                if (canConnect(nextState, branchBlock, trunkBlock)) {
                    if (!haveDown || !canConnect(level.getBlockState(nextPos.below()), branchBlock, trunkBlock)) {
                        blockState = blockState.trySetValue(PROPERTY_BY_DIRECTION.get(direction), true);
                    }
                }
            }

            level.setBlock(blockPos, blockState, 3);
        }
    }

    private boolean canConnect(BlockState blockState, Block branch, Block trunk) {
        return blockState.is(branch) || blockState.is(trunk);
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, int height,
                         int branchHeight) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                Codec.INT.fieldOf("height").forGetter(Config::height),
                Codec.INT.fieldOf("branch_height").forGetter(Config::branchHeight)
        ).apply(instance, Config::new));
    }
}
