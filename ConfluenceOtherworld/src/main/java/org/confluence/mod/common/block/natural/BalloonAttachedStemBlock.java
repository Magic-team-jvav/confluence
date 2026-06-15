package org.confluence.mod.common.block.natural;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.init.block.NatureBlocks;

import java.util.Map;
import java.util.function.Supplier;

public class BalloonAttachedStemBlock extends AttachedStemBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    private final StemGrownBlock fruit;

    private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.SOUTH, Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 16.0))
            .put(Direction.WEST, Block.box(0.0, 0.0, 6.0, 10.0, 10.0, 10.0))
            .put(Direction.NORTH, Block.box(6.0, 0.0, 0.0, 10.0, 10.0, 10.0))
            .put(Direction.EAST, Block.box(6.0, 0.0, 6.0, 16.0, 10.0, 10.0))
            .put(Direction.UP, Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0))
            .build());

    public BalloonAttachedStemBlock(StemGrownBlock fruit, Supplier<Item> seedSupplier, BlockBehaviour.Properties properties) {
        super(fruit, seedSupplier, properties);
        this.fruit = fruit;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABBS.get(state.getValue(FACING));
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        Direction attachedDirection = state.getValue(FACING);
        if (facing == attachedDirection) {
            if (!facingState.is(this.fruit)) {
                BlockState stemState = this.fruit.getStem().defaultBlockState();
                if (stemState.hasProperty(StemBlock.AGE)) {
                    return stemState.setValue(StemBlock.AGE, 7);
                }
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getBlock() instanceof FarmBlock ||
                state.is(NatureBlocks.CLOUD_BLOCK.get()) ||
                state.is(NatureBlocks.RAIN_CLOUD_BLOCK.get());
    }
}
