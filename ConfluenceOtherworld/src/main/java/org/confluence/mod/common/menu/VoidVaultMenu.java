package org.confluence.mod.common.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.attachment.PlayerVoidVaultContainer;
import org.confluence.mod.common.init.ModMenuTypes;

import java.util.function.Predicate;

public class VoidVaultMenu extends AbstractContainerMenu {
    private final PlayerVoidVaultContainer container;
    private final Predicate<Player> access;

    public VoidVaultMenu(int id, Inventory inventory) {
        this(id, inventory, player -> true);
    }

    public VoidVaultMenu(int id, Inventory inventory, Predicate<Player> access) {
        super(ModMenuTypes.VOID_VAULT.get(), id);
        this.container = PlayerVoidVaultContainer.of(inventory.player);
        this.access = access;
        container.startOpen(inventory.player);
        for (int row = 0; row < PlayerVoidVaultContainer.ROWS; row++) {
            for (int column = 0; column < PlayerVoidVaultContainer.COLUMNS; column++) {
                addSlot(new Slot(container, row * PlayerVoidVaultContainer.COLUMNS + column, 19 + column * 18, 25 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, 9 + row * 9 + column, 19 + column * 18, 147 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 19 + column * 18, 205));
        }
    }

    public static void open(Player player, Predicate<Player> access) {
        player.openMenu(new SimpleMenuProvider((id, inventory, owner) -> new VoidVaultMenu(id, inventory, access),
                Component.translatable("container.confluence.void_vault")));
    }

    @Override
    public boolean stillValid(Player player) {
        return access.test(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if (index < 0 || index >= slots.size()) return ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack source = slot.getItem();
        ItemStack original = source.copy();
        if (index < PlayerVoidVaultContainer.SIZE) {
            if (!moveItemStackTo(source, PlayerVoidVaultContainer.SIZE, slots.size(), true))
                return ItemStack.EMPTY;
        } else if (!moveItemStackTo(source, 0, PlayerVoidVaultContainer.SIZE, false)) {
            return ItemStack.EMPTY;
        }
        if (source.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        slot.onTake(player, source);
        return original;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        container.stopOpen(player);
    }
}
