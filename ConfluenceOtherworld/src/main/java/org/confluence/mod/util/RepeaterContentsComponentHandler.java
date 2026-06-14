package org.confluence.mod.util;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import net.minecraft.core.NonNullList;
import org.mesdag.portlib.component.PortDataComponentType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.confluence.mod.common.component.RepeaterContents;
import org.confluence.mod.common.item.crossbow.BaseTerraRepeaterItem;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class RepeaterContentsComponentHandler implements IItemHandlerModifiable {
    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<RepeaterContents> component;
    protected final int capacity;

    public RepeaterContentsComponentHandler(MutableDataComponentHolder parent, DataComponentType<RepeaterContents> component, int capacity) {
        this.parent = parent;
        this.component = component;
        this.capacity = capacity;
    }

    /**
     * 此方法会修改输入的物品
     */
    public static boolean insertItem(ItemStack weapon, ItemStack stack, boolean isInfiniteResources) {
        return insertItem(weapon, () -> stack, isInfiniteResources);
    }

    /**
     * 此方法会修改输入的物品
     */
    public static boolean insertItem(ItemStack weapon, Supplier<ItemStack> stackConsumer, boolean isInfiniteResources) {
        return insertItem(weapon, stackConsumer, (originalStack, remainingAfterInsert) ->
                originalStack.setCount(remainingAfterInsert.getCount()), isInfiniteResources);
    }

    public static boolean insertItem(ItemStack weapon, Supplier<ItemStack> stackConsumer, BiConsumer<ItemStack, ItemStack> operation, boolean isInfiniteResources) {
        if (!(weapon.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler)) {
            return false;
        }

        return handler.insertItem(stackConsumer, operation, isInfiniteResources);
    }

    /**
     * 此方法会修改输入的物品
     */
    public boolean insertItem(Supplier<ItemStack> stackConsumer, boolean isInfiniteResources) {
        return insertItem(stackConsumer, (originalStack, remainingAfterInsert) ->
                originalStack.setCount(remainingAfterInsert.getCount()), isInfiniteResources);
    }

    public boolean insertItem(Supplier<ItemStack> stackConsumer, BiConsumer<ItemStack, ItemStack> operation, boolean isInfiniteResources) {
        int itemsInserted = 0;

        int i = 0;
        ItemStack originalStack = null;
        while (!getContents().isFull() && i < 2) {
            ItemStack currentStack = stackConsumer.get();
            if (originalStack != null && originalStack == currentStack) {
                i++;
            } else {
                originalStack = currentStack;
            }

            if (originalStack.isEmpty()) {
                break;
            }

            if (!isItemValid(originalStack)) {
                break;
            }

            for (int j = 0; j < getSlots(); j++) {
                ItemStack stackToInsert = isInfiniteResources ? originalStack.copy() : originalStack;
                if (isInfiniteResources) {
                    stackToInsert.setCount(stackToInsert.getMaxStackSize());
                }

                if (!isItemValid(j, stackToInsert)) {
                    continue;
                }

                ItemStack remainingAfterInsert = insertItem(j, stackToInsert, false);
                ItemStack itemStack = getStackInSlot(j);
                if (remainingAfterInsert == stackToInsert) {
                    if (itemStack.isEmpty()) {
                        break;
                    }
                    continue;
                }

                if (!isInfiniteResources) {
                    operation.accept(stackToInsert, remainingAfterInsert);
                }

                itemsInserted++;
                if (remainingAfterInsert.isEmpty()) {
                    break;
                }
            }
        }
        return itemsInserted > 0;
    }

    public static List<ItemStack> extractItemList(ItemStack weapon, int amount, boolean isSimulate) {
        List<ItemStack> result = NonNullList.create();
        if (!(weapon.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterContentsComponentHandler handler) || handler.getContents().isEmpty()) {
            return result;
        }

        int j = 0;
        int slots = handler.getSlots();
        for (int i = 0; i < slots; i++) {
            ItemStack insertedItem = handler.extractItem(i, amount - j, isSimulate);
            if (insertedItem.isEmpty()) {
                continue;
            }
            j += insertedItem.getCount();
            result.add(insertedItem);
            if (j >= amount) {
                break;
            }
        }

        return result;
    }

    public Iterator<ItemStack> getAllItemIterator() {
        return getContents().nonEmptyItemsCopy().iterator();
    }

    public boolean isEmpty() {
        return this.getContents().isEmpty();
    }

    @Override
    public int getSlots() {
        return this.getContents().stream().toList().size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.getStackFromContents(this.getContents(), slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        if (!this.isItemValid(slot, stack)) {
            throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
        }
        RepeaterContents contents = this.getContents();
        ItemStack existing = this.getStackFromContents(contents, slot);
        if (!ItemStack.matches(stack, existing)) {
            this.updateContents(contents, stack, slot);
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack toInsert, boolean simulate) {
        this.validateSlotIndex(slot);

        if (toInsert.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!this.isItemValid(slot, toInsert)) {
            return toInsert;
        }

        RepeaterContents contents = this.getContents();
        ItemStack existing = this.getStackFromContents(contents, slot);
        int maxInsertLimit = existing.isEmpty() ? Math.min(contents.getMaxItemCapacity() - contents.getItemsTotalCount(), toInsert.getCount()) : Math.min(this.getSlotLimit(slot), toInsert.getMaxStackSize());

        if (!existing.isEmpty()) {
            if (!PortItemStackExtension.isSameItemSameComponents(toInsert, existing)) {
                return toInsert;
            }

//            maxInsertLimit -= existing.getCount();
        }

        if (maxInsertLimit <= 0) {
            return toInsert;
        }

        int inserted = Math.min(maxInsertLimit, toInsert.getCount());

        if (!simulate) {
            this.updateContents(contents, toInsert.copyWithCount(existing.getCount() + inserted), slot);
        }

        ItemStack stack = toInsert.copyWithCount(toInsert.getCount() - inserted);
        return stack.isEmpty() ? ItemStack.EMPTY : stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        this.validateSlotIndex(slot);

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        RepeaterContents contents = this.getContents();
        ItemStack existing = this.getStackFromContents(contents, slot);

        if (existing.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int toExtract = Math.min(amount, existing.getCount());

        if (!simulate) {
            this.updateContents(contents, existing.copyWithCount(existing.getCount() - toExtract), slot);
        }

        return existing.copyWithCount(toExtract);
    }

    @Override
    public int getSlotLimit(int slot) {
        RepeaterContents contents = getContents();
        return Math.max(0, Math.min(getStackInSlot(slot).getMaxStackSize(), contents.getMaxItemCapacity() - contents.getItemsTotalCount()));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isItemValid(stack);
    }

    public boolean isItemValid(@NotNull ItemStack stack) {
        final Supplier<Boolean> is = () -> !this.getContents().isFull() &&
                stack.canFitInsideContainerItems();
        return parent instanceof ItemStack itemStack ?
                itemStack.getItem() instanceof BaseTerraRepeaterItem repeaterItem && repeaterItem.getAllSupportedProjectiles(itemStack).test(stack) && is.get() :
                is.get();
    }

    protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {}

    public RepeaterContents getContents() {
        return this.parent.getOrDefault(this.component, RepeaterContents.EMPTY);
    }

    protected ItemStack getStackFromContents(RepeaterContents contents, int slot) {
        this.validateSlotIndex(slot);
        return contents.getSlotSize() <= slot ? ItemStack.EMPTY : contents.getStackInSlot(slot);
    }

    protected void updateContents(RepeaterContents contents, ItemStack stack, int slot) {
        this.validateSlotIndex(slot);
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlotSize(), this.getSlots()), ItemStack.EMPTY);
        ItemStack oldStack = list.get(slot);
        contents.copyInto(list);
        list.set(slot, stack);
        this.parent.set(this.component, RepeaterContents.fromItems(list, this.capacity));
        this.onContentsChanged(slot, oldStack, stack);
    }

    protected final void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }
}
