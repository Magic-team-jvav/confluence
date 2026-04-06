package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CoralPlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;

public class LunarCoralPlantBlock extends CoralPlantBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private final Block deadBlock;

    public LunarCoralPlantBlock(Block deadBlock, Properties properties) {
        super(deadBlock, properties);
        this.deadBlock = deadBlock;
    }

    protected static boolean scanForWater(BlockState state, BlockGetter level, BlockPos pos) {
        if (state.getValue(WATERLOGGED)) {
            return true;
        } else {
            for (Direction direction : Direction.values()) {
                if (level.getFluidState(pos.relative(direction)).is(FluidTags.WATER)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (scanForWater(state, level, pos)) {
            level.setBlock(pos, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, state.getValue(WATERLOGGED)), 2);
        }
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN && !state.canSurvive(level, currentPos)) {
            return Blocks.AIR.defaultBlockState();
        } else {
            this.tryScheduleDieTick(state, level, currentPos);
            if (state.getValue(WATERLOGGED)) {
                level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }

    @Override
    protected void tryScheduleDieTick(BlockState state, LevelAccessor level, BlockPos pos) {
        if (scanForWater(state, level, pos)) {
            level.scheduleTick(pos, this, 60 + level.getRandom().nextInt(40));
        }
    }
}
