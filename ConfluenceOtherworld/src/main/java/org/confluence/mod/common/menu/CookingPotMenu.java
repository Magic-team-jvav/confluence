package org.confluence.mod.common.menu;

import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.confluence.lib.common.menu.ContainerResultSlot;
import org.confluence.mod.common.init.ModMenuTypes;

public class CookingPotMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int INPUT_SLOT_4 = 3;
    public static final int CONTAINER_SLOT = 4;
    public static final int RESULT_SLOT = 5;
    public static final int SLOT_COUNT = 6;
    public static final int DATA_COUNT = 3;
    private static final int INV_SLOT_START = 6;
    private static final int INV_SLOT_END = 33;
    private static final int USE_ROW_SLOT_START = 33;
    private static final int USE_ROW_SLOT_END = 42;
    private final Container potContainer;
    private final ContainerData potData;
    private int cachedId = Item.getId(Items.AIR);
    private ItemStack cachedHeatSource = new ItemStack(Items.AIR);

    public CookingPotMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public CookingPotMenu(int containerId, Inventory inventory, Container potContainer, ContainerData potData) {
        super(ModMenuTypes.COOKING_POT.get(), containerId);
        checkContainerSize(potContainer, SLOT_COUNT);
        checkContainerDataCount(potData, DATA_COUNT);
        this.potContainer = potContainer;
        this.potData = potData;

        addSlot(new Slot(potContainer, INPUT_SLOT_1, 26, 26));
        addSlot(new Slot(potContainer, INPUT_SLOT_2, 44, 26));
        addSlot(new Slot(potContainer, INPUT_SLOT_3, 26, 44));
        addSlot(new Slot(potContainer, INPUT_SLOT_4, 44, 44));
        addSlot(new Slot(potContainer, CONTAINER_SLOT, 92, 20));
        addSlot(new ContainerResultSlot(potContainer, RESULT_SLOT, 134, 35));

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 142));
        }

        addDataSlots(potData);
    }

    @Override
    public boolean stillValid(Player player) {
        return potContainer.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == RESULT_SLOT) {
                if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (index > CONTAINER_SLOT) {
                if (!moveItemStackTo(itemstack1, INPUT_SLOT_1, CONTAINER_SLOT, false)) {
                    if (index < INV_SLOT_END) {
                        if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < USE_ROW_SLOT_END && !moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                    return ItemStack.EMPTY;
                } else if (!moveItemStackTo(itemstack1, CONTAINER_SLOT, RESULT_SLOT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
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

    public float getBurnProgress() {
        int i = potData.get(0);
        int j = potData.get(1);
        return j != 0 && i != 0 ? Mth.clamp((float) i / (float) j, 0.0F, 1.0F) : 0.0F;
    }

    public ItemStack getHeatSourceItem() {
        int i = potData.get(2);
        if (cachedId != i) {
            this.cachedHeatSource = Item.byId(i).getDefaultInstance();
            this.cachedId = i;
        }
        return cachedHeatSource;
    }
}
