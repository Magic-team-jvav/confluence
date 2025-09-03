package org.confluence.mod.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.PaintItems;
import org.confluence.mod.common.init.item.VanityArmorItems;

public class DyeMixMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    public final SimpleContainer container;
    private Runnable listener = () -> {};

    public DyeMixMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, ContainerLevelAccess.NULL);
    }

    public DyeMixMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(ModMenuTypes.DYE_MIX.get(), containerId);
        this.access = access;
        this.container = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                DyeMixMenu.this.slotsChanged(this);
            }
        };

        addSlot(new Slot(container, 0, 17, 14) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.RED_DYE) || stack.is(PaintItems.RED_PAINT);
            }
        });
        addSlot(new Slot(container, 1, 17, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.GREEN_DYE) || stack.is(PaintItems.GREEN_PAINT);
            }
        });
        addSlot(new Slot(container, 2, 17, 56) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(VanityArmorItems.BLUE_DYE) || stack.is(PaintItems.BLUE_PAINT);
            }
        });

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }
    }

    public void registerUpdateListener(Runnable listener) {
        this.listener = listener;
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        listener.run();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index > 2) {
                if (!moveItemStackTo(itemstack1, 0, 3, false)) {
                    if (index < 30) {
                        if (!moveItemStackTo(itemstack1, 30, 39, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 39 && !moveItemStackTo(itemstack1, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 2, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, FunctionalBlocks.DYE_VAT.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        access.execute((level, blockPos) -> clearContainer(player, container));
    }
}
