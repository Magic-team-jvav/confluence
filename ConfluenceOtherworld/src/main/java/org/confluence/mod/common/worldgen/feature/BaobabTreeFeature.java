package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.FeatureUtils;

import java.util.ArrayList;
import java.util.List;

public class BaobabTreeFeature extends Feature<BaobabTreeFeature.Config> {
    public BaobabTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    private static final TagKey<Block> LEAVES = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "leaves"));
    private static final TagKey<Block> DIRT = TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("minecraft", "dirt"));

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        final RandomSource random = pContext.random();
        final Config config = pContext.config();
        final WorldGenLevel level = pContext.level();
        final BlockPos baseBlockPos = pContext.origin();
        final BlockState trunkBlockState = config.trunk().getState(random, baseBlockPos);
        final BlockState branchBlockState = config.branch().getState(random, baseBlockPos);
        final BlockState rootBlockState = config.root().getState(random, baseBlockPos);
        final BlockState innerBlockState = config.inner().getState(random, baseBlockPos);
        final BlockState leavesBlockState = config.leaves().getState(random, baseBlockPos);
        final int height = config.height + random.nextInt(3);

        // 遇到底部不满足4x4泥土范围的生成地点，直接不生成
        for (int kx = -1; kx < 3; kx++) {
            for (int kz = -1; kz < 3; kz++) {
                if (!level.getBlockState(baseBlockPos.offset(kx, -1, kz)).is(DIRT)) return false;
            }
        }

        // 准备需要使用的List
        final List<Long> trunkList = new ArrayList<>();
        final List<Long> innerList = new ArrayList<>();
        final List<Long> leavesRootList = new ArrayList<>();
        final List<Long> leavesList = new ArrayList<>();
        final List<Long> logXList = new ArrayList<>();
        final List<Long> logYList = new ArrayList<>();
        final List<Long> logZList = new ArrayList<>();
        final List<Long> debugList = new ArrayList<>();

        // 分别写入树干、树干的中部填充的坐标
        for (int y = 0; y <= height; y++) {
            for (int x = -1; x <= 2; x++) {
                for (int z = -1; z <= 2; z++) {
                    trunkList.add(baseBlockPos.offset(x, y, z).asLong());
                }
            }
        }
        for (int y = 1; y < height; y++) {
            for (int x = 0; x <= 1; x++) {
                for (int z = 0; z <= 1; z++) {
                    innerList.add(baseBlockPos.offset(x, y, z).asLong());
                }
            }
        }

        // 计算树枝的每个方块的坐标
        for (int i = 0; i < 4; i++){
            Direction t = Direction.from2DDataValue(i);
            int x = t == Direction.EAST ? 3 : (t == Direction.WEST ? -2 : random.nextInt(0, 2));
            int z = t == Direction.SOUTH ? 3 : (t == Direction.NORTH ? -2 : random.nextInt(0, 2));
            branchPlace(random, baseBlockPos.offset(x, height - random.nextInt(0, 3), z), t, logXList, logYList, logZList, leavesRootList);
        }

        // 将树枝和树干都塞进一个统一的列表，便于检查当前位置是否足够放置这棵树，同时便于后续树叶状态更新中输入原木的坐标列表
        debugList.addAll(trunkList); debugList.addAll(logXList); debugList.addAll(logYList); debugList.addAll(logZList);

        // 检查当前位置是否有足够空间放置整棵树
        for (long pos : debugList) {
            BlockState blockState = level.getBlockState(BlockPos.of(pos));
            if (!(blockState.canBeReplaced() || blockState.is(LEAVES))) return false;
        }

        // 放置树枝
        logXList.forEach(pos -> level.setBlock(BlockPos.of(pos), branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X), 3));
        logYList.forEach(pos -> level.setBlock(BlockPos.of(pos), branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y), 3));
        logZList.forEach(pos -> level.setBlock(BlockPos.of(pos), branchBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z), 3));

        // 清空xyz列表用来给Root腾位置，节约List数量
        logXList.clear(); logYList.clear(); logZList.clear();

        // ↓由于树叶和树根的放置比较弹性，不需要考虑是否会被方块阻挡，所以在判断后放置，避免计算好了放不下来浪费算力
        // 计算树叶的位置
        leavesRootList.forEach(pos -> {
            BlockPos blockPos = BlockPos.of(pos);
            int side = random.nextInt(2, 5);
            leavesPlace(blockPos, leavesList, side, level);
            leavesPlace(blockPos.offset(0, 1, 0), leavesList, side - 1, level);
        });

        // 计算树根的位置
        for (int i = 0; i < 4; i++){
            Direction t = Direction.from2DDataValue(i);
            int x = t == Direction.EAST ? 3 : (t == Direction.WEST ? -2 : random.nextInt(0, 2));
            int z = t == Direction.SOUTH ? 3 : (t == Direction.NORTH ? -2 : random.nextInt(0, 2));
            rootPlace(random, baseBlockPos.offset(x, random.nextInt(2, 4), z), t, logXList, logYList, logZList, 10, level);
        }

        // 放置树叶、树干、树根、树干内部的填充物
        leavesList.forEach(pos -> level.setBlock(BlockPos.of(pos), leavesBlockState, 3));
        trunkList.forEach(pos -> level.setBlock(BlockPos.of(pos), trunkBlockState, 3));
        logXList.forEach(pos -> level.setBlock(BlockPos.of(pos), rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.X), 3));
        logYList.forEach(pos -> level.setBlock(BlockPos.of(pos), rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Y), 3));
        logZList.forEach(pos -> level.setBlock(BlockPos.of(pos), rootBlockState.trySetValue(BlockStateProperties.AXIS, Direction.Axis.Z), 3));
        innerList.forEach(pos -> level.setBlock(BlockPos.of(pos), innerBlockState, 3));

        // 重新计算树叶的状态并更新
        FeatureUtils.updateLeavesOptimized(level, debugList, leavesList, true, false);

        return true;
    }

    public record Config(BlockStateProvider trunk, BlockStateProvider branch, BlockStateProvider root, BlockStateProvider leaves, BlockStateProvider inner, int height) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("trunk_block").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("branch_block").forGetter(Config::branch),
                BlockStateProvider.CODEC.fieldOf("root_block").forGetter(Config::root),
                BlockStateProvider.CODEC.fieldOf("leaves_block").forGetter(Config::leaves),
                BlockStateProvider.CODEC.fieldOf("inner_block").forGetter(Config::inner),
                Codec.INT.fieldOf("height").forGetter(BaobabTreeFeature.Config::height)
        ).apply(instance, Config::new));
    }

    /// 生成树枝
    ///
    /// @param random 随机
    /// @param blockPos 基点坐标
    /// @param direction 树枝方向
    /// @param branchX 存放X方向树枝的Set
    /// @param branchY 存放Y方向树枝的Set
    /// @param branchZ 存放Z方向树枝的Set
    /// @param leavesRoot 存放树叶根部的Set
    private void branchPlace(RandomSource random, BlockPos blockPos,
                             Direction direction,
                             List<Long> branchX, List<Long> branchY, List<Long> branchZ,
                             List<Long> leavesRoot
    ) {
        int length = random.nextInt(1, 5);
        int height = random.nextInt(1, 5);
        List<Long> xzSet = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            xzSet.add(blockPos.asLong());
            blockPos = blockPos.relative(direction);
        }
        if (direction.getAxis() == Direction.Axis.X) branchX.addAll(xzSet);
        else branchZ.addAll(xzSet);
        for (int i = 0; i < height; i++) {
            branchY.add(blockPos.asLong());
            if (i == height - 1) {
                leavesRoot.add(blockPos.asLong());
                for (int j = 0; j < 4 ;j++) {
                    Direction t = Direction.from2DDataValue(j);
                    if (t.getAxis() == Direction.Axis.X) branchX.add(blockPos.relative(t).asLong());
                    else branchZ.add(blockPos.relative(t).asLong());
                }
            }
            blockPos = blockPos.offset(0, 1, 0);
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
                           List<Long> rootX, List<Long> rootY, List<Long> rootZ,
                           int maxStep, WorldGenLevel level
    ) {
        boolean place = false; // 此处的place是判断根系有没有落地来决定放不放置的。可能有点反逻辑
        int length = random.nextInt(0, 3);
        List<Long> xzSet = new ArrayList<>();
        List<Long> ySet = new ArrayList<>();

        checkPlace: {
            if (!level.getBlockState(blockPos).canBeReplaced()) {
                break checkPlace;
            }
            for (int i = 0; i < length; i++) {
                xzSet.add(blockPos.asLong());
                blockPos = blockPos.relative(direction);
                if (!level.getBlockState(blockPos).canBeReplaced()) {
                    place = true;
                    break checkPlace;
                }
            }
            for (int i = 0; i < maxStep; i++) {
                ySet.add(blockPos.asLong());
                blockPos = blockPos.offset(0, -1, 0);
                if (!level.getBlockState(blockPos).canBeReplaced()) {
                    place = true;
                    break checkPlace;
                }
            }
        }

        if (place) {
            rootY.addAll(ySet);
            if (direction.getAxis() == Direction.Axis.X) rootX.addAll(xzSet);
            else rootZ.addAll(xzSet);
        }
    }

    private void leavesPlace(BlockPos blockPos, List<Long> leavesList, int side, WorldGenLevel level) {
        for (int x = 1 - side; x <= side - 1; x++) {
            for (int z = -side; z <= side; z++) {
                BlockPos debugPos = blockPos.offset(x, 0, z);
                if (level.getBlockState(debugPos).canBeReplaced()) leavesList.add(debugPos.asLong());
            }
        }
        int x = -side;
        for (int z = 1 - side; z <= side - 1; z++) {
            BlockPos debugPos = blockPos.offset(x, 0, z);
            if (level.getBlockState(debugPos).canBeReplaced()) leavesList.add(debugPos.asLong());
        }
        x = side;
        for (int z = 1 - side; z <= side - 1; z++) {
            BlockPos debugPos = blockPos.offset(x, 0, z);
            if (level.getBlockState(debugPos).canBeReplaced()) leavesList.add(debugPos.asLong());
        }
    }
}
