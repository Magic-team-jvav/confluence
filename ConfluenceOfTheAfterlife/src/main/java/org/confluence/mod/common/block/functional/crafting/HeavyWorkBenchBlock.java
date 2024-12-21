package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeavyWorkBenchBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<HeavyWorkBenchBlock> CODEC = simpleCodec(HeavyWorkBenchBlock::new);
    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);
    private static final VoxelShape BASE_SHAPE_SOUTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape BASE_SHAPE_WEST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape BASE_SHAPE_NORTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape BASE_SHAPE_EAST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_SOUTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_WEST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_NORTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape RIGHT_SHAPE_EAST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape UP_SHAPE_SOUTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape UP_SHAPE_WEST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape UP_SHAPE_NORTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape UP_SHAPE_EAST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_SOUTH = box(0, 0, 3, 13, 16, 13);
    private static final VoxelShape RIGHT_UP_SHAPE_WEST = box(3, 0, 0, 13, 16, 13);
    private static final VoxelShape RIGHT_UP_SHAPE_NORTH = box(3, 0, 3, 16, 16, 13);
    private static final VoxelShape RIGHT_UP_SHAPE_EAST = box(3, 0, 3, 13, 16, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};
    private static final VoxelShape[] UP_SHAPES = new VoxelShape[]{UP_SHAPE_SOUTH, UP_SHAPE_WEST, UP_SHAPE_NORTH, UP_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_UP_SHAPES = new VoxelShape[]{RIGHT_UP_SHAPE_SOUTH, RIGHT_UP_SHAPE_WEST, RIGHT_UP_SHAPE_NORTH, RIGHT_UP_SHAPE_EAST};
    private static final Component CONTAINER_TITLE = Component.translatable("container.confluence.heavy_work_bench");

    public HeavyWorkBenchBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(PART, Part.BASE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<HeavyWorkBenchBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(PART, FACING);
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
        return new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> new HeavyWorkBenchMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pLevel, pPos)), CONTAINER_TITLE);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }

    @Override
    public void setPlacedBy(Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (!pLevel.isClientSide) {
            BlockPos relativePos = pPos.relative(getConnectedDirection(pState));
            pLevel.setBlockAndUpdate(relativePos, defaultBlockState().setValue(PART, Part.RIGHT).setValue(FACING, pState.getValue(FACING)));
            pLevel.setBlockAndUpdate(pPos.above(), defaultBlockState().setValue(PART, Part.UP).setValue(FACING, pState.getValue(FACING)));
            pLevel.setBlockAndUpdate(relativePos.above(), defaultBlockState().setValue(PART, Part.RIGHT_UP).setValue(FACING, pState.getValue(FACING)));
        }
    }

    /**
     * 获取与该方块相连的多方块的相对方向
     * <p>
     * 注：是以玩家视角看向的相对方向
     *
     * @param blockState 该方块的方块状态
     * @return 相对方向
     */
    public static Direction getConnectedDirection(BlockState blockState) {
        Direction facing = blockState.getValue(FACING);
        return switch (blockState.getValue(PART)) {
            case BASE, UP -> facing.getCounterClockWise(); // 获取其相对右边
            case RIGHT, RIGHT_UP -> facing.getClockWise(); // 获取其相对左边
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockState blockState = defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
        BlockPos clickedPos = pContext.getClickedPos();
        BlockPos relativeUpPos = clickedPos.relative(getConnectedDirection(blockState)).above();
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
        BlockPos relative = pPos.relative(getConnectedDirection(pState));
        pLevel.setBlockAndUpdate(relative, air);
        if (pState.getValue(PART).isUpper()) {
            pLevel.setBlockAndUpdate(pPos.below(), air);
            pLevel.setBlockAndUpdate(relative.below(), air);
        } else {
            pLevel.setBlockAndUpdate(pPos.above(), air);
            pLevel.setBlockAndUpdate(relative.above(), air);
        }
    }

    public enum Part implements StringRepresentable {
        BASE("base"),
        RIGHT("right"),
        UP("up"),
        RIGHT_UP("right_up");

        private final String name;

        Part(String pName) {
            this.name = pName;
        }

        public String toString() {
            return name;
        }

        public @NotNull String getSerializedName() {
            return name;
        }

        public boolean isUpper() {
            return this == UP || this == RIGHT_UP;
        }
    }
}
