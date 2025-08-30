package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import org.confluence.lib.util.FeatureUtils;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;

public class DartTrapFeature extends Feature<DartTrapFeature.Config> {
    public DartTrapFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos blockPos = pContext.origin();
        if (!FeatureUtils.isPosAir(level, blockPos)) return false;
        BlockPos.MutableBlockPos mutablePos = blockPos.mutable();
        for (int v = 1; v <= config.maxSearchDown && FeatureUtils.isPosAir(level, mutablePos); ++v) {
            mutablePos.move(0, -1, 0);
        }
        if (FeatureUtils.isPosSturdy(level, mutablePos, Direction.UP)) {
            BlockPos dartPos = mutablePos.offset(0, 2, 0);
            for (Direction direction : LibUtils.HORIZONTAL) {
                BlockPos.MutableBlockPos copy = dartPos.mutable();
                int h;
                for (h = 1; h <= config.maxDartDistance && FeatureUtils.isPosAir(level, copy); ++h) {
                    copy.move(direction);
                }
                ChunkPos chunkPos = new ChunkPos(mutablePos);
                if (!level.hasChunk(chunkPos.x, chunkPos.z)) continue;
                if (h >= 4 && !level.isStateAtPosition(copy, blockState -> blockState.isAir() || blockState.getCollisionShape(level, copy).isEmpty())) {
                    BlockState dartTrap = ModFeatures.getDartTrap(level, copy, direction.getOpposite());
                    boolean b = FeatureUtils.safeSetBlock(level, copy, dartTrap, ModFeatures.IS_REPLACEABLE);
                    Tuple<BlockPos, BlockState> pressurePlate = ModFeatures.getPressurePlate(level, mutablePos);
                    BlockPos platePos = pressurePlate.getA();
                    boolean b1 = FeatureUtils.safeSetBlock(level, platePos, pressurePlate.getB(), ModFeatures.IS_REPLACEABLE);
                    if (b && b1) {
                        INetworkEntity dart = ModFeatures.getNetworkEntity(level, copy);
                        INetworkEntity plate = ModFeatures.getNetworkEntity(level, platePos);
                        if (dart != null && plate != null) dart.connectTo(0x00FF00, platePos, plate);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public record Config(int maxDartDistance, int maxSearchDown) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_dart_distance", 24).forGetter(Config::maxDartDistance),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_search_down", 32).forGetter(Config::maxSearchDown)
        ).apply(instance, Config::new));
    }
}
