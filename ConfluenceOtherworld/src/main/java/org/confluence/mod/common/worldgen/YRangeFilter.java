package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.confluence.mod.common.init.ModFeatures;

import java.util.stream.Stream;

/// 只保留 Y 落在 [minInclusive, maxInclusive] 内的位置。原版没有「绝对高度」筛选
/// （SurfaceRelativeThresholdFilter 是相对地表高度图的，HeightRangePlacement 会直接改写 Y），
/// 用于把同一个地物按绝对高度拆成两个 placed feature（例如水中宝箱按 Y=40 分层）。
public class YRangeFilter extends PlacementModifier {
    public static final Codec<YRangeFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("min_inclusive").forGetter(YRangeFilter::minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(YRangeFilter::maxInclusive)
    ).apply(instance, YRangeFilter::new));

    private final int minInclusive;
    private final int maxInclusive;

    public YRangeFilter(int minInclusive, int maxInclusive) {
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static YRangeFilter of(int minInclusive, int maxInclusive) {
        return new YRangeFilter(minInclusive, maxInclusive);
    }

    public int minInclusive() {
        return minInclusive;
    }

    public int maxInclusive() {
        return maxInclusive;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int y = pos.getY();
        return minInclusive <= y && y <= maxInclusive ? Stream.of(pos) : Stream.empty();
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.Y_RANGE_FILTER_PLACEMENT_MODIFIER.get();
    }
}
