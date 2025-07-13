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
import org.confluence.lib.util.FeatureUtils;
import org.confluence.mod.common.init.block.OreBlocks;

import java.util.function.Supplier;

public class GemstoneCaveFeature extends Feature<GemstoneCaveFeature.Config> {
    private final Supplier<BlockState[]> gemstones = Suppliers.memoize(() -> new BlockState[]{
            OreBlocks.RUBY_ORE.get().defaultBlockState(),
            OreBlocks.TOPAZ_ORE.get().defaultBlockState(),
            OreBlocks.SAPPHIRE_ORE.get().defaultBlockState(),
            OreBlocks.JADE_ORE.get().defaultBlockState(),
            OreBlocks.AMETHYST_ORE.get().defaultBlockState(),
            Blocks.DIAMOND_ORE.defaultBlockState()
    });

    public GemstoneCaveFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        RandomSource random = pContext.random();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        float radius = config.radius + 0.5F;
        BlockState stone = Blocks.STONE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        int index0 = random.nextInt(6);
        int index1 = index0;
        while (index1 == index0) {
            index1 = random.nextInt(6);
        }
        float rotation = Mth.TWO_PI * random.nextFloat();
        int x = Mth.floor(Mth.sin(rotation) * radius);
        int z = Mth.floor(Mth.cos(rotation) * radius);

        BlockState[] states = gemstones.get();
        FeatureUtils.ball(radius + 2, blockPos, states[index0], true, level);
        FeatureUtils.ball(radius + 2, blockPos, states[index1], true, level, 0.5F, random);
        FeatureUtils.ball(radius + 2, blockPos, stone, true, level, 0.7F, random);
        FeatureUtils.ball(radius, blockPos, air, true, level);
        FeatureUtils.ellipsoid(radius - 1, radius + 1, radius - 1, blockPos.offset(x, 0, z), air, true, level);

        return true;
    }

    public record Config(int radius) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("radius").forGetter(GemstoneCaveFeature.Config::radius)
        ).apply(instance, Config::new));
    }
}
