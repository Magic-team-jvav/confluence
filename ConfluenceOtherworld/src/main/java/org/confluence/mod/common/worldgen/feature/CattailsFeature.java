package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.confluence.mod.common.block.natural.CattailBlock;

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
        BlockState cattailState = config.cattail.getState(random, origin);
        if (!(cattailState.getBlock() instanceof CattailBlock)) return false;
        int placed = 0;
        BlockPos.MutableBlockPos currentPos = new BlockPos.MutableBlockPos();
        Direction dir = Direction.WEST;
        Direction.Axis axis = dir.getAxis();
        int lx = dir.getStepX(), lz = dir.getStepZ(), x = 0, z = 0, s = 1;
        for (int i = 0; i < config.maxCheck; i++) {
            if (random.nextFloat() > config.chance) continue;
            currentPos.setWithOffset(origin, x, 0, z);
            if (level.getFluidState(currentPos).is(FluidTags.WATER)) {
                while (level.getFluidState(currentPos).is(FluidTags.WATER) && currentPos.getY() > level.getMinBuildHeight()) {
                    currentPos.move(Direction.DOWN);
                }
                BlockPos basePos = currentPos.above();
                if (cattailState.canSurvive(level, basePos)) {
                    BlockPos.MutableBlockPos buildPos = basePos.mutable();
                    int plantMaxAirHeight = random.nextInt(1, 4);
                    while (level.getFluidState(buildPos).is(FluidTags.WATER)) {
                        level.setBlock(buildPos, cattailState
                                .setValue(CattailBlock.PART, CattailBlock.CattailPart.SUBMERGED)
                                .setValue(CattailBlock.WATERLOGGED, true)
                                .setValue(CattailBlock.AIR_HEIGHT, 0)
                                .setValue(CattailBlock.MAX_AIR_HEIGHT, plantMaxAirHeight), 2);
                        buildPos.move(Direction.UP);
                    }
                    if (level.isEmptyBlock(buildPos)) {
                        boolean isOnlyOne = (plantMaxAirHeight == 1);
                        level.setBlock(buildPos, cattailState
                                .setValue(CattailBlock.PART, isOnlyOne ? CattailBlock.CattailPart.TOP : CattailBlock.CattailPart.TRANSITION)
                                .setValue(CattailBlock.WATERLOGGED, false)
                                .setValue(CattailBlock.AIR_HEIGHT, 1)
                                .setValue(CattailBlock.MAX_AIR_HEIGHT, plantMaxAirHeight), 2);
                        if (!isOnlyOne) {
                            buildPos.move(Direction.UP);
                            for (int h = 2; h <= plantMaxAirHeight; h++) {
                                if (level.isEmptyBlock(buildPos)) {
                                    boolean isTop = (h == plantMaxAirHeight);
                                    level.setBlock(buildPos, cattailState
                                            .setValue(CattailBlock.PART, isTop ? CattailBlock.CattailPart.TOP : CattailBlock.CattailPart.STEM)
                                            .setValue(CattailBlock.WATERLOGGED, false)
                                            .setValue(CattailBlock.AIR_HEIGHT, h)
                                            .setValue(CattailBlock.MAX_AIR_HEIGHT, plantMaxAirHeight), 2);
                                    if (isTop) break;
                                    buildPos.move(Direction.UP);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                    placed++;
                }
            }
            if ((x += dir.getStepX()) == lx && (z += dir.getStepZ()) == lz) {
                dir = dir.getClockWise();
                lx += dir.getStepX() * s;
                lz += dir.getStepZ() * s;
                if (dir.getAxis() == axis) s += 2;
            }
        }
        return placed > 0;
    }

    public record Config(
            BlockStateProvider cattail,
            int radius,
            float chance,
            int maxCheck
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("cattail").forGetter(Config::cattail),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("radius").forGetter(Config::radius),
                Codec.FLOAT.fieldOf("chance").forGetter(Config::chance),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("max_check").forGetter(Config::maxCheck)
        ).apply(instance, Config::new));
    }
}
