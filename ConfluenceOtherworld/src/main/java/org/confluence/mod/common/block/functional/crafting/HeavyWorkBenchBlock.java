package org.confluence.mod.common.block.functional.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.block.HorizontalDirectionalWithVerticalFourPartBlock;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.menu.HeavyWorkBenchMenu;
import org.confluence.mod.util.DynamicBiomeUtils;
import org.jetbrains.annotations.Nullable;

public class HeavyWorkBenchBlock extends HorizontalDirectionalWithVerticalFourPartBlock {
    public static final MapCodec<HeavyWorkBenchBlock> CODEC = simpleCodec(HeavyWorkBenchBlock::new);
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
    protected MapCodec<HeavyWorkBenchBlock> codec() {
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
        return new SimpleMenuProvider((pContainerId, pPlayerInventory, pPlayer) -> new HeavyWorkBenchMenu(pContainerId, pPlayerInventory, new HeavyWorkBenchBlock.LevelAccess(pLevel, pPos)), Component.translatable("container.confluence.heavy_work_bench"));
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        int index = pState.getValue(FACING).get2DDataValue();
        return switch (pState.getValue(PART)) {
            case BASE -> BASE_SHAPES[index];
            case RIGHT -> RIGHT_SHAPES[index];
            case UP -> UP_SHAPES[index];
            case RIGHT_UP -> RIGHT_UP_SHAPES[index];
        };
    }

    public static class LevelAccess extends EnvironmentLevelAccess {
        public LevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
            super(level, pos);
        }

        @Override
        public <R extends Recipe<?>> boolean matches(R recipe) {
            if (level == null || pos == null) return false;
            ItemStack resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(NatureBlocks.THIN_ICE_BLOCK.asItem())) {
                return DynamicBiomeUtils.getISection(level, pos).confluence$isGraveyard();
            }
            return true;
        }
    }
}
