package org.confluence.mod.common.block.functional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class FragileBlock extends Block implements ISimulatorBlock {
    public static final BooleanProperty IS_SUPPORTING = BooleanProperty.create("is_supporting");
    private final Supplier<BlockState> simulatorBlock;

    public FragileBlock(Properties pProperties, Supplier<BlockState> simulatorBlock) {
        super(pProperties);
        this.simulatorBlock = simulatorBlock;
        registerDefaultState(stateDefinition.any().setValue(IS_SUPPORTING, true).setValue(BlockStateProperties.FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_SUPPORTING, BlockStateProperties.FACING);
    }

    @Override
    public BlockState getSimulatedBlock(boolean isClient) {
        return simulatorBlock.get();
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockState blockState = level.getBlockState(context.getClickedPos().relative(context.getClickedFace().getOpposite()));
        BlockState state = defaultBlockState();
        if (blockState.getBlock() instanceof FragileBlock) {
            state = state.setValue(IS_SUPPORTING, false);
        }
        return state.setValue(BlockStateProperties.FACING, context.getClickedFace().getOpposite());
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        level.removeBlock(pos, false);
    }

    @Override
    protected @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState, @NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (!level.isClientSide() && !state.getValue(IS_SUPPORTING)) {
            BlockState blockState = level.getBlockState(pos.relative(state.getValue(BlockStateProperties.FACING)));
            if (!(blockState.getBlock() instanceof FragileBlock)) {
                level.scheduleTick(pos, this, 1);
            }
        }
        return state;
    }

    @Override
    protected @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return simulatorBlock.get().getShape(level, pos, context);
    }
}
