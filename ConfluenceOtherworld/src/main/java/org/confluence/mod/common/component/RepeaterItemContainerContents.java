package org.confluence.mod.common.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class RepeaterItemContainerContents {
    public static final RepeaterItemContainerContents EMPTY = new RepeaterItemContainerContents(NonNullList.create(), 64);
    public static final Codec<RepeaterItemContainerContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(ItemStack.CODEC).fieldOf("items").forGetter(getter -> getter.items),
                    Codec.INT.fieldOf("prudence").forGetter(getter -> getter.capacity))
            .apply(instance, RepeaterItemContainerContents::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RepeaterItemContainerContents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list()), getter -> getter.items,
            ByteBufCodecs.INT, getter -> getter.capacity,
            RepeaterItemContainerContents::new);
    private final int capacity;
    private final NonNullList<ItemStack> items;
    private @Nullable Integer hashCode;

    private RepeaterItemContainerContents(NonNullList<ItemStack> items, int capacity) {
        this.items = items;
        this.capacity = capacity;
    }

    private RepeaterItemContainerContents(int size, int capacity) {
        this(NonNullList.withSize(size, ItemStack.EMPTY), capacity);
    }

    private RepeaterItemContainerContents(List<ItemStack> items, int capacity) {
        this(items.size(), capacity);

        int i = 0;
        while (i < items.size()) {
            ItemStack itemStack = items.get(i);
            if (itemStack == null || itemStack.isEmpty()) {
                continue;
            }
            this.items.set(i, itemStack);
            i++;
        }

        items.removeIf(ItemStack::isEmpty);
    }

    public static RepeaterItemContainerContents fromItems(int capacity) {
        return fromItems(NonNullList.create(), capacity);
    }

    public static RepeaterItemContainerContents fromItems(List<ItemStack> items, int capacity) {
        int i = findLastNonEmptySlot(items);
        if (i == -1) {
            return new RepeaterItemContainerContents(new ArrayList<>(), capacity);
        }

        RepeaterItemContainerContents itemcontainercontents = new RepeaterItemContainerContents(i + 1, capacity);
        ItemStack frontStack = ItemStack.EMPTY;
        for (int j = 0; j <= i; j++) {
            ItemStack copyItemStack = items.get(j).copy();
            if (!frontStack.isEmpty() && frontStack.equals(copyItemStack)) {
                int frontStackCount = frontStack.getCount();
                int count = frontStack.getMaxStackSize() - frontStackCount;
                if (count > 0) {
                    ItemStack split = copyItemStack.split(count);
                    split.grow(frontStackCount);
                    itemcontainercontents.items.set(j - 1, split);
                }
            }
            itemcontainercontents.items.set(j, copyItemStack);
        }
        itemcontainercontents.items.removeIf(ItemStack::isEmpty);
        return itemcontainercontents;
    }

    private static int findLastNonEmptySlot(List<ItemStack> items) {
        for (int i = items.size() - 1; i >= 0; i--) {
            if (!items.get(i).isEmpty()) {
                return i;
            }
        }

        return -1;
    }

    public boolean isEmpty() {
        return nonEmptyStream().toList().isEmpty();
    }

    public void copyInto(NonNullList<ItemStack> list) {
        for (int i = 0; i < list.size(); i++) {
            ItemStack itemstack = i < this.items.size() ? this.items.get(i) : ItemStack.EMPTY;
            list.set(i, itemstack.copy());
        }
    }

    public boolean isFull() {
        return this.getTotalSize() >= this.getMaxCapacity();
    }

    public int getTotalSize() {
        return this.items.stream().filter(itemStack -> !itemStack.isEmpty()).mapToInt(ItemStack::getCount).sum();
    }

    public int getMaxCapacity() {
        return this.capacity;
    }

    public ItemStack copyOne() {
        return this.items.isEmpty() ? ItemStack.EMPTY : this.items.get(0).copy();
    }

    public Stream<ItemStack> stream() {
        return this.items.stream().map(ItemStack::copy);
    }

    public Stream<ItemStack> nonEmptyStream() {
        return this.items.stream().filter(p_331322_ -> !p_331322_.isEmpty()).map(ItemStack::copy);
    }

    public Iterable<ItemStack> nonEmptyItems() {
        return Iterables.filter(this.items, p_331420_ -> !p_331420_.isEmpty());
    }

    public Iterable<ItemStack> nonEmptyItemsCopy() {
        return Iterables.transform(this.nonEmptyItems(), ItemStack::copy);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RepeaterItemContainerContents itemcontainercontents)) {
            return false;
        }
        return ItemStack.listMatches(this.items, itemcontainercontents.items);
    }

    @Override
    public int hashCode() {
        return hashCode == null ? hashCode = ItemStack.hashStackList(this.items) : hashCode;
    }

    public int getSlots() {
        return this.items.size();
    }

    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.items.get(slot).copy();
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlots()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + getSlots() + ")");
        }
    }
}
