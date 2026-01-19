package org.confluence.mod.common.component;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RepeaterContents implements TooltipComponent {
    public static final RepeaterContents EMPTY = new RepeaterContents(NonNullList.create(), 64);
    public static final Codec<RepeaterContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Codec.list(ItemStack.CODEC).fieldOf("items").forGetter(RepeaterContents::asItems),
                    Codec.INT.fieldOf("maxItemCapacity").forGetter(RepeaterContents::getMaxItemCapacity))
            .apply(instance, RepeaterContents::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RepeaterContents> STREAM_CODEC = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC, RepeaterContents::asItems,
            ByteBufCodecs.INT, RepeaterContents::getMaxItemCapacity,
            RepeaterContents::new);
    private final int maxItemCapacity;
    private final NonNullList<ItemStack> items;
    private final int hashCode;
    private final boolean isFull;
    private final int itemsTotalCount;
    private final int slotSize;
    private final boolean isEmpty;

    private RepeaterContents(List<ItemStack> stackList, int maxItemCapacity) {
        NonNullList<ItemStack> items;
        if (stackList.isEmpty()) {
            items = NonNullList.withSize(1, ItemStack.EMPTY);
        } else {
            List<ItemStack> list = new ArrayList<>();
            for (ItemStack stack : stackList) {
                if (!stack.isEmpty()) {
                    list.add(stack);
                }
            }
            int size = list.size();
            NonNullList<ItemStack> nullList = NonNullList.withSize(size + 1, ItemStack.EMPTY);
            for (int i = 0; i < size; i++) {
                nullList.set(i, list.get(i).copy());
            }
            items = nullList;
        }
        this.items = items;
        this.maxItemCapacity = maxItemCapacity;
        this.itemsTotalCount = this.items.stream().filter(itemStack -> !itemStack.isEmpty()).mapToInt(ItemStack::getCount).sum();
        this.isEmpty = nonEmptyStream().toList().isEmpty();
        this.isFull = getItemsTotalCount() >= this.getMaxItemCapacity();
        this.slotSize = this.items.size();
        this.hashCode = ItemStack.hashStackList(this.items);
    }

    public static RepeaterContents fromItems(int capacity) {
        return new RepeaterContents(NonNullList.createWithCapacity(1), capacity);
    }

    public static RepeaterContents fromItems(List<ItemStack> items, int capacity) {
        return new RepeaterContents(items, capacity);
    }

    private List<ItemStack> asItems() {
        return items.stream().filter(itemstack -> !itemstack.isEmpty()).collect(Collectors.toList());
    }

    public void copyInto(NonNullList<ItemStack> list) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemstack = this.items.get(i);
            list.set(i, itemstack.copy());
        }
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public boolean isFull() {
        return isFull;
    }

    public int getItemsTotalCount() {
        return itemsTotalCount;
    }

    public int getMaxItemCapacity() {
        return this.maxItemCapacity;
    }

    public int getSlotSize() {
        return slotSize;
    }

    public int getUedSlotSize() {
        return nonEmptyStream().toList().size();
    }

    @Override
    public int hashCode() {
        return hashCode;
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
        if (!(other instanceof RepeaterContents itemcontainercontents)) {
            return false;
        }
        return ItemStack.listMatches(this.items, itemcontainercontents.items);
    }

    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return this.items.get(slot).copy();
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlotSize()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + getSlotSize() + ")");
        }
    }
}
