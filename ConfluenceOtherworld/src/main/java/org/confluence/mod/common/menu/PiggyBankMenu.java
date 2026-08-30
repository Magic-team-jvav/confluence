package org.confluence.mod.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.ExtraInventory;
import org.confluence.mod.common.attachment.PlayerPiggyBankContainer;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;

public class PiggyBankMenu extends AbstractContainerMenu {
    private final Player player;
    private final ContainerLevelAccess access;
    private final int containerRows = 6;

    public PiggyBankMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public PiggyBankMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.PIGGY_BANK.get(), containerId);
        this.player = inventory.player;
        this.access = access;

        int i = (containerRows - 4) * 18;
        PlayerPiggyBankContainer container = getContainer();
        for (int j = 0; j < containerRows; j++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(container, k + j * 9, 8 + k * 18, (j + 1) * 18));
            }
        }
        ExtraInventory extraInventory = getExtraInventory();
        for (int j = 0; j < ExtraInventory.SIZE_COINS; j++) {
            addSlot(new ExtraInventoryMenu.CoinsSlot(extraInventory, j + ExtraInventory.COINS_START, -25, (j + 1) * 18));
        }
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }
        for (int j = 0; j < 9; j++) {
            addSlot(new Slot(inventory, j, 8 + j * 18, 161 + i));
        }
    }

    public ExtraInventory getExtraInventory() {
        return ExtraInventory.of(player);
    }

    public PlayerPiggyBankContainer getContainer() {
        return PlayerPiggyBankContainer.of(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemStack = itemStack1.copy();
            if (index < containerRows * 9) {
                if (!moveItemStackTo(itemStack1, containerRows * 9, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemStack1, 0, containerRows * 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.PIGGY_BANK.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        getContainer().stopOpen(player);
    }

    public int getRowCount() {
        return containerRows;
    }
}
