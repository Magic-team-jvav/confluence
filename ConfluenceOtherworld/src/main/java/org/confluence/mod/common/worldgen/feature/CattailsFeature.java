package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.lib.util.LibCodecUtils;
import org.confluence.lib.util.LibUtils;

public class CattailsFeature extends Feature<CattailsFeature.Config> {
    public CattailsFeature(Codec<Config> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> context) {
        RandomSource random = context.random();
        Config config = context.config();
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        BlockState head = config.cattailsHead.getState(random, origin);
        BlockState body = config.cattailsBody.getState(random, origin);
        int minY = level.getMinBuildHeight();
        int maxY = level.getMaxBuildHeight();

        int placed = 0;
        BlockPos.MutableBlockPos bottomPos = new BlockPos.MutableBlockPos();

        Direction dir = Direction.WEST;
        Direction.Axis axis = dir.getAxis();
        for (int i = 0, lx = dir.getStepX(), lz = dir.getStepZ(), x = 0, z = 0, s = 1; i < config.maxCheck; i++) {
            place:
            {
                if (LibUtils.checkChance(config.chance, random)) break place;
                BlockState state = level.getBlockState(bottomPos.setWithOffset(origin, x, 0, z));
                boolean isStartInAir = state.isAir();
                if (!isStartInAir && !state.is(Blocks.WATER)) break place;

                int length = 1;
                BlockPos.MutableBlockPos abovePos = new BlockPos.MutableBlockPos(bottomPos.getX(), bottomPos.getY(), bottomPos.getZ());
                BlockState lastState;
                boolean condition;
                if (isStartInAir) {
                    do {
                        lastState = state;
                        state = level.getBlockState(bottomPos.move(0, -1, 0));
                        length++;
                    } while (length <= config.maxLength && bottomPos.getY() > minY && (state.isAir() || state.is(Blocks.WATER)));
                    condition = lastState.is(Blocks.WATER);
                    bottomPos.move(0, 1, 0);
                    if (length == config.maxLength && (!condition || !body.canSurvive(level, bottomPos))) break place;
                    abovePos.move(0, config.maxLength - length, 0);
                } else {
                    do {
                        lastState = state;
                        state = level.getBlockState(bottomPos.move(0, -1, 0));
                        length++;
                    } while (length < config.maxLength && bottomPos.getY() > minY && state.is(Blocks.WATER));
                    bottomPos.move(0, 1, 0);
                    if (length == config.maxLength || !lastState.is(Blocks.WATER) || !body.canSurvive(level, bottomPos)) break place;
                    do {
                        state = level.getBlockState(abovePos.move(0, 1, 0));
                        length++;
                    } while (length <= config.maxLength && abovePos.getY() < maxY && (state.isAir() || state.is(Blocks.WATER)));
                    condition = state.isAir();
                }
                if (condition && body.canSurvive(level, bottomPos)) {
                    for (int y = bottomPos.getY(); y < abovePos.getY(); y++) {
                        level.setBlock(bottomPos.setY(y), body.setValue(BlockStateProperties.WATERLOGGED, level.getBlockState(bottomPos).is(Blocks.WATER)), Block.UPDATE_ALL);
                    }
                    level.setBlock(abovePos, head.setValue(BlockStateProperties.WATERLOGGED, level.getBlockState(abovePos).is(Blocks.WATER)), Block.UPDATE_ALL);
                    placed++;
                }
            }

            if ((x += dir.getStepX()) == lx && (z += dir.getStepZ()) == lz) { // 螺旋遍历
                dir = dir.getClockWise();
                lx += dir.getStepX() * s;
                lz += dir.getStepZ() * s;
                if (dir.getAxis() == axis) {
                    s += 2;
                }
            }
        }
        return placed > 0;
    }

    public record Config(BlockStateProvider cattailsHead, BlockStateProvider cattailsBody, int radius, float chance, int maxLength, int maxCheck) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("cattails_head").forGetter(Config::cattailsHead),
                BlockStateProvider.CODEC.fieldOf("cattails_body").forGetter(Config::cattailsBody),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").forGetter(Config::radius),
                LibCodecUtils.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter(Config::chance),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_length").forGetter(Config::maxLength),
                ExtraCodecs.POSITIVE_INT.fieldOf("max_check").forGetter(Config::maxCheck)
        ).apply(instance, Config::new));
    }
}
