package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.common.block.natural.TaperedTwoPartBlock;
import org.confluence.mod.common.block.natural.TaperedTwoPartBlock.TaperedTwoPart;

/// 石笋 / 冰锥：在洞穴地面（{@code tip_direction = up}）或天花板（{@code tip_direction = down}）上长出一小簇锥形方块。
///
/// 手感参照原版「大型滴水石」，但做了削减：一次放置的方块总数很少（当前配置为单根 1~2 格，
/// {@code extra_spikes} 为 0，也就是最多 2 格）；所有方块都用 flag 2 写入（不做邻块更新，
/// 避免连锁的形状重组和刻更新），任何一格被占用就直接截短或放弃，
/// 所以一次放置的方块查询与写入都只有个位数，比原版的整片椭圆簇省得多。
public class StalagmiteFeature extends Feature<StalagmiteFeature.Config> {
    public StalagmiteFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        Direction direction = config.tipDirection();
        // 落点允许是「地面方块本身」或者「地面 / 地板上方那一格」，两种摆放修饰符都能直接接
        BlockPos base = baseAt(level, context.origin(), direction);
        if (base == null) return false;
        BlockState block = config.block().getState(random, base);
        if (!placeSpike(level, base, direction, config.height().sample(random), block)) return false;
        // 副锥：在附近再插几根更矮的，散布半径和根数由配置决定
        int extra = config.extraSpikes();
        int radius = config.radius();
        for (int i = 0; i < extra && radius > 0; i++) {
            int dx = random.nextInt(radius * 2 + 1) - radius;
            int dz = random.nextInt(radius * 2 + 1) - radius;
            if (dx == 0 && dz == 0) continue;
            BlockPos side = baseAt(level, base.offset(dx, 0, dz), direction);
            if (side == null) continue;
            placeSpike(level, side, direction, 1 + random.nextInt(2), block);
        }
        return true;
    }

    /// 找可以起锥的那一格：本身是空气、背面的方块能撑住；容忍地面 / 天花板高低差一格，也容忍落点直接给的是那格实心方块
    private static BlockPos baseAt(WorldGenLevel level, BlockPos pos, Direction direction) {
        if (canPlaceAt(level, pos, direction)) return pos;
        BlockPos forward = pos.relative(direction);
        if (canPlaceAt(level, forward, direction)) return forward;
        BlockPos backward = pos.relative(direction.getOpposite());
        return canPlaceAt(level, backward, direction) ? backward : null;
    }

    private static boolean canPlaceAt(WorldGenLevel level, BlockPos pos, Direction direction) {
        if (!level.isEmptyBlock(pos)) return false;
        BlockPos support = pos.relative(direction.getOpposite());
        return level.getBlockState(support).isFaceSturdy(level, support, direction);
    }

    /// 从底格开始沿 direction 放一根锥：贴附着方块的一格是粗底座（base），
    /// 最外一格按实际格数决定——只有 1 格是 tip_single，2 格及以上是 tip_merge；被占用的部分自动截短
    private static boolean placeSpike(WorldGenLevel level, BlockPos base, Direction direction, int height, BlockState block) {
        if (height <= 0) return false;
        BlockPos.MutableBlockPos pos = base.mutable();
        int allowed = 0;
        for (int i = 0; i < height; i++) {
            if (!level.isEmptyBlock(pos)) break;
            allowed++;
            pos.move(direction);
        }
        if (allowed <= 0) return false;
        BlockState body = block.setValue(TaperedTwoPartBlock.TIP_DIRECTION, direction).setValue(TaperedTwoPartBlock.PART, TaperedTwoPart.BASE);
        BlockState tip = body.setValue(TaperedTwoPartBlock.PART, allowed == 1 ? TaperedTwoPart.TIP_SINGLE : TaperedTwoPart.TIP_MERGE);
        pos.set(base);
        for (int i = 0; i < allowed; i++) {
            level.setBlock(pos, i == allowed - 1 ? tip : body, 2);
            pos.move(direction);
        }
        return true;
    }

    public record Config(BlockStateProvider block,
                         Direction tipDirection,
                         IntProvider height,
                         int extraSpikes,
                         int radius
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Config::block),
                Direction.CODEC.optionalFieldOf("tip_direction", Direction.UP).forGetter(Config::tipDirection),
                IntProvider.codec(1, 16).optionalFieldOf("height", ConstantInt.of(2)).forGetter(Config::height),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("extra_spikes", 1).forGetter(Config::extraSpikes),
                ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("radius", 2).forGetter(Config::radius)
        ).apply(instance, Config::new));
    }
}
