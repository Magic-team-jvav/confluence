package org.confluence.mod.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.common.init.ModMenuTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FletchingMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final Player player;

    public FletchingMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, ContainerLevelAccess.NULL);
    }

    public FletchingMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModMenuTypes.FLETCHING.get(), pContainerId);
        this.player = pPlayerInventory.player;
        this.access = pAccess;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(access, player, Blocks.FLETCHING_TABLE);
    }

    public static class Provider implements MenuProvider {
        private final Level level;
        private final BlockPos pos;

        public Provider(Level level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }

        @Override
        public @NotNull Component getDisplayName() {
            return Component.translatable("container.confluence.fletching");
        }

        @Override
        public @Nullable AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory, @NotNull Player player) {
            return new SkyMillMenu(containerId, inventory, ContainerLevelAccess.create(level, pos));
        }
    }
}
