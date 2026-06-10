package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class BlockPostFeature extends Feature<BlockPostFeature.Config> {
    public BlockPostFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        final RandomSource random = context.random();
        final Config config = context.config();
        final WorldGenLevel level = context.level();
        final BlockPos baseBlockPos = context.origin();
        final BlockState block = config.block.getState(random, baseBlockPos);
        final int length = config.length + random.nextInt(config.extraLength + 1);
        final boolean toGround = config.toEnd;
        final boolean replace = config.replace;
        final Direction direction = config.direction;

        if (!replace && !level.getBlockState(baseBlockPos).canBeReplaced()) return false;

        BlockPos placePos = baseBlockPos;
        if (toGround && (direction.getAxis() == Direction.Axis.Y) && !replace) {
            while (level.getBlockState(placePos).canBeReplaced()) {
                level.setBlock(placePos, block.trySetValue(WATERLOGGED, level.getFluidState(placePos).is(FluidTags.WATER)), 3);
                placePos = placePos.relative(direction);
            }
            return true;
        }
        for (int i = 0; i < length; i++) {
            if (replace || level.getBlockState(placePos).canBeReplaced()) {
                level.setBlock(placePos, block.trySetValue(WATERLOGGED, level.getFluidState(placePos).is(FluidTags.WATER)), 3);
                placePos = placePos.relative(direction);
            }
        }

        return true;
    }

    public record Config(
            BlockStateProvider block,
            boolean toEnd,
            int length,
            int extraLength,
            Direction direction,
            boolean replace
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("block").forGetter(Config::block),
                Codec.BOOL.fieldOf("to_end").forGetter(BlockPostFeature.Config::toEnd),
                Codec.INT.fieldOf("length").forGetter(BlockPostFeature.Config::length),
                Codec.INT.fieldOf("extra_length").forGetter(BlockPostFeature.Config::extraLength),
                Direction.CODEC.fieldOf("direction").forGetter(Config::direction),
                Codec.BOOL.fieldOf("replace").forGetter(Config::replace)
        ).apply(instance, Config::new));
    }
}
