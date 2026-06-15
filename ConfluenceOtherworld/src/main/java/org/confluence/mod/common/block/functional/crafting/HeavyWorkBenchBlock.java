package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.jetbrains.annotations.Nullable;

public class HeavyWorkBenchBlock extends HorizontalDirectionalWithVerticalFourPartBlock {
    private static final VoxelShape BASE_SHAPE_SOUTH = Shapes.or(box(2, 0, 0, 4, 13, 13), box(4, 0, 0, 16, 13, 2), box(2, 0, 13, 4, 4, 16), box(4, 0, 2, 14, 13, 13), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_WEST = Shapes.or(box(3, 0, 2, 16, 13, 4), box(14, 0, 4, 16, 13, 16), box(0, 0, 2, 3, 4, 4), box(3, 0, 4, 14, 13, 14), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_NORTH = Shapes.or(box(12, 0, 3, 14, 13, 16), box(0, 0, 14, 12, 13, 16), box(12, 0, 0, 14, 4, 3), box(2, 0, 3, 12, 13, 14), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape BASE_SHAPE_EAST = Shapes.or(box(0, 0, 12, 13, 13, 14), box(0, 0, 0, 2, 13, 12), box(13, 0, 12, 16, 4, 14), box(2, 0, 2, 13, 13, 12), box(0, 13, 0, 16, 16, 16));
    private static final VoxelShape RIGHT_SHAPE_SOUTH = Shapes.or(box(12, 0, 0, 14, 13, 13), box(0, 0, 0, 12, 13, 2), box(0, 13, 0, 16, 16, 16), box(12, 0, 13, 14, 4, 16));
    private static final VoxelShape RIGHT_SHAPE_WEST = Shapes.or(box(3, 0, 12, 16, 13, 14), box(14, 0, 0, 16, 13, 12), box(0, 13, 0, 16, 16, 16), box(0, 0, 12, 3, 4, 14));
    private static final VoxelShape RIGHT_SHAPE_NORTH = Shapes.or(box(2, 0, 3, 4, 13, 16), box(4, 0, 14, 16, 13, 16), box(0, 13, 0, 16, 16, 16), box(2, 0, 0, 4, 4, 3));
    private static final VoxelShape RIGHT_SHAPE_EAST = Shapes.or(box(0, 0, 2, 13, 13, 4), box(0, 0, 4, 2, 13, 16), box(0, 13, 0, 16, 16, 16), box(13, 0, 2, 16, 4, 4));
    private static final VoxelShape UP_SHAPE_SOUTH = Shapes.or(box(1, 0, 2, 3, 6, 13), box(1, 0, 0, 16, 13, 2), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape UP_SHAPE_WEST = Shapes.or(box(3, 0, 1, 14, 6, 3), box(14, 0, 1, 16, 13, 16), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape UP_SHAPE_NORTH = Shapes.or(box(13, 0, 3, 15, 6, 14), box(0, 0, 14, 15, 13, 16), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape UP_SHAPE_EAST = Shapes.or(box(2, 0, 13, 13, 6, 15), box(0, 0, 0, 2, 13, 15), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape RIGHT_UP_SHAPE_SOUTH = Shapes.or(box(0, 0, 0, 15, 13, 2), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape RIGHT_UP_SHAPE_WEST = Shapes.or(box(14, 0, 0, 16, 13, 15), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape RIGHT_UP_SHAPE_NORTH = Shapes.or(box(1, 0, 14, 16, 13, 16), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape RIGHT_UP_SHAPE_EAST = Shapes.or(box(0, 0, 1, 2, 13, 16), box(0, 0, 0, 16, 0.02, 16));
    private static final VoxelShape[] BASE_SHAPES = new VoxelShape[]{BASE_SHAPE_SOUTH, BASE_SHAPE_WEST, BASE_SHAPE_NORTH, BASE_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_SHAPES = new VoxelShape[]{RIGHT_SHAPE_SOUTH, RIGHT_SHAPE_WEST, RIGHT_SHAPE_NORTH, RIGHT_SHAPE_EAST};
    private static final VoxelShape[] UP_SHAPES = new VoxelShape[]{UP_SHAPE_SOUTH, UP_SHAPE_WEST, UP_SHAPE_NORTH, UP_SHAPE_EAST};
    private static final VoxelShape[] RIGHT_UP_SHAPES = new VoxelShape[]{RIGHT_UP_SHAPE_SOUTH, RIGHT_UP_SHAPE_WEST, RIGHT_UP_SHAPE_NORTH, RIGHT_UP_SHAPE_EAST};

    public HeavyWorkBenchBlock(Properties properties) {
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
    public @Nullable MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider((containerId, inventory, player) -> new HeavyWorkBenchMenu(containerId, inventory, new EnvironmentLevelAccess(level, pos)), Component.translatable("container.confluence.heavy_work_bench"));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue();
        return switch (state.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }
}
