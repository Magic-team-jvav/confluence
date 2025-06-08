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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.menu.HardmodeAnvilMenu;
import org.jetbrains.annotations.Nullable;

public class HardmodeAnvilBlock extends FallingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final MapCodec<HardmodeAnvilBlock> CODEC = simpleCodec(HardmodeAnvilBlock::new);
    private static final VoxelShape BASE;
    private static final VoxelShape X_LEG1;
    private static final VoxelShape X_LEG2;
    private static final VoxelShape X_TOP;
    private static final VoxelShape Z_LEG1;
    private static final VoxelShape Z_LEG2;
    private static final VoxelShape Z_TOP;
    private static final VoxelShape X_AXIS_AABB;
    private static final VoxelShape Z_AXIS_AABB;

    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = (Direction)state.getValue(FACING);
        return direction.getAxis() == Direction.Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
    }
    static {
        BASE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
        X_LEG1 = Block.box(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
        X_LEG2 = Block.box(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
        X_TOP = Block.box(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
        Z_LEG1 = Block.box(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
        Z_LEG2 = Block.box(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
        Z_TOP = Block.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
        X_AXIS_AABB = Shapes.or(BASE, new VoxelShape[]{X_LEG1, X_LEG2, X_TOP});
        Z_AXIS_AABB = Shapes.or(BASE, new VoxelShape[]{Z_LEG1, Z_LEG2, Z_TOP});
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
        return new SimpleMenuProvider((containerId, inventory, player) -> new HardmodeAnvilMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("container.confluence." + BuiltInRegistries.BLOCK.getKey(this).getPath()));
    }
}
