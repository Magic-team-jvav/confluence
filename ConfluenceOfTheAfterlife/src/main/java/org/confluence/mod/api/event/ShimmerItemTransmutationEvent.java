package org.confluence.mod.api.event;

import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.confluence.mod.common.data.saved.ConfluenceData;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModTags;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
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
            if (targets == null) {
                ItemStack sourceItem = source.getItem();
                for (Ingredient ingredient : BLACK_LIST) {
                    if (ingredient.test(sourceItem)) {
                        return null;
                    }
                }

                ConfluenceData data = ConfluenceData.get((ServerLevel) source.level());
                int ordinal = data.getGamePhase().ordinal();
                for (ItemTransmutation transmutation : ITEM_TRANSMUTATION) {
                    if (ordinal < transmutation.gamePhase.ordinal()) continue;
                    if (transmutation.source.test(sourceItem)) {
                        int times = sourceItem.getCount() / transmutation.shrink;
                        List<ItemStack> results = new ArrayList<>();
                        for (ItemStack result : transmutation.target) {
                            int count = result.getCount() * times;
                            int maxStackSize = result.getMaxStackSize();
                            while (count > maxStackSize) {
                                ItemStack copy = result.copy();
                                copy.setCount(maxStackSize);
                                results.add(copy);
                                count -= maxStackSize;
                            }
                            result.setCount(count);
                            results.add(result);
                        }
                        this.shrink = transmutation.shrink * times;
                        this.targets = results;
                        return targets;
                    }
                }
                if (sourceItem.getDamageValue() != 0) return null;
                RegistryAccess registryAccess = source.level().registryAccess();
                boolean isHardCore = data.isHardcore();
                RandomSource random = source.level().random;
                for (RecipeHolder<?> recipeHolder : ((ServerLevel) source.level()).getServer().getRecipeManager().getRecipes()) {
                    Recipe<?> recipe = recipeHolder.value();
                    if (recipe.isSpecial() || recipe.isIncomplete() || recipe instanceof AbstractCookingRecipe) continue;
                    ItemStack resultItem = recipe.getResultItem(registryAccess);
                    if (sourceItem.getCount() >= resultItem.getCount() && ItemStack.isSameItem(sourceItem, resultItem)) {
                        int times = sourceItem.getCount() / resultItem.getCount();
                        List<ItemStack> results = new ArrayList<>();
                        for (Ingredient ingredient : recipe.getIngredients()) {
                            ItemStack[] itemStacks = ingredient.getItems();
                            if (itemStacks.length == 0 || Arrays.stream(itemStacks).allMatch(itemStack -> itemStack.is(ModTags.Items.HARDMODE))) {
                                continue;
                            }
                            ItemStack input = itemStacks[random.nextInt(itemStacks.length)];
                            while (!isHardCore && input.is(ModTags.Items.HARDMODE)) {
                                input = itemStacks[random.nextInt(itemStacks.length)];
                            }
                            ItemStack result = input.copy();
                            if (result.getItem().hasCraftingRemainingItem(result)) continue;
                            int count = result.getCount() * times;
                            int maxStackSize = result.getMaxStackSize();
                            while (count > maxStackSize) {
                                ItemStack copy = result.copy();
                                copy.setCount(maxStackSize);
                                results.add(copy);
                                count -= maxStackSize;
                            }
                            result.setCount(count);
                            results.add(result);
                        }
                        if (results.isEmpty()) continue;
                        this.shrink = resultItem.getCount() * times;
                        this.targets = results;
                        return targets;
                    }
                }
                return null;
            }
            return targets;
        }
    }

    public static void addItem(TagKey<Item> source, List<ItemStack> target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), target, shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(TagKey<Item> source, Item target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Ingredient source, List<ItemStack> target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(source, target, shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Item source, Item target, int shrink) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), shrink, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(Item source, Item target) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), 1, GamePhase.BEFORE_SKELETRON));
    }

    public static void addItem(TagKey<Item> source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), target, shrink, gamePhase));
    }

    public static void addItem(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(source, target, shrink, gamePhase));
    }

    public static void addItem(Item source, Item target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), shrink, gamePhase));
    }

    public static void addItem(Item source, Item target, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), 1, gamePhase));
    }

    public static void addItem(TagKey<Item> source, Item target, int shrink, GamePhase gamePhase) {
        ITEM_TRANSMUTATION.add(new ItemTransmutation(Ingredient.of(source), Collections.singletonList(new ItemStack(target)), shrink, gamePhase));
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
