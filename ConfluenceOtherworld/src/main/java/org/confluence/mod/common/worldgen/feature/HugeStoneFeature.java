package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.confluence.lib.util.VectorUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;

import java.util.List;

public class HugeStoneFeature extends Feature<HugeStoneFeature.Config> {
    public HugeStoneFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos basePos = pContext.origin();
        long seed = random.nextLong();
        NormalNoise normalNoise = NormalNoise.create(RandomSource.create(seed), new NormalNoise.NoiseParameters(-5, 1.0, 1.0, 1.0, 1.0));
        RandomSource worldgenRandom = new WorldgenRandom(RandomSource.create(seed));
        BlockState stone = config.stone.getState(random, basePos);
        boolean useTecture = (config.stoneTexture != null);
        BlockState stoneTexture;
        if (useTecture) {
            stoneTexture = config.stoneTexture.getState(random, basePos);
        } else stoneTexture = stone;

        int radius = config.radius + random.nextInt(config.radiusMore + 1);
        float noiseScale = config.noiseScale;
        List<Vector3d> posList = VectorUtils.ballPos(radius, basePos, 0.006F, worldgenRandom);
        List<BlockPos> placePos = VectorUtils.getBlocksInConvexHull(posList);
        if (placePos.isEmpty()) return false;

        placePos.forEach(p -> {
            if (useTecture) {
                double noiseValue = normalNoise.getValue(p.getX() * noiseScale, p.getY() * noiseScale, p.getZ() * noiseScale);
                level.setBlock(p, ((noiseValue < 0.075) && (noiseValue > -0.075)) ? stoneTexture : stone, 3);
            } else level.setBlock(p, stone, 3);
        });

        return true;
    }

    public record Config(int radius,
                         int radiusMore,
                         BlockStateProvider stone,
                         @Nullable BlockStateProvider stoneTexture,
                         float noiseScale
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("radius").forGetter(Config::radius),
                Codec.INT.fieldOf("radius_more").forGetter(Config::radiusMore),
                BlockStateProvider.CODEC.fieldOf("stone").forGetter(Config::stone),
                BlockStateProvider.CODEC.optionalFieldOf("stone_texture", null).forGetter(Config::stoneTexture),
                Codec.FLOAT.optionalFieldOf("noise_scale", 1.0F).forGetter(Config::noiseScale)
        ).apply(instance, Config::new));
    }
}

