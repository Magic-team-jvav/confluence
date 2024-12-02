package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.mod.common.block.functional.AbstractMechanicalBlock;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.util.ModUtils;

public class DartTrapFeature extends Feature<DartTrapFeature.Config> {
    public DartTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        if (!ModFeatures.isPosAir(level, blockPos)) return false;
        BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
        for (int v = 1; v <= config.maxSearchDown && ModFeatures.isPosAir(level, mutablePos); ++v) {
            mutablePos.move(Direction.DOWN);
        }
        if (ModFeatures.isPosSturdy(level, mutablePos, Direction.UP)) {
            BlockPos dartPos = mutablePos.offset(0, 2, 0);
            Direction opposite = null;
            for (Direction direction : ModUtils.HORIZONTAL) {
                BlockPos.MutableBlockPos copy = dartPos.mutable();
                int h;
                for (h = 1; h <= config.maxDistance && ModFeatures.isPosAir(level, copy); ++h) {
                    copy.move(direction);
                }
                if (h >= 4 && !level.isStateAtPosition(copy, blockState -> blockState.isAir() || blockState.getCollisionShape(level, copy).isEmpty())) {
                    dartPos = copy.immutable();
                    opposite = direction.getOpposite();
                    break;
                }
            }
            if (opposite == null) return false;
            BlockPos supportPos = mutablePos.immutable();
            BlockPos platePos = supportPos.above();
            BlockState dartTrap = FunctionalBlocks.DART_TRAP.get().defaultBlockState().setValue(BlockStateProperties.FACING, opposite);
            ModFeatures.safeSetBlock(level, dartPos, dartTrap, ModFeatures.IS_REPLACEABLE);
            ModFeatures.safeSetBlock(level, platePos, ModFeatures.getPressurePlate(level, supportPos), ModFeatures.IS_REPLACEABLE);
            AbstractMechanicalBlock.Entity dart = ModFeatures.getMechanicalEntity(level, dartPos);
            AbstractMechanicalBlock.Entity plate = ModFeatures.getMechanicalEntity(level, platePos);
            if (dart != null && plate != null) dart.connectTo(0x00FF00, platePos, plate);
            return true;
        }
        return false;
    }

    public record Config(int maxDistance, int maxSearchDown) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.fieldOf("max_distance").orElse(64).forGetter(Config::maxDistance),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_search_down").orElse(64).forGetter(Config::maxSearchDown)
        ).apply(instance, Config::new));
    }
}
