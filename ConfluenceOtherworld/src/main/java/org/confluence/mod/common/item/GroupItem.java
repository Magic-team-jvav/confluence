package org.confluence.mod.common.item;

import com.google.common.collect.AbstractIterator;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenCustomHashSet;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import org.confluence.lib.util.LibUtils;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.api.event.CustomGroupItemIconEvent;
import org.confluence.mod.common.init.ModDataComponentTypes;
import org.confluence.mod.common.init.ModTabs;
import org.confluence.mod.mixed.IClientItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Unmodifiable;

import javax.annotation.CheckForNull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GroupItem extends Item {
    private static GroupItem instance;

    @ApiStatus.Internal
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

    public static boolean isInvalidCreativeModeTab(ResourceKey<CreativeModeTab> tabKey) {
        return tabKey == CreativeModeTabs.SEARCH || tabKey == ModTabs.DEVELOPER.getKey();
    }

    public static boolean isInvalidCreativeModeTab(CreativeModeTab tab) {
        return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).map(GroupItem::isInvalidCreativeModeTab).orElse(false);
    }

    public static ItemStack of(ResourceLocation name, ItemStack... stacks) {
        return of(name, Arrays.asList(stacks));
    }

    public static ItemStack of(ResourceLocation name, List<ItemStack> stacks) {
        ItemStack itemStack = getInstance().getDefaultInstance();
        itemStack.set(ModDataComponentTypes.GROUP_STACKS, Stacks.of(name, false, stacks));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("itemGroup." + name.getNamespace() + "." + name.getPath()));
        return itemStack;
    }

    @ApiStatus.Internal
    public static void toggleVisibility(ItemStack group) {
        Stacks stacks = group.get(ModDataComponentTypes.GROUP_STACKS);
        if (stacks == null) throw new NullPointerException("Stacks must non-null!");
        group.set(ModDataComponentTypes.GROUP_STACKS, stacks.toggleVisibility());
    }

    public static class Stacks implements DataComponentType<Stacks> {
        public static final Stacks EMPTY = new Stacks(Confluence.asResource("empty"), false, new ItemStack[0], -1);
        public static final Codec<Stacks> CODEC = Codec.unit(EMPTY);
        public static final StreamCodec<ByteBuf, Stacks> STREAM_CODEC = StreamCodec.unit(EMPTY);
        private static final AtomicInteger cachedId = new AtomicInteger();

        private transient ObjectLinkedOpenCustomHashSet<ItemStack> duplicateChecker;
        private transient int lastIndex;
        private transient long lastAdvanceTime;
        private transient ItemStack iconStack;

        private transient final ResourceLocation name;
        private transient final boolean visible;
        private transient final ItemStack[] values;
        private transient final int id;

        private Stacks(ResourceLocation name, boolean visible, ItemStack[] values, int id) {
            this.name = name;
            this.visible = visible;
            this.values = values;
            this.id = id;
            if (LibUtils.isPhysicalClient()) {
                for (ItemStack stack : values) {
                    IClientItemStack.of(stack).confluence$setGroupId(id);
                }
            }
        }

        public ResourceLocation getName() {
            return name;
        }

        public boolean isVisible() {
            return visible;
        }

        @Unmodifiable
        public ItemStack[] getValues() {
            return values;
        }

        @ApiStatus.Internal
        public ItemStack getCurrentRendered(long currentAdvanceTime) {
            if (iconStack != null) {
                return iconStack;
            }
            int length = values.length;
            if (length == 0) {
                return ItemStack.EMPTY;
            }
            if (length == 1) {
                return values[0];
            }
            if (currentAdvanceTime != lastAdvanceTime) {
                this.lastAdvanceTime = currentAdvanceTime;
                if (++lastIndex >= length) {
                    this.lastIndex = 0;
                }
            }
            return values[lastIndex];
        }

        public int getId() {
            return id;
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
            return o == this || (o instanceof Stacks stacks && stacks.visible == visible && stacks.id == id && stacks.name.equals(name) && stacks.values.equals(values));
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + Arrays.hashCode(values);
            result = 31 * result + Boolean.hashCode(visible);
            result = 31 * result + id;
            return result;
        }

        @Override
        public String toString() {
            return "Stacks{" +
                    "name=" + name +
                    ", visible=" + visible +
                    ", values=" + Arrays.toString(values) +
                    ", id=" + id +
                    '}';
        }

        @ApiStatus.Internal
        public Stacks toggleVisibility() {
            Stacks data = new Stacks(name, !visible, values, id == -1 ? cachedId.getAndIncrement() : id);
            if (iconStack != null) {
                data.iconStack = iconStack.copy();
            }
            return data;
        }

        @ApiStatus.Internal
        public Stacks withValues(ResourceKey<CreativeModeTab> tabKey, ItemStack... stacks) {
            if (stacks.length == 0) return this;
            ItemStack[] src = new ItemStack[values.length + stacks.length];
            System.arraycopy(values, 0, src, 0, values.length);
            int i = 0;
            for (ItemStack stack : stacks) {
                if (duplicateChecker == null) {
                    this.duplicateChecker = new ObjectLinkedOpenCustomHashSet<>(values, ItemStackLinkedSet.TYPE_AND_TAG);
                }
                if (duplicateChecker.contains(stack)) continue;
                src[values.length + i] = stack;
                duplicateChecker.add(stack);
                ++i;
            }
            ItemStack[] dest = new ItemStack[values.length + i];
            System.arraycopy(src, 0, dest, 0, dest.length);
            Stacks data = new Stacks(name, visible, dest, id == -1 ? cachedId.getAndIncrement() : id);
            data.duplicateChecker = duplicateChecker.clone();
            data.iconStack = iconStack == null ? CustomGroupItemIconEvent.getIcon(tabKey, name) : iconStack.copy();
            return data;
        }

        public static Stacks of(ResourceLocation name, boolean visible, ItemStack... stacks) {
            return of(name, visible, Arrays.asList(stacks));
        }

        public static Stacks of(ResourceLocation name, boolean visible, List<ItemStack> stacks) {
            int id = cachedId.getAndIncrement();
            Set<ItemStack> set = ItemStackLinkedSet.createTypeAndComponentsSet();
            for (ItemStack stack : stacks) {
                if (LibUtils.isPhysicalClient()) {
                    IClientItemStack.of(stack).confluence$setGroupId(id);
                }
                set.add(stack);
            }
            return new Stacks(name, visible, set.toArray(ItemStack[]::new), id);
        }
    }

    public static BelongsTo belongsTo(String path) {
        return new BelongsTo(Confluence.asResource(path));
    }

    public static CreativeModeTab.Output belongsTo(String path, CreativeModeTab.Output output) {
        if (StartupConfigs.itemGroups()) {
            return belongsTo(belongsTo(path), output);
        }
        return output;
    }

    public static CreativeModeTab.Output belongsTo(BelongsTo belongsTo, CreativeModeTab.Output output) {
        if (StartupConfigs.itemGroups()) {
            return (stack, tabVisibility) -> {
                stack.set(ModDataComponentTypes.BELONGS_TO_GROUP, belongsTo);
                output.accept(stack, tabVisibility);
            };
        }
        return output;
    }

    public record BelongsTo(ResourceLocation name) implements DataComponentType<BelongsTo> {
        public static final Codec<BelongsTo> CODEC = ResourceLocation.CODEC.xmap(BelongsTo::new, BelongsTo::name);
        public static final StreamCodec<ByteBuf, BelongsTo> STREAM_CODEC = ResourceLocation.STREAM_CODEC.map(BelongsTo::new, BelongsTo::name);

        @Override
        public Codec<BelongsTo> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<? super RegistryFriendlyByteBuf, BelongsTo> streamCodec() {
            return STREAM_CODEC;
        }

        @Override
        public boolean equals(Object o) {
            return o == this || (o instanceof BelongsTo(ResourceLocation rl) && rl.equals(name));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    @ApiStatus.Internal
    public record DisplayItems(Collection<ItemStack> delegate) implements Collection<ItemStack> {
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
                ItemStack[] values = new ItemStack[0];

                @Override
                @CheckForNull
                protected ItemStack computeNext() {
                    if (index < values.length) {
                        return values[index++];
                    } else if (unfiltered.hasNext()) {
                        ItemStack element = unfiltered.next();
                        if (element.is(getInstance())) {
                            index = 0;
                            Stacks stacks = element.getOrDefault(ModDataComponentTypes.GROUP_STACKS, Stacks.EMPTY);
                            values = stacks.visible ? stacks.values : new ItemStack[0];
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
