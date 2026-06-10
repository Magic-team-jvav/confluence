package org.confluence.mod.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static org.confluence.lib.util.LibFeatureUtils.rectangular;
import static org.confluence.lib.util.LibFeatureUtils.rectangularCheck;

public class MushroomTreeFeature extends Feature<MushroomTreeFeature.Config> {
    public MushroomTreeFeature(Codec<Config> pCodec) {
        super(pCodec);
    }

    @Override
    public boolean place(FeaturePlaceContext<Config> pContext) {
        RandomSource random = pContext.random();
        Config config = pContext.config();
        WorldGenLevel level = pContext.level();
        BlockPos baseBlockPos = pContext.origin();
        BlockPos checkPos;
        BlockState stemBlockState = config.stem().getState(random, baseBlockPos);
        BlockState pileusBlockState = config.pileus().getState(random, baseBlockPos);
        BlockState indusiumBlockState = config.indusium().getState(random, baseBlockPos);
        int height = config.height + random.nextInt(config.height_more + 1) - 1;
        int width = random.nextInt(1, 3);
        boolean check0 = rectangularCheck(baseBlockPos, baseBlockPos.offset(0, height, 0), level);
        boolean check1 = rectangularCheck(baseBlockPos.offset(width, height + 1, width), baseBlockPos.offset(-width, height + 2, -width), level);
        boolean check2 = rectangularCheck(baseBlockPos.offset(width - 1, height + 3, width), baseBlockPos.offset(1 - width, height + 3, -width), level);
        boolean check3 = rectangularCheck(baseBlockPos.offset(width, height + 3, width - 1), baseBlockPos.offset(-width, height + 3, 1 - width), level);

        boolean placed = check0 && check1 && check2 && check3;

        if (placed) {
            rectangular(baseBlockPos, baseBlockPos.offset(0, height, 0), stemBlockState, level, true);
            rectangular(baseBlockPos.offset(width, height + 1, width), baseBlockPos.offset(-width, height + 2, -width), pileusBlockState, level, true);
            rectangular(baseBlockPos.offset(width - 1, height + 3, width), baseBlockPos.offset(1 - width, height + 3, -width), pileusBlockState, level, true);
            rectangular(baseBlockPos.offset(width, height + 3, width - 1), baseBlockPos.offset(-width, height + 3, 1 - width), pileusBlockState, level, true);
            for (int x = -width; x <= width; x++) {
                for (int y = 0; y < 3; y++) {
                    for (int z = -width; z <= width; z++) {
                        checkPos = baseBlockPos.offset(x, height + 1 + y, z);
                        if (level.getBlockState(checkPos).is(pileusBlockState.getBlock())) {
                            if (level.getBlockState(checkPos.offset(0, 1, 0)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.UP, true), 3);
                            }
                            if (level.getBlockState(checkPos.offset(0, -1, 0)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.DOWN, true), 3);
                            }
                            if (level.getBlockState(checkPos.offset(1, 0, 0)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.EAST, true), 3);
                            }
                            if (level.getBlockState(checkPos.offset(-1, 0, 0)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.WEST, true), 3);
                            }
                            if (level.getBlockState(checkPos.offset(0, 0, 1)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.SOUTH, true), 3);
                            }
                            if (level.getBlockState(checkPos.offset(0, 0, -1)).is(pileusBlockState.getBlock())) {
                                level.setBlock(checkPos, level.getBlockState(checkPos).trySetValue(PipeBlock.NORTH, true), 3);
                            }
                        }
                    }
                }
            }
            if (level.getBlockState(baseBlockPos.offset(1, height, 0)).canBeReplaced()) level.setBlock(baseBlockPos.offset(1, height, 0), indusiumBlockState.trySetValue(HORIZONTAL_FACING, Direction.EAST), 3);
            if (level.getBlockState(baseBlockPos.offset(-1, height, 0)).canBeReplaced()) level.setBlock(baseBlockPos.offset(-1, height, 0), indusiumBlockState.trySetValue(HORIZONTAL_FACING, Direction.WEST), 3);
            if (level.getBlockState(baseBlockPos.offset(0, height, 1)).canBeReplaced()) level.setBlock(baseBlockPos.offset(0, height, 1), indusiumBlockState.trySetValue(HORIZONTAL_FACING, Direction.SOUTH), 3);
            if (level.getBlockState(baseBlockPos.offset(0, height, -1)).canBeReplaced()) level.setBlock(baseBlockPos.offset(0, height, -1), indusiumBlockState.trySetValue(HORIZONTAL_FACING, Direction.NORTH), 3);
            return true;
        }
        return false;
    }

    public record Config(
            BlockStateProvider stem,
            BlockStateProvider pileus,
            BlockStateProvider indusium,
            int height,
            int height_more
    ) implements FeatureConfiguration {
        public static final Codec<Config> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockStateProvider.CODEC.fieldOf("stem_block").forGetter(Config::stem),
                BlockStateProvider.CODEC.fieldOf("pileus_block").forGetter(Config::pileus),
                BlockStateProvider.CODEC.fieldOf("indusium_block").forGetter(Config::indusium),
                Codec.INT.fieldOf("height").forGetter(MushroomTreeFeature.Config::height),
                Codec.INT.fieldOf("height_more").forGetter(MushroomTreeFeature.Config::height)
        ).apply(instance, Config::new));
    }
}
