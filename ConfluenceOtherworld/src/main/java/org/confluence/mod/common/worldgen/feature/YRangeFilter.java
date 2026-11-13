package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.confluence.mod.common.init.ModFeatures;

public class YRangeFilter extends PlacementFilter {
    public static final MapCodec<YRangeFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return minInclusive <= pos.getY() && pos.getY() <= maxInclusive;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.Y_RANGE_FILTER_PLACEMENT_MODIFIER.get();
    }
}
