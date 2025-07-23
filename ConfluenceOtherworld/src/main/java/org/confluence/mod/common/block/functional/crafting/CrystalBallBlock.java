package org.confluence.mod.common.block.functional.crafting;

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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.menu.CrystalBallMenu;
import org.confluence.terra_furniture.common.init.TFTags;
import org.jetbrains.annotations.Nullable;

public class CrystalBallBlock extends Block {
    public CrystalBallBlock(Properties properties) {
        super(properties);
    }
    private static final VoxelShape SHAPE = Shapes.or(
            box(2, 0, 2, 14, 4, 14),
            box(1, 1.5, 1, 3, 3.5, 3),
            box(13, 1.5, 1, 15, 3.5, 3),
            box(13, 1.5, 13, 15, 3.5, 15),
            box(1, 1.5, 13, 3, 3.5, 15),
            box(4.5, 3, 4.5, 11.5, 10, 11.5)
    );

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
            ItemStack resultItem = recipe.getResultItem(level.registryAccess());
            if (resultItem.is(ToolItems.MAGIC_HONEY_DROPPER)) {
                return anyMatch(state -> state.getFluidState().is(Tags.Fluids.HONEY), 2);
            } else if (resultItem.is(ToolItems.MAGIC_LAVA_DROPPER)) {
                return anyMatch(state -> state.getFluidState().is(Tags.Fluids.LAVA), 2);
            } else if (resultItem.is(ToolItems.MAGIC_WATER_DROPPER)) {
                return anyMatch(state -> state.getFluidState().is(Tags.Fluids.WATER) || state.is(TFTags.SINKS), 2);
            }
            return true;
        }
    }
}
