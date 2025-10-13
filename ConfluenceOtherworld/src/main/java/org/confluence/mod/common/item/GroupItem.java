package org.confluence.mod.common.item;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.confluence.mod.common.init.ModDataComponentTypes;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.function.Function;

public class GroupItem extends Item {
    private static GroupItem instance;

    public GroupItem() {
        super(new Properties().stacksTo(1));
        if (instance != null) {
            throw new UnsupportedOperationException("Group Item must be singleton instance");
        }
        instance = this;
    }

    public static GroupItem getInstance() {
        return instance;
    }

    public static ItemStack of(ItemStack... stacks) {
        return of(Arrays.asList(stacks));
    }

    public static ItemStack of(List<ItemStack> stacks) {
        ItemStack itemStack = instance.getDefaultInstance();
        itemStack.set(ModDataComponentTypes.GROUP_STACKS, Stacks.of(false, stacks));
        return itemStack;
    }

    public static void toggleVisibility(ItemStack group) {
        Stacks stacks = group.get(ModDataComponentTypes.GROUP_STACKS);
        if (stacks == null) throw new NullPointerException("Stacks must non-null!");
        group.set(ModDataComponentTypes.GROUP_STACKS, stacks.toggleVisibility());
    }

    public static class Stacks implements DataComponentType<Stacks> {
        public static final Stacks EMPTY = new Stacks(false, List.of());
        public static final Codec<Stacks> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.BOOL.fieldOf("visible").forGetter(Stacks::isVisible),
                ItemStack.SINGLE_ITEM_CODEC.listOf().xmap(Stacks::distinct, Function.identity()).fieldOf("values").forGetter(Stacks::getValues)
        ).apply(instance, Stacks::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Stacks> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Stacks::isVisible,
                ItemStack.LIST_STREAM_CODEC, Stacks::getValues,
                Stacks::new
        );

        private final boolean visible;
        private final List<ItemStack> values;

        private Stacks(boolean visible, List<ItemStack> values) {
            this.visible = visible;
            this.values = values;
        }

        public boolean isVisible() {
            return visible;
        }

        public List<ItemStack> getValues() {
            return values;
        }

        @Override
        public Codec<Stacks> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, Stacks> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public final boolean equals(Object o) {
            return o == this || (o instanceof Stacks stacks && stacks.visible == visible && stacks.values.equals(values));
        }

        @Override
        public int hashCode() {
            int result = Boolean.hashCode(visible);
            result = 31 * result + values.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "Stacks{" +
                    "visible=" + visible +
                    ", values=" + values +
                    '}';
        }

        public Stacks toggleVisibility() {
            return new Stacks(!visible, values);
        }

        private static List<ItemStack> distinct(List<ItemStack> stacks) {
            Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
            set.addAll(stacks);
            return Lists.newArrayList(set);
        }

        public static Stacks of(boolean visible, ItemStack... stacks) {
            return of(visible, Arrays.asList(stacks));
        }

        public static Stacks of(boolean visible, List<ItemStack> stacks) {
            return new Stacks(visible, distinct(stacks));
        }
    }

    public static class DisplayItems implements Collection<ItemStack> {
        private final Collection<ItemStack> delegate;

        public DisplayItems(Collection<ItemStack> delegate) {
            this.delegate = delegate;
        }

        @Override
        public int size() {
            return delegate.size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return delegate.contains(o);
        }

        @Override
        public Iterator<ItemStack> iterator() {
            Iterator<ItemStack> unfiltered = delegate.iterator();
            return new AbstractIterator<>() {
                int index = 0;
                List<ItemStack> values = List.of();

                @Override
                @CheckForNull
                protected ItemStack computeNext() {
                    if (index < values.size()) {
                        return values.get(index++);
                    } else if (unfiltered.hasNext()) {
                        ItemStack element = unfiltered.next();
                        if (element.is(instance)) {
                            index = 0;
                            values = element.getOrDefault(ModDataComponentTypes.GROUP_STACKS, Stacks.EMPTY).values;
                            return element;
                        }
                        return element;
                    }
                    return endOfData();
                }
            };
        }

        @Override
        public Object[] toArray() {
            return delegate.toArray();
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return delegate.toArray(ts);
        }

        @Override
        public boolean add(ItemStack e) {
            return delegate.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return delegate.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> collection) {
            return delegate.containsAll(collection);
        }

        @Override
        public boolean addAll(Collection<? extends ItemStack> collection) {
            return delegate.addAll(collection);
        }

        @Override
        public boolean removeAll(Collection<?> collection) {
            return delegate.removeAll(collection);
        }

        @Override
        public boolean retainAll(Collection<?> collection) {
            return delegate.retainAll(collection);
        }

        @Override
        public void clear() {
            delegate.clear();
        }
    }
}
