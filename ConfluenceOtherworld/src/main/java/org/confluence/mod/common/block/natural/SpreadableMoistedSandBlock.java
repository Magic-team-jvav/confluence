package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import org.confluence.mod.common.block.natural.spreadable.ISpreadable;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Map;
import java.util.function.Supplier;

public class SpreadableMoistedSandBlock extends Block implements ISpreadable {
    private final ISpreadable.Type type;
    private final Supplier<? extends Block> targetBlock;
    public static final BooleanProperty NORTH = PipeBlock.NORTH;
    public static final BooleanProperty EAST = PipeBlock.EAST;
    public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
    public static final BooleanProperty WEST = PipeBlock.WEST;
    public static final BooleanProperty UP = PipeBlock.UP;
    public static final BooleanProperty DOWN = PipeBlock.DOWN;
    private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;

    public SpreadableMoistedSandBlock(ISpreadable.Type type, BlockBehaviour.Properties properties, Supplier<? extends Block> targetBlock) {
        super(properties.randomTicks().instrument(NoteBlockInstrument.SNARE).strength(0.5F).sound(SoundType.SAND));
        this.type = type;
        this.targetBlock = targetBlock;
        registerDefaultState(stateDefinition.any()
                .setValue(STILL_ALIVE, true)
                .setValue(NORTH, true)
                .setValue(EAST, true)
                .setValue(SOUTH, true)
                .setValue(WEST, true)
                .setValue(UP, true)
                .setValue(DOWN, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STILL_ALIVE, UP, DOWN, NORTH, EAST, SOUTH, WEST);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockGetter blockgetter = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        Block block = targetBlock.get();
        return this.defaultBlockState()
                .setValue(DOWN, !blockgetter.getBlockState(blockpos.below()).is(block))
                .setValue(UP, !blockgetter.getBlockState(blockpos.above()).is(block))
                .setValue(NORTH, !blockgetter.getBlockState(blockpos.north()).is(block))
                .setValue(EAST, !blockgetter.getBlockState(blockpos.east()).is(block))
                .setValue(SOUTH, !blockgetter.getBlockState(blockpos.south()).is(block))
                .setValue(WEST, !blockgetter.getBlockState(blockpos.west()).is(block));
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (!facingState.is(targetBlock.get())) {
            return state.setValue(PROPERTY_BY_DIRECTION.get(facing), true);
        }
        return state.setValue(PROPERTY_BY_DIRECTION.get(facing), false);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(rot.rotate(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.NORTH)), state.getValue(NORTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.SOUTH)), state.getValue(SOUTH))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.EAST)), state.getValue(EAST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.WEST)), state.getValue(WEST))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.UP)), state.getValue(UP))
                .setValue(PROPERTY_BY_DIRECTION.get(mirror.mirror(Direction.DOWN)), state.getValue(DOWN));
    }

    @Override
    public Type getSpreadType() {
        return type;
    }

    public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        if (!serverLevel.isAreaLoaded(blockPos, 3)) return;
        spread(blockState, serverLevel, blockPos, randomSource);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.dimensionType().ultraWarm()) {
            if (state.is(NatureBlocks.MOISTENED_EBONSAND_BLOCK.get())) {
                level.setBlock(pos, NatureBlocks.EBONSAND.get().defaultBlockState(), 3);
            } else if (state.is(NatureBlocks.MOISTENED_CRIMSAND_BLOCK.get())) {
                level.setBlock(pos, NatureBlocks.CRIMSAND.get().defaultBlockState(), 3);
            } else if (state.is(NatureBlocks.MOISTENED_PEARLSAND_BLOCK.get())) {
                level.setBlock(pos, NatureBlocks.PEARLSAND.get().defaultBlockState(), 3);
            }

            level.levelEvent(2009, pos, 0);
            level.playSound(null, pos, SoundEvents.WET_SPONGE_DRIES, SoundSource.BLOCKS, 1.0F, (1.0F + level.getRandom().nextFloat() * 0.2F) * 0.7F);
        }
    }
}
