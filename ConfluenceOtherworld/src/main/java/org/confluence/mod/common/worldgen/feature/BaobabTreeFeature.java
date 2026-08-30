package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.FeatureUtils;

public class BaobabTreeFeature extends Feature<BaobabTreeFeature.Config> {
    // 树木结构常量
    private static final int HEIGHT_VARIATION = 3;          // 高度随机变化范围
    private static final int BRANCH_OFFSET_POS = 3;         // 树枝起始偏移（东/南方向）
    private static final int BRANCH_OFFSET_NEG = -2;        // 树枝起始偏移（西/北方向）
    private static final int BRANCH_HEIGHT_VARIATION = 3;   // 树枝高度随机变化
    private static final int BRANCH_LENGTH_MIN = 1;         // 树枝最小长度
    private static final int BRANCH_LENGTH_MAX = 5;         // 树枝最大长度
    private static final int BRANCH_HEIGHT_MIN = 1;         // 树枝最小高度
    private static final int BRANCH_HEIGHT_MAX = 5;         // 树枝最大高度
    private static final int LEAF_RADIUS_MIN = 2;           // 树叶最小半径
    private static final int LEAF_RADIUS_MAX = 5;           // 树叶最大半径
    private static final int ROOT_START_HEIGHT_MIN = 2;     // 树根起始高度最小值
    private static final int ROOT_START_HEIGHT_MAX = 4;     // 树根起始高度最大值
    private static final int ROOT_LENGTH_MIN = 0;           // 树根最小长度
    private static final int ROOT_LENGTH_MAX = 3;           // 树根最大长度
    private static final int ROOT_MAX_STEP = 10;            // 根系最大检查步数

    public BaobabTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockState trunkBlockState = config.trunk().getState(random, baseBlockPos);
        BlockState branchBlockState = config.branch().getState(random, baseBlockPos);
        BlockState rootBlockState = config.root().getState(random, baseBlockPos);
        BlockState innerBlockState = config.inner().getState(random, baseBlockPos);
        BlockState leavesBlockState = config.leaves().getState(random, baseBlockPos);
        int height = config.height + random.nextInt(HEIGHT_VARIATION);

        // 遇到底部不满足4x4泥土范围的生成地点，直接不生成
        for (int kx = -1; kx < 3; kx++) {
            for (int kz = -1; kz < 3; kz++) {
                if (!level.getBlockState(baseBlockPos.offset(kx, -1, kz)).is(BlockTags.DIRT))
                    return false;
            }
        }

        // 准备需要使用的Set (fastutil原始类型，避免自动装箱)
        LongOpenHashSet trunkSet = new LongOpenHashSet();
        LongOpenHashSet innerSet = new LongOpenHashSet();
        LongOpenHashSet leavesRootSet = new LongOpenHashSet();
        LongOpenHashSet leavesSet = new LongOpenHashSet();
        LongOpenHashSet branchXSet = new LongOpenHashSet();
        LongOpenHashSet branchYSet = new LongOpenHashSet();
        LongOpenHashSet branchZSet = new LongOpenHashSet();
        LongOpenHashSet collisionSet = new LongOpenHashSet();

        // 分别写入树干、树干的中部填充的坐标
        for (int y = 0; y <= height; y++) {
            for (int x = -1; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    trunkSet.add(baseBlockPos.offset(x, y, z).asLong());
                }
            }
        }
        for (int y = 1; y < height; y++) {
            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    innerSet.add(baseBlockPos.offset(x, y, z).asLong());
                }
            }
        }

        // 计算树枝的每个方块的坐标
        for (int i = 0; i < 4; i++) {
            Direction t = Direction.from2DDataValue(i);
            int x = t == Direction.EAST ? BRANCH_OFFSET_POS : (t == Direction.WEST ? BRANCH_OFFSET_NEG : random.nextInt(0, 2));
            int z = t == Direction.SOUTH ? BRANCH_OFFSET_POS : (t == Direction.NORTH ? BRANCH_OFFSET_NEG : random.nextInt(0, 2));
            branchPlace(random, baseBlockPos.offset(x, height - random.nextInt(0, BRANCH_HEIGHT_VARIATION), z), t, branchXSet, branchYSet, branchZSet, leavesRootSet);
        }

        // 将树枝和树干都塞进一个统一的集合，便于检查当前位置是否足够放置这棵树，同时便于后续树叶状态更新中输入原木的坐标列表
        collisionSet.addAll(trunkSet);
        collisionSet.addAll(branchXSet);
        collisionSet.addAll(branchYSet);
        collisionSet.addAll(branchZSet);

        // 检查当前位置是否有足够空间放置整棵树
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (long pos : collisionSet) {
            mutablePos.set(pos);
            BlockState blockState = level.getBlockState(mutablePos);
            if (!(blockState.canBeReplaced() || blockState.is(BlockTags.LEAVES))) return false;
        }

        // 预计算三种轴向的方块状态，避免循环中重复计算
        BlockState branchX = branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X);
        BlockState branchY = branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        BlockState branchZ = branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z);

        // 放置树枝
        for (long pos : branchXSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, branchX, 3);
        }
        for (long pos : branchYSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, branchY, 3);
        }
        for (long pos : branchZSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, branchZ, 3);
        }

        // 清空xyz集合用来给Root腾位置
        branchXSet.clear();
        branchYSet.clear();
        branchZSet.clear();

        // ↓由于树叶和树根的放置比较弹性，不需要考虑是否会被方块阻挡，所以在判断后放置，避免计算好了放不下来浪费算力
        // 计算树叶的位置
        for (long pos : leavesRootSet) {
            mutablePos.set(pos);
            int side = random.nextInt(LEAF_RADIUS_MIN, LEAF_RADIUS_MAX);
            leavesPlace(mutablePos, leavesSet, side, level);
            leavesPlace(mutablePos.offset(0, 1, 0), leavesSet, side - 1, level);
        }

        // 计算树根的位置
        for (int i = 0; i < 4; i++) {
            Direction direction = Direction.from2DDataValue(i);
            int x = direction == Direction.EAST ? BRANCH_OFFSET_POS : (direction == Direction.WEST ? BRANCH_OFFSET_NEG : random.nextInt(0, 2));
            int z = direction == Direction.SOUTH ? BRANCH_OFFSET_POS : (direction == Direction.NORTH ? BRANCH_OFFSET_NEG : random.nextInt(0, 2));
            rootPlace(random, baseBlockPos.offset(x, random.nextInt(ROOT_START_HEIGHT_MIN, ROOT_START_HEIGHT_MAX), z), direction, branchXSet, branchYSet, branchZSet, ROOT_MAX_STEP, level);
        }

        // 预计算根部方块状态
        BlockState rootX = rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X);
        BlockState rootY = rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y);
        BlockState rootZ = rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z);

        // 放置树叶、树干、树根、树干内部的填充物
        for (long pos : leavesSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, leavesBlockState, 3);
        }
        for (long pos : trunkSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, trunkBlockState, 3);
        }
        for (long pos : branchXSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, rootX, 3);
        }
        for (long pos : branchYSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, rootY, 3);
        }
        for (long pos : branchZSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, rootZ, 3);
        }
        for (long pos : innerSet) {
            mutablePos.set(pos);
            level.setBlock(mutablePos, innerBlockState, 3);
        }

        // 重新计算树叶的状态并更新
        FeatureUtils.updateLeavesOptimized(level, collisionSet, leavesSet, true, false);

        return true;
    }

    /// 生成树枝
    ///
    /// @param random     随机
    /// @param blockPos   基点坐标
    /// @param direction  树枝方向
    /// @param branchX    存放X方向树枝的Set
    /// @param branchY    存放Y方向树枝的Set
    /// @param branchZ    存放Z方向树枝的Set
    /// @param leavesRoot 存放树叶根部的Set
    private void branchPlace(RandomSource random, BlockPos blockPos,
                             Direction direction,
                             LongSet branchX, LongSet branchY, LongSet branchZ,
                             LongSet leavesRoot
    ) {
        int length = random.nextInt(BRANCH_LENGTH_MIN, BRANCH_LENGTH_MAX);
        int height = random.nextInt(BRANCH_HEIGHT_MIN, BRANCH_HEIGHT_MAX);
        BlockPos currentPos = blockPos;
        LongOpenHashSet xzSet = new LongOpenHashSet();

        // 水平延伸
        for (int i = 0; i < length; i++) {
            xzSet.add(currentPos.asLong());
            currentPos = currentPos.relative(direction);
        }
        if (direction.getAxis() == Direction.Axis.X) branchX.addAll(xzSet);
        else branchZ.addAll(xzSet);

        // 垂直延伸
        for (int i = 0; i < height; i++) {
            branchY.add(currentPos.asLong());
            if (i == height - 1) {
                leavesRoot.add(currentPos.asLong());
                for (int j = 0; j < 4; j++) {
                    Direction dir = Direction.from2DDataValue(j);
                    if (dir.getAxis() == Direction.Axis.X)
                        branchX.add(currentPos.relative(dir).asLong());
                    else branchZ.add(currentPos.relative(dir).asLong());
                }
            }
            currentPos = currentPos.offset(0, 1, 0);
        }
    }

    @SuppressWarnings("SameParameterValue")
    /// 生成树根
    ///
    /// @param random 随机
    /// @param blockPos 基点坐标
    /// @param direction 树枝方向
    /// @param rootX 存放X方向树根的Set
    /// @param rootY 存放Y方向树根的Set
    /// @param rootZ 存放Z方向树根的Set
    /// @param maxStep 最大檢查步數
    private void rootPlace(RandomSource random, BlockPos blockPos,
                           Direction direction,
                           LongSet rootX, LongSet rootY, LongSet rootZ,
                           int maxStep, WorldGenLevel level
    ) {
        int length = random.nextInt(ROOT_LENGTH_MIN, ROOT_LENGTH_MAX);
        LongOpenHashSet horizontalPositions = new LongOpenHashSet();
        LongOpenHashSet verticalPositions = new LongOpenHashSet();

        if (findRootSupport(blockPos, direction, length, maxStep, level, horizontalPositions, verticalPositions)) {
            rootY.addAll(verticalPositions);
            if (direction.getAxis() == Direction.Axis.X) rootX.addAll(horizontalPositions);
            else rootZ.addAll(horizontalPositions);
        }
    }

    /**
     * 寻找树根支撑点
     *
     * @param startPos            起始位置
     * @param direction           延伸方向
     * @param maxLength           水平延伸最大长度
     * @param maxStep             垂直下降最大步数
     * @param level               世界
     * @param horizontalPositions 收集水平方向坐标
     * @param verticalPositions   收集垂直方向坐标
     * @return true 如果找到支撑点
     */
    private boolean findRootSupport(BlockPos startPos, Direction direction,
                                    int maxLength, int maxStep, WorldGenLevel level,
                                    LongOpenHashSet horizontalPositions,
                                    LongOpenHashSet verticalPositions) {
        BlockPos currentPos = startPos;

        // 检查起始位置
        if (!level.getBlockState(currentPos).canBeReplaced()) return false;

        // 水平延伸
        for (int i = 0; i < maxLength; i++) {
            horizontalPositions.add(currentPos.asLong());
            currentPos = currentPos.relative(direction);
            if (!level.getBlockState(currentPos).canBeReplaced()) return true;
        }

        // 垂直下降寻找支撑
        for (int i = 0; i < maxStep; i++) {
            verticalPositions.add(currentPos.asLong());
            currentPos = currentPos.offset(0, -1, 0);
            if (!level.getBlockState(currentPos).canBeReplaced()) return true;
        }

        return false;
    }

    private void leavesPlace(BlockPos blockPos, LongSet leavesSet, int side, WorldGenLevel level) {
        for (int x = 1 - side; x <= side - 1; x++) {
            for (int z = -side; z <= side; z++) {
                BlockPos leafPos = blockPos.offset(x, 0, z);
                if (level.getBlockState(leafPos).canBeReplaced()) leavesSet.add(leafPos.asLong());
            }
        }
        int x = -side;
        for (int z = 1 - side; z <= side - 1; z++) {
            BlockPos leafPos = blockPos.offset(x, 0, z);
            if (level.getBlockState(leafPos).canBeReplaced()) leavesSet.add(leafPos.asLong());
        }
        x = side;
        for (int z = 1 - side; z <= side - 1; z++) {
            BlockPos leafPos = blockPos.offset(x, 0, z);
            if (level.getBlockState(leafPos).canBeReplaced()) leavesSet.add(leafPos.asLong());
        }
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch,
                         BlockStateProvider root, BlockStateProvider leaves,
                         BlockStateProvider inner, int height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                BlockStateProvider.CODEC.fieldOf("root_block").forGetter(Config::root),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                BlockStateProvider.CODEC.fieldOf("inner_block").forGetter(Config::inner),
                Codec.INT.fieldOf("height").forGetter(BaobabTreeFeature.Config::height)
        ).apply(instance, Config::new));
    }
}
