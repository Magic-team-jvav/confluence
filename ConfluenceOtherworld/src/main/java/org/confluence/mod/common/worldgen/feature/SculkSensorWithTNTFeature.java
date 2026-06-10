package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.NotNull;

public class SculkSensorWithTNTFeature extends Feature<SculkSensorWithTNTFeature.Config> {
    public SculkSensorWithTNTFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos blockPos = context.origin();
        if (!LibFeatureUtils.isPosAir(level, blockPos)) return false;
        BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
        for (int v = 1; v <= config.maxSearchDown && LibFeatureUtils.isPosAir(level, mutablePos); ++v) {
            mutablePos.move(0, -1, 0);
        }
        if (LibFeatureUtils.safeSetBlock(level, mutablePos.above(), Blocks.SCULK_SENSOR.defaultBlockState(), ModFeatures.IS_REPLACEABLE)) {
            return LibFeatureUtils.safeSetBlock(level, mutablePos, FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get().defaultBlockState(), ModFeatures.IS_REPLACEABLE)
                    && LibFeatureUtils.safeSetBlock(level, mutablePos.below(), Blocks.TNT.defaultBlockState(), ModFeatures.IS_REPLACEABLE);
        }
        return false;
    }

    public record Config(int maxSearchDown) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_search_down", 64).forGetter(Config::maxSearchDown)
        ).apply(instance, Config::new));
    }
}
