package org.confluence.mod.common.block.natural;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

public class CattailsBodyBlock extends GrowingPlantBodyBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<CattailsBodyBlock> CODEC = simpleCodec(CattailsBodyBlock::new);
    protected static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    protected static final VoxelShape SHAPE = box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);

    public CattailsBodyBlock(BlockBehaviour.Properties properties) {
        super(properties, Direction.UP, SHAPE, true);
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected MapCodec<CattailsBodyBlock> codec() {
        return CODEC;
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        BlockState state = defaultBlockState();
        if (state.is(NatureBlocks.CATTAILS_BODY.get())) {
            return NatureBlocks.CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.JUNGLE_CATTAILS_BODY.get())) {
            return NatureBlocks.JUNGLE_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.GLOWING_MUSHROOM_CATTAILS_BODY.get())) {
            return NatureBlocks.GLOWING_MUSHROOM_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.HALLOW_CATTAILS_BODY.get())) {
            return NatureBlocks.HALLOW_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.EBONY_CATTAILS_BODY.get())) {
            return NatureBlocks.EBONY_CATTAILS_HEAD.get();
        } else if (state.is(NatureBlocks.CRIMSON_CATTAILS_BODY.get())) {
            return NatureBlocks.CRIMSON_CATTAILS_HEAD.get();
        } else {
            return NatureBlocks.CATTAILS_HEAD.get();
        }
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        boolean water = state.getFluidState().is(Fluids.WATER);
        if (facing == this.growthDirection.getOpposite() && !state.canSurvive(level, currentPos)) {
            level.scheduleTick(currentPos, this, 1);
        }

        GrowingPlantHeadBlock growingplantheadblock = this.getHeadBlock();
        if (facing == this.growthDirection && !facingState.is(this) && !facingState.is(growingplantheadblock)) {
            return this.updateHeadAfterConvertedFromBody(state, growingplantheadblock.getStateForPlacement(level).trySetValue(WATERLOGGED, water));
        } else {
            if (this.scheduleFluidTicks) {
                level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
            }

            return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
        }
    }
}
