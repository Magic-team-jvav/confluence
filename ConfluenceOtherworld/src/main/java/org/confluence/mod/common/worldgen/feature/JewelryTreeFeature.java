package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.phys.AABB;

public class JewelryTreeFeature extends Feature<JewelryTreeFeature.Config> {
    public JewelryTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        BlockPos origin = pContext.origin();
        WorldGenLevel level = pContext.level();
        // E: extraRadius
        // T: trunk
        // B: base
        // #: another
        // EEETEEE
        // EEETEEE
        // EEETEEE
        // ###B###
        AABB area = new AABB(origin).inflate(config.extraRadius, 0.0, config.extraRadius).expandTowards(0.0, config.minHeight, 0.0);
        if (level.getBlockStates(area).allMatch(BlockBehaviour.BlockStateBase::isAir)) {

        }
        return false;
    }

    public record Config(BlockStateProvider base, BlockStateProvider trunk, BlockStateProvider brunch, int extraRadius, int minHeight) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("base").forGetter(Config::base),
                BlockStateProvider.CODEC.fieldOf("trunk").forGetter(Config::trunk),
                BlockStateProvider.CODEC.fieldOf("brunch").forGetter(Config::brunch),
                ExtraCodecs.POSITIVE_INT.fieldOf("extraRadius").forGetter(Config::extraRadius),
                ExtraCodecs.POSITIVE_INT.fieldOf("minHeight").forGetter(Config::minHeight)
        ).apply(instance, Config::new));
    }
}
