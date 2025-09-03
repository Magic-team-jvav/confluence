package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithHorizontalTwoPartBlock;
import org.confluence.mod.common.menu.DyeVatMenu;

public class DyeVatBlock extends HorizontalDirectionalWithHorizontalTwoPartBlock {
    private static final VoxelShape BASE_SHAPE_SOUTH = box(3.0, 0.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE_WEST = box(3.0, 0.0, 3.0, 13.0, 16.0, 16.0);
    private static final VoxelShape BASE_SHAPE_NORTH = box(0.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape BASE_SHAPE_EAST = box(3.0, 0.0, 0.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_SOUTH = box(0.0, 0.0, 3.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_WEST = box(3.0, 0.0, 0.0, 13.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_NORTH = box(3.0, 0.0, 3.0, 16.0, 16.0, 13.0);
    private static final VoxelShape RIGHT_SHAPE_EAST = box(3.0, 0.0, 3.0, 13.0, 16.0, 16.0);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return state.getValue(PART).isBase() ? BASE_SHAPES[index] : RIGHT_SHAPES[index];
    }

    public DyeVatBlock() {
        super(Properties.ofFullCopy(Blocks.BARREL));
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
        return new SimpleMenuProvider((containerId, inventory, player) -> new DyeVatMenu(containerId, inventory, ContainerLevelAccess.create(level, pos)), Component.translatable("container.confluence.dye_vat"));
    }
}
