package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;

import java.util.Optional;

public class BoulderTrapFeature extends Feature<BoulderTrapFeature.Config> {
    public BoulderTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        if (ModFeatures.isPosAir(level, blockPos)) {
            Optional<Column> optionalColumn = Column.scan(level, blockPos, config.maxBoulderHeight, BlockBehaviour.BlockStateBase::isAir, ModFeatures.IS_BASE_STONE);
            if (optionalColumn.isPresent() && optionalColumn.get() instanceof Column.Range range && range.height() > 4) {
                BlockPos supportPos = blockPos.atY(range.floor());
                if (ModFeatures.isPosSturdy(level, supportPos, Direction.UP)) {
                    BlockPos boulderPos = blockPos.atY(range.ceiling());
                    BlockPos platePos = blockPos.atY(range.floor() + 1);
                    boolean b = ModFeatures.safeSetBlock(level, boulderPos, ModFeatures.getBoulder(level, pContext.random(), config.boulder), ModFeatures.IS_REPLACEABLE);
                    boolean b1 = ModFeatures.safeSetBlock(level, platePos, ModFeatures.getPressurePlate(level, supportPos), ModFeatures.IS_REPLACEABLE);
                    if (b && b1) {
                        INetworkEntity boulder = ModFeatures.getNetworkEntity(level, boulderPos);
                        INetworkEntity plate = ModFeatures.getNetworkEntity(level, platePos);
                        if (boulder != null && plate != null) boulder.connectTo(0xFF0000, platePos, plate);
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public record Config(BlockState boulder, int maxBoulderHeight) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockState.CODEC.fieldOf("boulder").orElseGet(() -> FunctionalBlocks.NORMAL_BOULDER.get().defaultBlockState()).forGetter(Config::boulder),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_boulder_height").orElse(64).forGetter(Config::maxBoulderHeight)
        ).apply(instance, Config::new));
    }
}
