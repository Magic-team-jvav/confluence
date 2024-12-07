package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.mixed.IWorldGenRegion;

import java.util.HashSet;
import java.util.function.Predicate;

public class ThinIcePatchFeature extends Feature<ThinIcePatchFeature.Config> {
    public static final Predicate<BlockState> PREDICATE = blockState -> !blockState.isAir() && !blockState.is(BlockTags.FEATURES_CANNOT_REPLACE);

    public ThinIcePatchFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        if (ModFeatures.isPosAir(level, blockPos)) {
            BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
            for (int v = 1; v <= config.maxSearchHeight && ModFeatures.isPosAir(level, mutablePos); v++) {
                if (v == config.maxSearchHeight) return false;
                mutablePos.move(Direction.DOWN);
            }
            return carvePatch(config, level, mutablePos, pContext.chunkGenerator().getMinY());
        } else {
            BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
            for (int v = 1; v <= config.maxSearchHeight && !ModFeatures.isPosAir(level, mutablePos); v++) {
                if (v == config.maxSearchHeight) return false;
                mutablePos.move(Direction.UP);
            }
            return carvePatch(config, level, mutablePos, pContext.chunkGenerator().getMinY());
        }
    }

    private boolean carvePatch(Config config, WorldGenLevel level, BlockPos.MutableBlockPos mutablePos, int minY) {
        IWorldGenRegion worldGenRegion = (IWorldGenRegion) level;
        int radiusSqr = config.radius * config.radius;
        int ox = mutablePos.getX();
        int oy = mutablePos.getY();
        int oz = mutablePos.getZ();
        HashSet<BlockPos> air = new HashSet<>();
        HashSet<BlockPos> ice = new HashSet<>();
        for (int y = 1; y <= config.maxDepth; y++) {
            int ay = oy - y;
            if (ay < minY) break;
            for (int x = -config.radius; x <= config.radius; x++) {
                int ax = ox + x;
                int radiusSqrSubXSqr = radiusSqr - x * x;
                for (int z = -config.radius; z <= config.radius; z++) {
                    if (z * z <= radiusSqrSubXSqr && level.isStateAtPosition(mutablePos.set(ax, ay, oz + z), PREDICATE)) {
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
            BlockState iceState = NatureBlocks.THIN_ICE_BLOCK.get().defaultBlockState();
            air.forEach(blockPos -> worldGenRegion.confluence$setBlock(blockPos, airState, 2));
            ice.forEach(blockPos -> worldGenRegion.confluence$setBlock(blockPos, iceState, 2));
            return true;
        }
        return false;
    }

    public record Config(int stepHeight, int radius, int maxDepth, int maxSearchHeight, float successRatio) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("step_height").orElse(3).forGetter(Config::stepHeight),
                ExtraCodecs.POSITIVE_INT.fieldOf("radius").orElse(4).forGetter(Config::radius),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_depth").orElse(32).forGetter(Config::maxDepth),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_search_height").orElse(32).forGetter(Config::maxDepth),
                ExtraCodecs.POSITIVE_FLOAT.fieldOf("success_ratio").orElse(0.5F).forGetter(Config::successRatio)
        ).apply(instance, Config::new));
    }
}
