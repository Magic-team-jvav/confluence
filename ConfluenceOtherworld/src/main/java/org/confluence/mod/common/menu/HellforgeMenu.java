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
import net.minecraft.world.item.ItemStack;
import org.confluence.lib.common.menu.ContainerResultSlot;
import org.confluence.lib.common.menu.ForgeFuelSlot;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.*;

public class HellforgeMenu extends AbstractContainerMenu {
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int INPUT_SLOT_4 = 3;
    public static final int FUEL_SLOT = 4;
    public static final int RESULT_SLOT = 5;
    public static final int SLOT_COUNT = 6;
    public static final int DATA_COUNT = 5;
    private static final int INV_SLOT_START = 6;
    private static final int INV_SLOT_END = 33;
    private static final int USE_ROW_SLOT_START = 33;
    private static final int USE_ROW_SLOT_END = 42;
    private final Container forgeContainer;
    private final ContainerData forgeData;

    public HellforgeMenu(int pContainerId, Inventory inventory) {
        this(pContainerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public HellforgeMenu(int pContainerId, Inventory pPlayerInventory, Container forgeContainer, ContainerData forgeData) {
        super(ModMenuTypes.HELLFORGE.get(), pContainerId);
        checkContainerSize(forgeContainer, SLOT_COUNT);
        checkContainerDataCount(forgeData, DATA_COUNT);
        this.forgeContainer = forgeContainer;
        this.forgeData = forgeData;

        addSlot(new Slot(forgeContainer, INPUT_SLOT_1, 47, 20));
        addSlot(new Slot(forgeContainer, INPUT_SLOT_2, 65, 20));
        addSlot(new Slot(forgeContainer, INPUT_SLOT_3, 47, 38));
        addSlot(new Slot(forgeContainer, INPUT_SLOT_4, 65, 38));
        addSlot(new ForgeFuelSlot(ModRecipes.HELLFORGE_TYPE.get(), forgeContainer, FUEL_SLOT, 16, 52));
        addSlot(new ContainerResultSlot(forgeContainer, RESULT_SLOT, 128, 35));

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(pPlayerInventory, l + k * 9 + 9, 8 + l * 18, 84 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(pPlayerInventory, m, 8 + m * 18, 142));
        }

        addDataSlots(forgeData);
    }

    @Override
    public boolean stillValid(Player player) {
        return forgeContainer.stillValid(player);
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
            } else if (index > FUEL_SLOT) {
                if (isFuel(itemstack1)) {
                    if (!moveItemStackTo(itemstack1, FUEL_SLOT, RESULT_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(itemstack1, INPUT_SLOT_1, FUEL_SLOT, false)) {
                    if (index < INV_SLOT_END) {
                        if (!moveItemStackTo(itemstack1, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < USE_ROW_SLOT_END && !moveItemStackTo(itemstack1, INV_SLOT_START, INV_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
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

    protected boolean isFuel(ItemStack stack) {
        return stack.getBurnTime(ModRecipes.HELLFORGE_TYPE.get()) > 0;
    }

    public float getBurnProgress() {
        int i = forgeData.get(DATA_COOKING_PROGRESS);
        int j = forgeData.get(DATA_COOKING_TOTAL_TIME);
        return j != 0 && i != 0 ? Mth.clamp((float) i / (float) j, 0.0F, 1.0F) : 0.0F;
    }

    public float getLitProgress() {
        int i = forgeData.get(DATA_LIT_DURATION);
        if (i == 0) {
            i = 100;
        }

        return Mth.clamp((float) forgeData.get(DATA_LIT_TIME) / (float) i, 0.0F, 1.0F);
    }

    public boolean isLit() {
        return forgeData.get(DATA_LIT_TIME) > 0;
    }
}
