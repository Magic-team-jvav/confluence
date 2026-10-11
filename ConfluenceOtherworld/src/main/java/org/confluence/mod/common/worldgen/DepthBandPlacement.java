package org.confluence.mod.common.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public class DepthBandPlacement extends PlacementModifier {
    public static final MapCodec<DepthBandPlacement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
