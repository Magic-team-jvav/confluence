package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.block.StateProperties;
import org.confluence.mod.common.menu.AlchemyTableMenu;
import org.jetbrains.annotations.Nullable;

public class AlchemyTableBlock extends HorizontalDirectionalWithVerticalFourPartBlock {
    public static final MapCodec<AlchemyTableBlock> CODEC = simpleCodec(AlchemyTableBlock::new);
    private static final VoxelShape BASE_SHAPE_SOUTH = Shapes.or(box(5, 3, 2, 16, 12, 14), box(2, 0, 1, 16, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_WEST = Shapes.or(box(2, 3, 5, 14, 12, 16), box(1, 0, 2, 15, 3, 16), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_NORTH = Shapes.or(box(0, 3, 2, 11, 12, 14), box(0, 0, 1, 14, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_EAST = Shapes.or(box(2, 3, 0, 14, 12, 11), box(1, 0, 0, 15, 3, 14), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_SOUTH = Shapes.or(box(0, 3, 2, 11, 12, 14), box(0, 0, 1, 14, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_WEST = Shapes.or(box(2, 3, 0, 14, 12, 11), box(1, 0, 0, 15, 3, 14), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_NORTH = Shapes.or(box(5, 3, 2, 16, 12, 14), box(2, 0, 1, 16, 3, 15), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_EAST = Shapes.or(box(2, 3, 5, 14, 12, 16), box(1, 0, 2, 15, 3, 16), box(0, 12, 0, 16, 16, 16));
    private static final VoxelShape UP_SHAPE_SOUTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_WEST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_NORTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape UP_SHAPE_EAST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_SOUTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_WEST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_NORTH = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape RIGHT_UP_SHAPE_EAST = box(0, 0, 0, 16, 0.02, 16);
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};
    private static final VoxelShape[] UP_SHAPES = new VoxelShape[]{UP_SHAPE_SOUTH, UP_SHAPE_WEST, UP_SHAPE_NORTH, UP_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_UP_SHAPES = new VoxelShape[]{RIGHT_UP_SHAPE_SOUTH, RIGHT_UP_SHAPE_WEST, RIGHT_UP_SHAPE_NORTH, RIGHT_UP_SHAPE_EAST};

    public AlchemyTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<AlchemyTableBlock> codec() {
        return CODEC;
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
    public @Nullable MenuProvider getMenuProvider(BlockState pState, Level pLevel, BlockPos pPos) {
        return new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> new AlchemyTableMenu(pContainerId, pPlayerInventory, ContainerLevelAccess.create(pLevel, pPos)), Component.translatable("container.confluence.alchemy_table"));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(StateProperties.VERTICAL_FOUR_PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }
}
