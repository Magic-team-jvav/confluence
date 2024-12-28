package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.block.StateProperties;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.confluence.mod.common.recipe.EnvironmentLevelAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeavyWorkBenchBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HeavyWorkBenchBlock> CODEC = simpleCodec(HeavyWorkBenchBlock::new);
    private static final VoxelShape BASE_SHAPE_SOUTH = Shapes.or(box(2, 0, 0, 4, 13, 13), box(4, 0, 0, 16, 13, 2), box(2, 0, 13, 4, 4, 16), box(4, 0, 2, 14, 13, 13), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_WEST = Shapes.or(box(3, 0, 2, 16, 13, 4), box(14, 0, 4, 16, 13, 16), box(0, 0, 2, 3, 4, 4), box(3, 0, 4, 14, 13, 14), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_NORTH = Shapes.or(box(12, 0, 3, 14, 13, 16), box(0, 0, 14, 12, 13, 16), box(12, 0, 0, 14, 4, 3), box(2, 0, 3, 12, 13, 14), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_EAST = Shapes.or(box(0, 0, 12, 13, 13, 14), box(0, 0, 0, 2, 13, 12), box(13, 0, 12, 16, 4, 14), box(2, 0, 2, 13, 13, 12), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_SOUTH = Shapes.or(box(12, 0, 0, 14, 13, 13), box(0, 0, 0, 12, 13, 2), box(0, 13, 0, 16, 16, 16), box(12, 0, 13, 14, 4, 16));
    private static final VoxelShape RIGHT_SHAPE_WEST = Shapes.or(box(3, 0, 12, 16, 13, 14), box(14, 0, 0, 16, 13, 12), box(0, 13, 0, 16, 16, 16), box(0, 0, 12, 3, 4, 14));
    private static final VoxelShape RIGHT_SHAPE_NORTH = Shapes.or(box(2, 0, 3, 4, 13, 16), box(4, 0, 14, 16, 13, 16), box(0, 13, 0, 16, 16, 16), box(2, 0, 0, 4, 4, 3));
    private static final VoxelShape RIGHT_SHAPE_EAST = Shapes.or(box(0, 0, 2, 13, 13, 4), box(0, 0, 4, 2, 13, 16), box(0, 13, 0, 16, 16, 16), box(13, 0, 2, 16, 4, 4));
    private static final VoxelShape UP_SHAPE_SOUTH = Shapes.or(box(1, 0, 2, 3, 6, 13), box(1, 0, 0, 16, 13, 2));
    private static final VoxelShape UP_SHAPE_WEST = Shapes.or(box(3, 0, 1, 14, 6, 3), box(14, 0, 1, 16, 13, 16));
    private static final VoxelShape UP_SHAPE_NORTH = Shapes.or(box(13, 0, 3, 15, 6, 14), box(0, 0, 14, 15, 13, 16));
    private static final VoxelShape UP_SHAPE_EAST = Shapes.or(box(2, 0, 13, 13, 6, 15), box(0, 0, 0, 2, 13, 15));
    private static final VoxelShape RIGHT_UP_SHAPE_SOUTH = box(0, 0, 0, 15, 13, 2);
    private static final VoxelShape RIGHT_UP_SHAPE_WEST = box(14, 0, 0, 16, 13, 15);
    private static final VoxelShape RIGHT_UP_SHAPE_NORTH = box(1, 0, 14, 16, 13, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_EAST = box(0, 0, 1, 2, 13, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};
    private static final VoxelShape[] UP_SHAPES = new VoxelShape[]{UP_SHAPE_SOUTH, UP_SHAPE_WEST, UP_SHAPE_NORTH, UP_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_UP_SHAPES = new VoxelShape[]{RIGHT_UP_SHAPE_SOUTH, RIGHT_UP_SHAPE_WEST, RIGHT_UP_SHAPE_NORTH, RIGHT_UP_SHAPE_EAST};
    private static final Component CONTAINER_TITLE = Component.translatable("container.confluence.heavy_work_bench");

    public HeavyWorkBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<HeavyWorkBenchBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(StateProperties.VERTICAL_FOUR_PART, FACING);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos) {
        return new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> new HeavyWorkBenchMenu(pContainerId, pPlayerInventory, new HeavyWorkBenchBlock.LevelAccess(pLevel, pPos)), CONTAINER_TITLE);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(StateProperties.VERTICAL_FOUR_PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }

    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, defaultBlockState().setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.RIGHT).setValue(FACING, pState.getValue(FACING)));
            pLevel.setBlockAndUpdate(pPos.above(), defaultBlockState().setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.UP).setValue(FACING, pState.getValue(FACING)));
            pLevel.setBlockAndUpdate(relativePos.above(), defaultBlockState().setValue(StateProperties.VERTICAL_FOUR_PART, StateProperties.VerticalFourPart.RIGHT_UP).setValue(FACING, pState.getValue(FACING)));
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos relativeUpPos = clickedPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(blockState)).above();
        for (BlockPos blockPos : BlockPos.betweenClosed(clickedPos, relativeUpPos)) {
            if (!level.getBlockState(blockPos).canBeReplaced(pContext) || !level.getWorldBorder().isWithinBounds(blockPos)) {
                return null;
            }
        }
        return blockState;
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pMovedByPiston) {
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston);
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockPos relative = pPos.relative(StateProperties.VerticalFourPart.getConnectedDirection(pState));
        pLevel.setBlockAndUpdate(relative, air);
        if (pState.getValue(StateProperties.VERTICAL_FOUR_PART).isUpper()) {
            pLevel.setBlockAndUpdate(pPos.below(), air);
            pLevel.setBlockAndUpdate(relative.below(), air);
        } else {
            pLevel.setBlockAndUpdate(pPos.above(), air);
            pLevel.setBlockAndUpdate(relative.above(), air);
        }
    }

    public static class LevelAccess extends EnvironmentLevelAccess {
        public LevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
            super(level, pos);
        }

        @Override
        public <R extends Recipe<?>> boolean matches(R recipe) {
            if (level == null) return false;
            ItemStack resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(NatureBlocks.THIN_ICE_BLOCK.asItem())) {
                return false; // todo 灵雾
            }
            return true;
        }
    }
}
