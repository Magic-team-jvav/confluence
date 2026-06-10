package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Column;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibFeatureUtils;
import org.confluence.mod.common.block.functional.network.INetworkEntity;
import org.confluence.mod.common.init.ModFeatures;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class FallingSandTrapFeature extends Feature<FallingSandTrapFeature.Config> {
    public FallingSandTrapFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(@NotNull FeaturePlaceContext<Config> context) {
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        if (!LibFeatureUtils.isPosAir(level, origin)) return false;
        Optional<Column> optionalColumn = Column.scan(level, origin, config.maxDistanceTo, BlockBehaviour.BlockStateBase::isAir, ModFeatures.IS_BASE_STONE);
        if (optionalColumn.isPresent() && optionalColumn.get() instanceof Column.Range range && range.height() >= config.minDistanceTo) {
            int halfHeight = range.height() / 2;
            BlockPos supportPos = origin.atY(range.floor());
            if (!LibFeatureUtils.isPosSturdy(level, supportPos, Direction.UP)) return false;

            int radius = config.radius;
            int ceiling = range.ceiling();

            int width = radius * 2 + 1;
            BlockState fragile = FunctionalBlocks.MECHANICAL_FRAGILE_SANDSTONE.get().defaultBlockState();
            BlockPos.MutableBlockPos mutable = origin.mutable().setY(ceiling).move(-radius, 0, -radius);
            BlockPos supportingPos = mutable.immutable();
            level.setBlock(supportingPos, fragile, Block.UPDATE_ALL);
            INetworkEntity fragileEntity = ModFeatures.getNetworkEntity(level, supportingPos);
            if (fragileEntity == null) return false;
            BlockPos.MutableBlockPos mutable1 = mutable.immutable().mutable();
            for (int z = 1; z < width; z++) {
                level.setBlock(mutable1.move(0, 0, 1), fragile, Block.UPDATE_ALL);
                INetworkEntity fragileEntity1 = ModFeatures.getNetworkEntity(level, mutable1);
                if (fragileEntity1 != null) {
                    fragileEntity1.connectTo(0xFFFF00, supportingPos, fragileEntity);
                }
                fillAir(level, mutable1, halfHeight);
            }

            for (int x = 1; x < width; x++) {
                level.setBlock(mutable.move(1, 0, 0), fragile, Block.UPDATE_ALL);
                INetworkEntity fragileEntity1 = ModFeatures.getNetworkEntity(level, mutable);
                if (fragileEntity1 != null) {
                    fragileEntity1.connectTo(0xFFFF00, supportingPos, fragileEntity);
                }
                mutable1 = mutable.immutable().mutable();
                for (int z = 1; z < width; z++) {
                    level.setBlock(mutable1.move(0, 0, 1), fragile, Block.UPDATE_ALL);
                    fragileEntity1 = ModFeatures.getNetworkEntity(level, mutable1);
                    if (fragileEntity1 != null) {
                        fragileEntity1.connectTo(0xFFFF00, supportingPos, fragileEntity);
                    }
                    fillAir(level, mutable1, halfHeight);
                }
            }

            BlockState sand = Blocks.SAND.defaultBlockState();
            for (BlockPos pos : BlockPos.betweenClosed(origin.atY(0).offset(-radius, ceiling + 1, -radius), origin.atY(0).offset(radius, ceiling + config.height, radius))) {
                LibFeatureUtils.safeSetBlock(level, pos, sand, ModFeatures.IS_REPLACEABLE);
            }

            boolean isDeepslate = level.isStateAtPosition(supportPos, blockState -> blockState.is(Blocks.DEEPSLATE));
            BlockState pressureBlock = (isDeepslate
                    ? FunctionalBlocks.DEEPSLATE_PRESSURE_BLOCK
                    : FunctionalBlocks.STONE_PRESSURE_BLOCK).get().defaultBlockState();
            level.setBlock(supportPos, pressureBlock, Block.UPDATE_ALL);
            INetworkEntity plateEntity = ModFeatures.getNetworkEntity(level, supportPos);
            if (plateEntity != null) {
                fragileEntity.connectTo(0xFFFF00, supportPos, plateEntity);
                return true;
            }
        }
        return false;
    }

    private static void fillAir(WorldGenLevel level, BlockPos pos, int height) {
        for (int i = 1; i < height; i++) {
            level.setBlock(pos.below(i), Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    public record Config(BlockStateProvider fallingBlock, int radius, int height, int minDistanceTo,
                         int maxDistanceTo) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.lenientOptionalFieldOf("falling_block", BlockStateProvider.simple(Blocks.SAND)).forGetter(Config::fallingBlock),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("radius", 4).forGetter(Config::radius),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("height", 4).forGetter(Config::height),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("min_distance_to", 4).forGetter(Config::minDistanceTo),
                ExtraCodecs.POSITIVE_INT.lenientOptionalFieldOf("max_distance_to", 16).forGetter(Config::maxDistanceTo)
        ).apply(instance, Config::new));
    }
}
