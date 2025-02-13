package org.confluence.mod.api.event;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.confluence.mod.common.data.saved.GamePhase;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This event is Server side only.
 */
public abstract class ShimmerItemTransmutationEvent extends Event {
    public static final List<Ingredient> BLACK_LIST = new ArrayList<>();
    public static final List<ItemTransmutation> ITEM_TRANSMUTATION = new ArrayList<>();
    protected final ItemEntity source;
    protected int coolDown;
    protected int shrink = 0;
    protected double speedY;

    public ShimmerItemTransmutationEvent(ItemEntity source) {
        this.source = source;
        this.coolDown = source.lifespan;
        this.speedY = 0.1;
    }

    public ItemEntity getSource() {
        return source;
    }

    /**
     * Shrink item stack's count. Defaults to 0.
     */
    public void setShrink(int count) {
        this.shrink = count;
    }

    public int getShrink() {
        return shrink;
    }

    /**
     * Determines how long could ItemEntity transmutation next time.
     * <p>
     * Defaults to ItemEntity's lifespan(most of the time it is 6000).
     */
    public void setCoolDown(int coolDown) {
        this.coolDown = coolDown;
    }

    public int getCoolDown() {
        return coolDown;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }

    public double getSpeedY() {
        return speedY;
    }

    /**
     * This event fired when an ItemEntity toss in shimmer.
     * <p>
     * This event is {@link ICancellableEvent}
     */
    public static class Pre extends ShimmerItemTransmutationEvent implements ICancellableEvent {
        private int transformTime = 20;

        public Pre(ItemEntity source) {
            super(source);
        }

        /**
         * Determines how long should ItemEntity transmutation before.
         */
        public void setTransformTime(int transformTime) {
            this.transformTime = transformTime;
        }

        public int getTransformTime() {
            return transformTime;
        }
    }

    /**
     * This event fired when an ItemEntity trying to transmutation.
     */
    public static class Post extends ShimmerItemTransmutationEvent {
        private @Nullable List<ItemStack> targets;

        public Post(ItemEntity source) {
            super(source);
        }

        public void setTargets(@Nullable List<ItemStack> targets) {
            this.targets = targets;
        }

        /**
         * Determines what will ItemEntity transmutation to,
         * <p>
         * If targets have not set yet, it will try to generate targets.
         */
        public @Nullable List<ItemStack> getTargets() {
            return targets;
        }
    }

    public static void addItem(TagKey<Item> source, List<ItemStack> target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), target, shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(TagKey<Item> source, Item target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Ingredient source, List<ItemStack> target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(source, target, shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Item source, Item target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Item source, Item target) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), 1, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(TagKey<Item> source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), target, shrink, gamePhase));
    }

    public static void addItem(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(source, target, shrink, gamePhase));
    }

    public static void addItem(Item source, Item target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, gamePhase));
    }

    public static void addItem(Item source, Item target, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), 1, gamePhase));
    }

    public static void addItem(TagKey<Item> source, Item target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, gamePhase));
    }

    public record ItemTransmutation(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) {}

    public static void blackList(Ingredient ingredient) {
        BLACK_LIST.add(ingredient);
    }

    public static void blackList(TagKey<Item> tagKey) {
        BLACK_LIST.add(Ingredient.of(tagKey));
    }

    public static void blackList(Item item) {
        BLACK_LIST.add(Ingredient.of(item));
    }
}
