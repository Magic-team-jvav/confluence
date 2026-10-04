package org.confluence.mod.common.worldgen.feature;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.function.Supplier;

public class GemstoneCaveFeature extends Feature<GemstoneCaveFeature.Config> {
    private final Supplier<BlockState[]> stonedGems = Suppliers.memoize(() -> new BlockState[]{
            OreBlocks.RUBY_ORE.get().defaultBlockState(),
            OreBlocks.TOPAZ_ORE.get().defaultBlockState(),
            OreBlocks.SAPPHIRE_ORE.get().defaultBlockState(),
            OreBlocks.JADE_ORE.get().defaultBlockState(),
            OreBlocks.AMETHYST_ORE.get().defaultBlockState(),
            Blocks.DIAMOND_ORE.defaultBlockState()
    });
    private final Supplier<BlockState[]> deepslatedGems = Suppliers.memoize(() -> new BlockState[]{
            OreBlocks.DEEPSLATE_RUBY_ORE.get().defaultBlockState(),
            OreBlocks.DEEPSLATE_TOPAZ_ORE.get().defaultBlockState(),
            OreBlocks.DEEPSLATE_SAPPHIRE_ORE.get().defaultBlockState(),
            OreBlocks.DEEPSLATE_JADE_ORE.get().defaultBlockState(),
            OreBlocks.DEEPSLATE_AMETHYST_ORE.get().defaultBlockState(),
            Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState()
    });

    public GemstoneCaveFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        Config config = context.config();
        RandomSource random = context.random();
        WorldGenLevel level = context.level();
        BlockPos blockPos = context.origin();
        float radius = config.radius + 0.5F;
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState deepslate = Blocks.DEEPSLATE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        int index0 = random.nextInt(6);
        int t = index0;
        while (t == index0) t = random.nextInt(6);
        int index1 = t;
        float rotation = Mth.TWO_PI * random.nextFloat();
        int x = Mth.floor(Mth.sin(rotation) * radius);
        int z = Mth.floor(Mth.cos(rotation) * radius);
        LibFeatureUtils.ball(radius + 2, blockPos, state -> determineGems(state, index0), true, level);
        LibFeatureUtils.ball(radius + 2, blockPos, state -> determineGems(state, index1), true, level, 0.5F, random);
        LibFeatureUtils.ball(radius + 2, blockPos, state -> isDeepslate(state) ? deepslate : stone, true, level, 0.7F, random);
        LibFeatureUtils.ball(radius, blockPos, air, true, level);
        LibFeatureUtils.ellipsoid(radius - 1, radius + 1, radius - 1, blockPos.offset(x, 0, z), air, true, level);

        return true;
    }

    private static boolean isDeepslate(BlockState state) {
        return state.is(Blocks.DEEPSLATE) || state.is(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE);
    }

    private BlockState determineGems(BlockState state, int index) {
        if (isDeepslate(state)) {
            return deepslatedGems.get()[index];
        }
        return stonedGems.get()[index];
    }

    public record Config(int radius) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("radius").forGetter(GemstoneCaveFeature.Config::radius)
        ).apply(instance, Config::new));
    }
}
