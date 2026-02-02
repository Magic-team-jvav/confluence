package org.confluence.terra_furniture.common.menu;

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
import org.confluence.terra_furniture.common.init.TFRegistries;

import static net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity.*;

public class GlassKilnMenu extends AbstractContainerMenu {
    public static final int INPUT_START = 0;
    public static final int INPUT_END = 15;
    public static final int FUEL_SLOT = 16;
    public static final int RESULT_SLOT =17;
    public static final int SLOT_COUNT = 18;
    public static final int DATA_COUNT = 4;
    private static final int INV_SLOT_START = 18;
    private static final int INV_SLOT_END = 45;
    private static final int USE_ROW_SLOT_START = 45;
    private static final int USE_ROW_SLOT_END = 54;
    private final Container forgeContainer;
    private final ContainerData forgeData;

    public GlassKilnMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(DATA_COUNT));
    }

    public GlassKilnMenu(int containerId, Inventory inventory, Container forgeContainer, ContainerData forgeData) {
        super(TFRegistries.GLASS_KILN_MENU.get(), containerId);
        checkContainerSize(forgeContainer, SLOT_COUNT);
        checkContainerDataCount(forgeData, DATA_COUNT);
        this.forgeContainer = forgeContainer;
        this.forgeData = forgeData;

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                addSlot(new Slot(forgeContainer, i * 4 + j, j * 18 + 21, i * 18 + 20));
            }
        }
        addSlot(new ForgeFuelSlot(TFRegistries.GLASS_KILN_RECIPE_TYPE.get(), forgeContainer, FUEL_SLOT, 132, 74));
        addSlot(new ContainerResultSlot(forgeContainer, RESULT_SLOT, 132, 24));

        for (int k = 0; k < 3; k++) {
            for (int l = 0; l < 9; l++) {
                addSlot(new Slot(inventory, l + k * 9 + 9, 8 + l * 18, 108 + k * 18));
            }
        }
        for (int m = 0; m < 9; m++) {
            addSlot(new Slot(inventory, m, 8 + m * 18, 166));
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
                } else if (!moveItemStackTo(itemstack1, INPUT_START, FUEL_SLOT, false)) {
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
        return stack.getBurnTime(TFRegistries.GLASS_KILN_RECIPE_TYPE.get()) > 0;
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
}
