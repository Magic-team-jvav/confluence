package org.confluence.mod.common.block.natural;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LAYERS;

public class SandLayerBlock extends Block {
    protected static final VoxelShape[] SHAPE_BY_LAYER;

    static {
        VoxelShape[] shapes = new VoxelShape[9];
        shapes[0] = Shapes.empty();
        for (int i = 1; i <= 8; i++) {
            shapes[i] = box(0, 0, 0, 16, 2 * i, 16);
        }
        SHAPE_BY_LAYER = shapes;
    }

    public SandLayerBlock() {
        super(Properties.of().sound(SoundType.SAND));
        this.registerDefaultState(this.defaultBlockState().setValue(LAYERS, 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS)];
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(LAYERS) == 8 ? 0.2F : 1.0F;
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext UseContext) {
        int StackLayers = state.getValue(LAYERS);
        if (UseContext.getItemInHand().is(asItem()) && StackLayers < 8) {
            if (UseContext.replacingClickedOnBlock()) {
                return UseContext.getClickedFace() == Direction.UP;
            }
            return true;
        }
        return StackLayers == 1;
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        if (state.is(this) && state.getValue(LAYERS) < 8) {
            return state.cycle(LAYERS);
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        return below.is(this) || !below.isAir();
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos CurrentPos, BlockPos facingPos) {
        return state.canSurvive(level, CurrentPos)
                ? super.updateShape(state, facing, facingState, level, CurrentPos, facingPos)
                : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }
}
