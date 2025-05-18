package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.mod.common.menu.SolidifierMenu;
import org.jetbrains.annotations.Nullable;

public class SolidifierBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<SolidifierBlock> CODEC = simpleCodec(SolidifierBlock::new);
    private static final VoxelShape[] SHAPES = new VoxelShape[]{
            Shapes.or(box(4.5, 2, 14, 11.5, 9, 15), box(1, 0, 2, 15, 11, 14), box(0, 0, 11, 16, 2, 13), box(0, 0, 3, 16, 2, 5), box(6, 11, 6, 10, 19, 10)),
            Shapes.or(box(1, 2, 4.5, 2, 9, 11.5), box(2, 0, 1, 14, 11, 15), box(11, 0, 0, 13, 2, 16), box(3, 0, 0, 5, 2, 16), box(6, 11, 6, 10, 19, 10)),
            Shapes.or(box(4.5, 2, 1, 11.5, 9, 2), box(1, 0, 2, 15, 11, 14), box(0, 0, 11, 16, 2, 13), box(0, 0, 3, 16, 2, 5), box(6, 11, 6, 10, 19, 10)),
            Shapes.or(box(14, 2, 4.5, 15, 9, 11.5), box(2, 0, 1, 14, 11, 15), box(11, 0, 0, 13, 2, 16), box(3, 0, 0, 5, 2, 16), box(6, 11, 6, 10, 19, 10))
    };

    public SolidifierBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected MapCodec<SolidifierBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
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
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new SolidifierMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("container.confluence.solidifier"));
    }
}
