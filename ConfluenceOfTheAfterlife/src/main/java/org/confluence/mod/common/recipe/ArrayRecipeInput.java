package org.confluence.mod.common.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public class ArrayRecipeInput implements RecipeInput {
    private final ItemStack[] itemStacks;

    public ArrayRecipeInput(ItemStack[] itemStacks) {
        this.itemStacks = itemStacks;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return itemStacks[index];
    }

    @Override
    public int size() {
        return itemStacks.length;
    }
}
