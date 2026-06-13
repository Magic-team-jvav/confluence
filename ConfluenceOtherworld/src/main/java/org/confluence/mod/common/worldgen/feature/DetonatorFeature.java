package org.confluence.mod.common.worldgen.feature;

import PortLib.extensions.com.mojang.serialization.Codec.PortCodecExtension;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class DetonatorFeature extends Feature<DetonatorFeature.Config> {
    public DetonatorFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        int mx = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(context.origin().getX()), 8);
        int mz = SectionPos.sectionToBlockCoord(SectionPos.blockToSectionCoord(context.origin().getZ()), 8);
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(mx, context.origin().getY(), mz);
        WorldGenLevel level = context.level();
        if (LibFeatureUtils.isPosAir(level, pos)) {
            Config config = context.config();
            for (int v = 1; v <= config.maxSearchDown && LibFeatureUtils.isPosAir(level, pos); ++v) {
                pos.move(0, -1, 0);
            }
            RandomSource random = context.random();
            if (!LibFeatureUtils.isPosLiquid(level, pos) &&
                    config.oreFeature.value().place(level, context.chunkGenerator(), random, pos) &&
                    LibFeatureUtils.safeSetBlock(level, pos, FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.get().defaultBlockState(), ModFeatures.IS_REPLACEABLE)
            ) {
                BlockPos.MutableBlockPos offset = new BlockPos.MutableBlockPos();
                for (int i = 0; i < config.tryTimes; i++) {
                    offset.setWithOffset(pos, Mth.randomBetweenInclusive(random, -7, 7), 0, Mth.randomBetweenInclusive(random, -7, 7));
                    if (LibFeatureUtils.isPosAir(level, offset) && LibFeatureUtils.isPosSturdy(level, offset.offset(0, -1, 0), Direction.UP)) {
                        level.setBlock(offset, FunctionalBlocks.DETONATOR.get().defaultBlockState(), Block.UPDATE_ALL);
                        INetworkEntity a = ModFeatures.getNetworkEntity(level, pos);
                        INetworkEntity b = ModFeatures.getNetworkEntity(level, offset);
                        if (a != null && b != null) {
                            a.connectTo(0x39C5BB, offset, b);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public record Config(Holder<ConfiguredFeature<?, ?>> oreFeature, int maxSearchDown,
                         int tryTimes) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ConfiguredFeature.CODEC.fieldOf("ore_feature").forGetter(Config::oreFeature),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "max_search_down", 32).forGetter(Config::maxSearchDown),
                PortCodecExtension.lenientOptionalFieldOf(ExtraCodecs.POSITIVE_INT, "try_times", 4).forGetter(Config::tryTimes)
        ).apply(instance, Config::new));
    }
}
