package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoconutBlock extends Block {
    private static final IntegerProperty PIECE = IntegerProperty.create("piece", 0, 3);
    private static final VoxelShape[] SHAPE_BY_PIECE = new VoxelShape[]{
        box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0),
        box(3.0, 0.0, 3.0, 13.0, 4.0, 13.0),
        box(3.0, 0.0, 3.0, 13.0, 5.0, 13.0),
        box(3.0, 0.0, 3.0, 13.0, 9.0, 13.0)
    };
    public CoconutBlock() {
        super(BlockBehaviour.Properties.of().strength(1.0f).pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any().setValue(PIECE, 0));
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext UseContext) {
        return !UseContext.isSecondaryUseActive() && UseContext.getItemInHand().is(asItem()) && state.getValue(PIECE) < 3 || super.canBeReplaced(state, UseContext);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState clickedBlockState = context.getLevel().getBlockState(context.getClickedPos());
        if (clickedBlockState.is(this)) {
            int currentPiece = clickedBlockState.getValue(PIECE);
            if (currentPiece < 3) {
                return clickedBlockState.setValue(PIECE, currentPiece + 1);
            }
        }
        return this.defaultBlockState();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_PIECE[state.getValue(PIECE)];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PIECE);
    }
}
