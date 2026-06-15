package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.mod.common.menu.SawmillMenu;
import org.jetbrains.annotations.Nullable;

public class SawmillBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock {
    private static final VoxelShape[] SHAPES_BASE = new VoxelShape[]{
            Shapes.or(box(3, 2, 1, 16, 4, 15), box(1, 0, 1, 3, 12, 15), box(0, 12, 7, 3, 14, 9), box(0, 12, 9, 16, 14, 16), box(0, 12, 0, 16, 14, 7)),
            Shapes.or(box(1, 2, 3, 15, 4, 16), box(1, 0, 1, 15, 12, 3), box(0, 12, 0, 7, 14, 16), box(9, 12, 0, 16, 14, 16), box(7, 12, 0, 9, 14, 3)),
            Shapes.or(box(0, 2, 1, 13, 4, 15), box(13, 0, 1, 15, 12, 15), box(13, 12, 7, 16, 14, 9), box(0, 12, 9, 16, 14, 16), box(0, 12, 0, 16, 14, 7)),
            Shapes.or(box(1, 2, 0, 15, 4, 13), box(1, 0, 13, 15, 12, 15), box(7, 12, 13, 9, 14, 16), box(9, 12, 0, 16, 14, 16), box(0, 12, 0, 7, 14, 16))
    };
    private static final VoxelShape[] SHAPES_RIGHT = new VoxelShape[]{
            Shapes.or(box(0, 14, 4.5, 13, 21, 7.5), box(0, 14, 8.5, 13, 21, 11.5), box(3, 14, 7.5, 13, 21, 8.5), box(0, 2, 1, 13, 4, 15), box(13, 0, 1, 15, 12, 15), box(13, 12, 7, 16, 14, 9), box(0, 12, 9, 16, 14, 16), box(0, 12, 0, 16, 14, 7)),
            Shapes.or(box(8.5, 14, 0, 11.5, 21, 13), box(4.5, 14, 0, 7.5, 21, 13), box(7.5, 14, 3, 8.5, 21, 13), box(1, 2, 0, 15, 4, 13), box(1, 0, 13, 15, 12, 15), box(7, 12, 13, 9, 14, 16), box(9, 12, 0, 16, 14, 16), box(0, 12, 0, 7, 14, 16)),
            Shapes.or(box(3, 14, 8.5, 16, 21, 11.5), box(3, 14, 4.5, 16, 21, 7.5), box(3, 14, 7.5, 13, 21, 8.5), box(3, 2, 1, 16, 4, 15), box(1, 0, 1, 3, 12, 15), box(0, 12, 7, 3, 14, 9), box(0, 12, 9, 16, 14, 16), box(0, 12, 0, 16, 14, 7)),
            Shapes.or(box(4.5, 14, 3, 7.5, 21, 16), box(8.5, 14, 3, 11.5, 21, 16), box(7.5, 14, 3, 8.5, 21, 13), box(1, 2, 3, 15, 4, 16), box(1, 0, 1, 15, 12, 3), box(0, 12, 0, 7, 14, 16), box(9, 12, 0, 16, 14, 16), box(7, 12, 0, 9, 14, 3))
    };

    public SawmillBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            player.openMenu(state.getMenuProvider(level, pos));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART).isBase() ? SHAPES_BASE[index] : SHAPES_RIGHT[index];
    }

    @Override
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new SawmillMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("container.confluence.sawmill"));
    }
}
