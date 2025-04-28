package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import static org.confluence.lib.util.FeatureUtils.ball;
import static org.confluence.lib.util.FeatureUtils.ellipsoid;

public class AmethystGeodeFeature extends Feature<AmethystGeodeFeature.Config> {
    public AmethystGeodeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos centerPos = pContext.origin();
        BlockState outBlockState = config.out().getState(pContext.random(), centerPos);
        BlockState middleBlockState = config.middle().getState(pContext.random(), centerPos);
        BlockState inBlockState = config.in().getState(pContext.random(), centerPos);
        BlockState inRandomBlockState = config.in_random().getState(pContext.random(), centerPos);
        BlockState breakBlockState = Blocks.AIR.defaultBlockState();
        float r = pContext.random().nextFloat() * 2 * Mth.PI;

        ball(7.5, centerPos, outBlockState, true, level);
        ball(6.5, centerPos, middleBlockState, true, level);
        ball(5.5, centerPos, inBlockState, true, level);
        ball(5.5, centerPos, inRandomBlockState, true, level, 0.05F, pContext.random());
        ball(4.5, centerPos, breakBlockState, true, level);
        ellipsoid(3.5, 6.5, 3.5, centerPos.offset((int) (3 * Mth.sin(r)), 0, (int) (3 * Mth.cos(r))), breakBlockState, true, level);
        return true;
    }

    public record Config(BlockStateProvider out,BlockStateProvider middle,BlockStateProvider in,BlockStateProvider in_random) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("out").forGetter(Config::out),
                BlockStateProvider.CODEC.fieldOf("middle").forGetter(Config::middle),
                BlockStateProvider.CODEC.fieldOf("in").forGetter(Config::in),
                BlockStateProvider.CODEC.fieldOf("in_random").forGetter(Config::in_random)
        ).apply(instance, Config::new));
    }
}
