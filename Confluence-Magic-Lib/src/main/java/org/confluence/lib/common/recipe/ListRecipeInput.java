package org.confluence.lib.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public class ListRecipeInput implements RecipeInput {
    private final List<ItemStack> items;

    public ListRecipeInput(List<ItemStack> items) {
        this.items = items;
    }

    public ListRecipeInput(List<ItemStack> items, int start, int end) {
        this(items.subList(start, end));
    }

    @Override
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    public int size() {
        return items.size();
    }
}
