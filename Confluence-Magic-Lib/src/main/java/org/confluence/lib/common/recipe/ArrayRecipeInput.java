package org.confluence.lib.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ArrayRecipeInput implements RecipeInput {
    private final ItemStack[] itemStacks;

    public ArrayRecipeInput(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public ItemStack getItem(int index) {
        return itemStacks[index];
    }

    @Override
    public int size() {
        return itemStacks.length;
    }
}
