package org.confluence.mod.common.block.functional.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.common.menu.CrystalBallMenu;
import org.jetbrains.annotations.Nullable;

public class CrystalBallBlock extends Block {
    public CrystalBallBlock(Properties properties) {
        super(properties);
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
