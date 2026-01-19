package org.confluence.mod.util;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.MutableDataComponentHolder;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.confluence.mod.common.component.RepeaterItemContainerContents;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public class RepeaterItemComponentHandler implements IItemHandlerModifiable {
    protected final MutableDataComponentHolder parent;
    protected final DataComponentType<RepeaterItemContainerContents> component;
    protected final int capacity;

    public RepeaterItemComponentHandler(MutableDataComponentHolder parent, DataComponentType<RepeaterItemContainerContents> component, int capacity) {
        this.parent = parent;
        this.component = component;
        this.capacity = capacity;
    }

    public static boolean insertItem(ItemStack weapon, ItemStack stack) {
        return insertItem(weapon, () -> stack);
    }

    public static boolean insertItem(ItemStack weapon, Supplier<ItemStack> stackConsumer) {
        if (!(weapon.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterItemComponentHandler handler)) {
            return false;
        }

        ItemStack stack = stackConsumer.get();
        if (stack.isEmpty()) {
            return false;
        }

        if (handler.getContents().isFull()) {
            return false;
        }

        int j = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            stack = stackConsumer.get();
            if (stack.isEmpty()) {
                break;
            }

            if (!handler.isItemValid(i, stack)) {
                continue;
            }

            ItemStack insertedItem = handler.insertItem(i, stack, false);
            if (insertedItem == stack) {
                continue;
            }

            if (insertedItem.isEmpty()) {
                continue;
            }

            stack.setCount(insertedItem.getCount());
            j++;
        }

        return j > 0;
    }

    public static List<ItemStack> extractItem(ItemStack weapon, int amount) {
        List<ItemStack> result = NonNullList.create();
        if (!(weapon.getCapability(Capabilities.ItemHandler.ITEM) instanceof RepeaterItemComponentHandler handler)) {
            return result;
        }
        if (handler.getContents().isEmpty()) {
            return result;
        }
        int j = 0;

        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack insertedItem = handler.extractItem(i, amount - j, false);
            if (insertedItem.isEmpty()) {
                continue;
            }
            j += insertedItem.getCount();
            result.add(insertedItem);
            if (j >= amount) {
                return result;
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
    public ItemStack getStackInSlot(int slot) {
        return this.getStackFromContents(this.getContents(), slot);
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        if (!this.isItemValid(slot, stack)) {
            throw new RuntimeException("Invalid stack " + stack + " for slot " + slot + ")");
        }
        RepeaterItemContainerContents contents = this.getContents();
        ItemStack existing = this.getStackFromContents(contents, slot);
        if (!ItemStack.matches(stack, existing)) {
            this.updateContents(contents, stack, slot);
        }
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack toInsert, boolean simulate) {
        this.validateSlotIndex(slot);

        if (toInsert.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!this.isItemValid(slot, toInsert)) {
            return toInsert;
        }

        RepeaterItemContainerContents contents = this.getContents();
        ItemStack existing = this.getStackFromContents(contents, slot);
        // Max amount of the stack that could be inserted
        int insertLimit = Math.min(contents.getTotalSize(), Math.min(this.getSlotLimit(slot), toInsert.getMaxStackSize()));

        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(toInsert, existing)) {
                return toInsert;
            }

            insertLimit -= existing.getCount();
        }

        if (insertLimit <= 0) {
            return toInsert;
        }

        int inserted = Math.min(insertLimit, toInsert.getCount());

        if (!simulate) {
            this.updateContents(contents, toInsert.copyWithCount(existing.getCount() + inserted), slot);
        }

        return toInsert.copyWithCount(toInsert.getCount() - inserted);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        this.validateSlotIndex(slot);

        if (amount == 0) {
            return ItemStack.EMPTY;
        }

        RepeaterItemContainerContents contents = this.getContents();
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
        return getStackInSlot(slot).getMaxStackSize();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return this.getContents().isFull() && stack.canFitInsideContainerItems();
    }

    protected void onContentsChanged(int slot, ItemStack oldStack, ItemStack newStack) {}

    protected RepeaterItemContainerContents getContents() {
        return this.parent.getOrDefault(this.component, RepeaterItemContainerContents.EMPTY);
    }

    protected ItemStack getStackFromContents(RepeaterItemContainerContents contents, int slot) {
        this.validateSlotIndex(slot);
        return contents.getSlots() <= slot ? ItemStack.EMPTY : contents.getStackInSlot(slot);
    }

    protected void updateContents(RepeaterItemContainerContents contents, ItemStack stack, int slot) {
        this.validateSlotIndex(slot);
        NonNullList<ItemStack> list = NonNullList.withSize(Math.max(contents.getSlots(), this.getSlots()), ItemStack.EMPTY);
        contents.copyInto(list);
        ItemStack oldStack = list.get(slot);
        list.set(slot, stack);
        this.parent.set(this.component, RepeaterItemContainerContents.fromItems(list, capacity));
        this.onContentsChanged(slot, oldStack, stack);
        list.removeIf(ItemStack::isEmpty);
    }

    protected final void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }
}
