package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.menu.CrystalBallMenu;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class CrystalBallBlock extends Block {
    public CrystalBallBlock(Properties properties) {
        super(properties);
    }
    private static final VoxelShape SHAPE = Stream.of(
            Block.box(4.375, 4.032, 4.375, 11.625, 10.125, 11.625),
            Block.box(2, 0, 2, 14, 4, 14),
            Block.box(1, 1.5, 1, 3, 3.5, 3),
            Block.box(13, 1.5, 1, 15, 3.5, 3),
            Block.box(13, 1.5, 13, 15, 3.5, 15),
            Block.box(1, 1.5, 13, 3, 3.5, 15),
            Block.box(4.5, 3, 4.5, 11.5, 10, 11.5)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
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
        return new SimpleMenuProvider((containerId, inventory, player) -> new CrystalBallMenu(containerId, inventory, new LevelAccess(level, pos)), Component.translatable("container.confluence.crystal_ball"));
    }

    public static class LevelAccess extends EnvironmentLevelAccess {
        public LevelAccess(@Nullable Level level, @Nullable BlockPos pos) {
            super(level, pos);
        }

        @Override
        public <R extends Recipe<?>> boolean matches(R recipe) {
            if (level == null || pos == null) return false;
            //ItemStack resultItem = recipe.getResultItem(level.registryAccess()); todo
            return true;
        }
    }
}
