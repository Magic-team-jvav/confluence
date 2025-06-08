package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.menu.HardmodeAnvilMenu;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class HardmodeAnvilBlock extends FallingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<HardmodeAnvilBlock> CODEC = simpleCodec(HardmodeAnvilBlock::new);
    private static final VoxelShape X_AXIS_AABB;
    private static final VoxelShape Z_AXIS_AABB;

    static {
        X_AXIS_AABB = Stream.of(
                Block.box(3, 0, 4, 13, 4, 12),
                Block.box(5, 4, 5, 11, 8, 11),
                Block.box(2, 8, 3.5, 14, 14, 12.5),
                Block.box(0, 9, 3.5, 2, 14, 12.5),
                Block.box(14, 9, 4.5, 16, 14, 11.5),
                Block.box(0, 9.01, 3.3934, 4.37868, 13.99, 9.75736)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        Z_AXIS_AABB = Stream.of(
                Block.box(4, 0, 3, 12, 4, 13),
                Block.box(5, 4, 5, 11, 8, 11),
                Block.box(3.5, 8, 2, 12.5, 14, 14),
                Block.box(3.5, 9, 0, 12.5, 14, 2),
                Block.box(4.5, 9, 14, 11.5, 14, 16),
                Block.box(3.3934, 9.01, 0, 9.75736, 13.99, 4.37868)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        // 修正方向判断：X轴方向对应南北朝向，Z轴方向对应东西朝向
        return direction.getAxis() == Direction.Axis.Z ? Z_AXIS_AABB : X_AXIS_AABB;
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    public HardmodeAnvilBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected MapCodec<HardmodeAnvilBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new HardmodeAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)),
                Component.translatable("container.confluence." + BuiltInRegistries.BLOCK.getKey(this).getPath()));
    }
}