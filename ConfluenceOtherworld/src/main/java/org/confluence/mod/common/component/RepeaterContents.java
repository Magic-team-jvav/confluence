package org.confluence.mod.common.component;

import PortLib.extensions.net.minecraft.world.item.ItemStack.PortItemStackExtension;
import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.mesdag.portlib.network.PortRegistryFriendlyByteBuf;
import org.mesdag.portlib.network.codec.PortByteBufCodecs;
import org.mesdag.portlib.network.codec.PortStreamCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class RepeaterContents implements TooltipComponent {
    public static final RepeaterContents EMPTY = new RepeaterContents(NonNullList.create(), 64);
    public static final Codec<RepeaterContents> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.CODEC.listOf().fieldOf("items").forGetter(RepeaterContents::asItems),
            Codec.INT.fieldOf("maxItemCapacity").forGetter(RepeaterContents::getMaxItemCapacity)
    ).apply(instance, RepeaterContents::new));
    public static final PortStreamCodec<PortRegistryFriendlyByteBuf, RepeaterContents> STREAM_CODEC = PortStreamCodec.composite(
            PortItemStackExtension.listStreamCodec(), RepeaterContents::asItems,
            PortByteBufCodecs.INT, RepeaterContents::getMaxItemCapacity,
            RepeaterContents::new
    );
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
        this.itemsTotalCount = items.stream().filter(itemStack -> !itemStack.isEmpty()).mapToInt(ItemStack::getCount).sum();
        this.isEmpty = nonEmptyStream().toList().isEmpty();
        this.isFull = getItemsTotalCount() >= getMaxItemCapacity();
        this.slotSize = items.size();
        this.hashCode = PortItemStackExtension.hashStackList(items);
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
        return items.stream().map(ItemStack::copy);
    }

    public Stream<ItemStack> nonEmptyStream() {
        return items.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy);
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
        if (!(other instanceof RepeaterContents contents)) {
            return false;
        }
        return PortItemStackExtension.listMatches(items, contents.items);
    }

    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        return items.get(slot).copy();
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= getSlotSize()) {
            throw new UnsupportedOperationException("Slot " + slot + " not in valid range - [0," + getSlotSize() + ")");
        }
    }
}
