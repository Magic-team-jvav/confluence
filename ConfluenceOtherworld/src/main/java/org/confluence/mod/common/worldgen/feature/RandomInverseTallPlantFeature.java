package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.common.block.natural.InverseTallPlantBlock;

public class RandomInverseTallPlantFeature extends Feature<RandomInverseTallPlantFeature.Config> {
    public RandomInverseTallPlantFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        BlockState blockState = config.block.getState(random, basePos);
        BlockState blockLower = blockState.trySetValue(InverseTallPlantBlock.HALF, DoubleBlockHalf.LOWER);
        BlockState blockUpper = blockState.trySetValue(InverseTallPlantBlock.HALF, DoubleBlockHalf.UPPER);
        int tries = config.tries;
        int radius = config.radius;

        boolean placed = false;

        for (int i = 0; i < tries; i++) {
            placed = placePlant(basePos, radius, blockLower, blockUpper, level, random) || placed;
        }

        return placed;
    }

    public record Config(int tries, int radius, BlockStateProvider block) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("tries").forGetter(Config::tries),
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Config::block)
        ).apply(instance, Config::new));
    }

    private boolean placePlant(BlockPos blockPos, int radius, BlockState blockLower, BlockState blockUpper, WorldGenLevel level, RandomSource random) {
        int radius_2 = radius * radius;
        while (true) {
            int x = random.nextInt(-radius, radius + 1);
            int x_2 = x * x;
            int y = random.nextInt(-radius, radius + 1);
            int xy_2 = y * y + x_2;
            if (xy_2 >= radius_2) continue;
            int z = random.nextInt(-radius, radius + 1);
            int xyz_2 = z * z + xy_2;
            if (xyz_2 >= radius_2) continue;
            BlockPos newPos = blockPos.offset(x, y, z);
            if (blockLower.canSurvive(level, newPos)) {
                level.setBlock(newPos, blockLower, 3);
                level.setBlock(newPos.below(), blockUpper, 3);
                return true;
            }
            return false;
        }
    }
}
