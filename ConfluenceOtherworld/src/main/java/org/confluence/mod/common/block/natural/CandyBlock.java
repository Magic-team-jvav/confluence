package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.confluence.lib.util.LibUtils;

import java.util.Map;

public class CandyBlock extends Block {
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public CandyBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(NORTH, true).setValue(EAST, true)
                .setValue(SOUTH, true).setValue(WEST, true)
                .setValue(UP, true).setValue(DOWN, true));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = this.defaultBlockState();
        for (Direction direction : LibUtils.DIRECTIONS) {
            BooleanProperty prop = PROPERTY_BY_DIRECTION.get(direction);
            if (prop != null) {
                state = state.setValue(prop, !level.getBlockState(pos.relative(direction)).is(this));
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return facingState.is(this)
                ? state.setValue(PROPERTY_BY_DIRECTION.get(facing), false)
                : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        BlockState result = state;
        for (Direction dir : LibUtils.DIRECTIONS) {
            if (PROPERTY_BY_DIRECTION.containsKey(dir)) {
                result = result.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(dir)), state.getValue(PROPERTY_BY_DIRECTION.get(dir)));
            }
        }
        return result;
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        BlockState result = state;
        for (Direction dir : LibUtils.DIRECTIONS) {
            if (PROPERTY_BY_DIRECTION.containsKey(dir)) {
                result = result.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(dir)), state.getValue(PROPERTY_BY_DIRECTION.get(dir)));
            }
        }
        return result;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }
}
