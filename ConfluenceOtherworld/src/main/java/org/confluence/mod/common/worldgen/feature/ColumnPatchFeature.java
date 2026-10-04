package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibFeatureUtils;

import java.util.HashSet;
import java.util.function.Predicate;

public class ColumnPatchFeature extends Feature<ColumnPatchFeature.Config> {
    public static final Predicate<BlockState> PREDICATE = blockState -> !blockState.isAir() && !blockState.is(BlockTags.FEATURES_CANNOT_REPLACE);

    public ColumnPatchFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        RandomSource random = pContext.random();
        BlockPos blockPos = pContext.origin();
        if (LibFeatureUtils.isPosAir(level, blockPos)) {
            BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
            for (int v = 1; v <= config.maxSearchHeight && LibFeatureUtils.isPosAir(level, mutablePos); v++) {
                if (v == config.maxSearchHeight) return false;
                mutablePos.move(Direction.DOWN);
            }
            return carvePatch(config, level, random, mutablePos, pContext.chunkGenerator().getMinY());
        } else {
            BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
            for (int v = 1; v <= config.maxSearchHeight && !LibFeatureUtils.isPosAir(level, mutablePos); v++) {
                if (v == config.maxSearchHeight) return false;
                mutablePos.move(Direction.UP);
            }
            return carvePatch(config, level, random, mutablePos, pContext.chunkGenerator().getMinY());
        }
    }

    private boolean carvePatch(Config config, WorldGenLevel level, RandomSource random, BlockPos.MutableBlockPos mutablePos, int minY) {
        int radiusSqr = config.radius * config.radius;
        int ox = mutablePos.getX();
        int oy = mutablePos.getY();
        int oz = mutablePos.getZ();
        HashSet<BlockPos> air = new HashSet<>();
        HashSet<BlockPos> ice = new HashSet<>();
        for (int y = 1; y <= config.maxDepth; y++) {
            int ay = oy - y;
            if (ay < minY) break;
            mutablePos.setY(ay);
            for (int x = -config.radius; x <= config.radius; x++) {
                int ax = ox + x;
                int radiusSqrSubXSqr = radiusSqr - x * x;
                mutablePos.setX(ax);
                for (int z = -config.radius; z <= config.radius; z++) {
                    if (z * z <= radiusSqrSubXSqr && level.isStateAtPosition(mutablePos.setZ(oz + z), PREDICATE)) {
                        if (y <= 3) {
                            air.add(mutablePos.immutable());
                        } else {
                            ice.add(mutablePos.immutable());
                        }
                    }
                }
            }
        }
        if ((air.size() + ice.size()) / (config.maxDepth * config.radius * config.radius * Mth.PI) > config.successRatio) {
            BlockState airState = Blocks.AIR.defaultBlockState();
            air.forEach(blockPos -> level.setBlock(blockPos, airState, 3));
            ice.forEach(blockPos -> level.setBlock(blockPos, config.blockStateProvider.getState(random, blockPos), 3));
            return true;
        }
        return false;
    }

    public record Config(int stepHeight, int radius, int maxDepth, int maxSearchHeight, float successRatio, BlockStateProvider blockStateProvider) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.validate(i -> i >= 0 ? DataResult.success(i) : DataResult.error(() -> "Value must be non-negative: " + i)).lenientOptionalFieldOf("step_height", 3).forGetter(Config::stepHeight),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("radius", 4).forGetter(Config::radius),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_depth", 32).forGetter(Config::maxDepth),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_search_height", 32).forGetter(Config::maxDepth),
                ExtraCodecs.POSITIVE_FLOAT.lenientOptionalFieldOf("success_ratio", 0.5F).forGetter(Config::successRatio),
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Config::blockStateProvider)
        ).apply(instance, Config::new));
    }
}
