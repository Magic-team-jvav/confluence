package org.confluence.mod.common.item;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.mixed.IClientItemStack;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static ItemStack of(Component name, ItemStack... stacks) {
        return of(name, Arrays.asList(stacks));
    }

    public static ItemStack of(Component name, List<ItemStack> stacks) {
        ItemStack itemStack = instance.getDefaultInstance();
        itemStack.set(ModDataComponentTypes.GROUP_STACKS, Stacks.of(false, stacks));
        itemStack.set(DataComponents.CUSTOM_NAME, name);
        return itemStack;
    }

    public static void toggleVisibility(ItemStack group) {
        Stacks stacks = group.get(ModDataComponentTypes.GROUP_STACKS);
        if (stacks == null) throw new NullPointerException("Stacks must non-null!");
        group.set(ModDataComponentTypes.GROUP_STACKS, stacks.toggleVisibility());
    }

    public static class Stacks implements DataComponentType<Stacks> {
        public static final Stacks EMPTY = new Stacks(false, List.of());
        public static final Codec<Stacks> CODEC = Codec.unit(EMPTY);
        public static final StreamCodec<ByteBuf, Stacks> STREAM_CODEC = StreamCodec.unit(EMPTY);
        private static final AtomicInteger cachedId = new AtomicInteger();

        public transient long lastRenderTime;
        public transient int lastRenderIndex;
        public transient int id = -1;

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
        public StreamCodec<ByteBuf, Stacks> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public final boolean equals(Object o) {
            return o == this || (o instanceof Stacks stacks && stacks.visible == visible && stacks.id == id && stacks.values.equals(values));
        }

        @Override
        public int hashCode() {
            int result = Boolean.hashCode(visible);
            result = 31 * result + id;
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
            Stacks stacks = new Stacks(!visible, values);
            stacks.lastRenderTime = lastRenderTime;
            stacks.lastRenderIndex = lastRenderIndex;
            stacks.id = id;
            return stacks;
        }

        public static Stacks of(boolean visible, ItemStack... stacks) {
            return of(visible, Arrays.asList(stacks));
        }

        public static Stacks of(boolean visible, List<ItemStack> stacks) {
            int id = cachedId.getAndIncrement();
            Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
            for (ItemStack stack : stacks) {
                if (LibUtils.isPhysicalClient()) {
                    IClientItemStack.of(stack).confluence$setGroupId(id);
                }
                set.add(stack);
            }
            Stacks stacks1 = new Stacks(visible, Lists.newArrayList(set));
            stacks1.id = id;
            return stacks1;
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
                            Stacks stacks = element.getOrDefault(ModDataComponentTypes.GROUP_STACKS, Stacks.EMPTY);
                            values = stacks.visible ? stacks.values : List.of();
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
