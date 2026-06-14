package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Set;

import static net.neoforged.neoforge.common.net.minecraftforge.common.CommonHooks.fireCropGrowPost;

public abstract class BaseCropBlock extends CropBlock {
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 7);
    public static final int MAX_AGE = 7;

    public BaseCropBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    protected abstract ItemLike getBaseSeedId();

    @Override
    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }

    public abstract Set<Block> getCanPlaceBlocks();

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return getCanPlaceBlocks().contains(state.getBlock());
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return mayPlaceOn(belowState, level, below);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 3)) return;
        if (level.getRawBrightness(pos, 0) >= 9) {
            int i = getAge(state);
            if (i < getMaxAge()) {
                float f = getGrowthSpeed(state, level, pos);
                BlockState belowState = level.getBlockState(pos.below());
                if (belowState.is(NatureBlocks.RAIN_CLOUD_BLOCK.get())) {
                    f *= 3.0F;
                }
                if (net.minecraftforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int) (25.0F / f) + 1) == 0)) {
                    level.setBlock(pos, getStateForAge(i + 1), 2);
                    fireCropGrowPost(level, pos, state);
                }
            }
        }
    }
}
