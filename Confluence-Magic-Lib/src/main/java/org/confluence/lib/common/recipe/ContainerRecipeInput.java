package org.confluence.lib.common.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public class ContainerRecipeInput implements RecipeInput {
    private final Container container;

    public ContainerRecipeInput(Container container) {
        this.container = container;
    }

    @Override
    public ItemStack getItem(int index) {
        return container.getItem(index);
    }

    @Override
    public int size() {
        return container.getContainerSize();
    }
}
