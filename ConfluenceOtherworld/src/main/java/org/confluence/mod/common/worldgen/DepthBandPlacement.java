package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import org.confluence.mod.common.init.ModFeatures;

import java.util.stream.Stream;

/// 把 Y 直接设为「高度图高度 + 均匀偏移」，等价于 SurfaceRelativeThresholdFilter 的深度带，但不需要事后筛选。
///
/// 旧写法是「全高度随机 Y + SurfaceRelativeThresholdFilter(带)」：Y 先在整个高度范围里抽，再按带宽筛掉，
/// 命中率会被 带宽 / Y 总高 稀释（例如 -110..-80 在 325 格高里只有 30 格 ≈ 9%），绝大多数尝试都白费。
/// 本放置器让 Y 一开始就落在带内，因此带内的尝试次数可以直接等于目标密度。
///
/// 原版 RandomOffsetPlacement 的 y_spread 被限制在 ±16，做不了 -110 这种大偏移，所以单独实现。
public class DepthBandPlacement extends PlacementModifier {
    public static final Codec<DepthBandPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(DepthBandPlacement::heightmap),
            Codec.INT.fieldOf("min_inclusive").forGetter(DepthBandPlacement::minInclusive),
            Codec.INT.fieldOf("max_inclusive").forGetter(DepthBandPlacement::maxInclusive)
    ).apply(instance, DepthBandPlacement::new));

    private final Heightmap.Types heightmap;
    private final int minInclusive;
    private final int maxInclusive;

    public DepthBandPlacement(Heightmap.Types heightmap, int minInclusive, int maxInclusive) {
        this.heightmap = heightmap;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static DepthBandPlacement of(Heightmap.Types heightmap, int minInclusive, int maxInclusive) {
        return new DepthBandPlacement(heightmap, minInclusive, maxInclusive);
    }

    public Heightmap.Types heightmap() {
        return heightmap;
    }

    public int minInclusive() {
        return minInclusive;
    }

    public int maxInclusive() {
        return maxInclusive;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos) {
        int surface = context.getHeight(heightmap, pos.getX(), pos.getZ());
        int y = surface + UniformInt.of(minInclusive, maxInclusive).sample(random);
        return Stream.of(pos.atY(y));
    }

    @Override
    public PlacementModifierType<?> type() {
        return ModFeatures.DEPTH_BAND_PLACEMENT_MODIFIER.get();
    }
}
