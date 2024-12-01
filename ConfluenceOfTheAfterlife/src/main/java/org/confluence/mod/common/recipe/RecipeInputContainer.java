package org.confluence.mod.common.recipe;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeInput;

public class RecipeInputContainer extends SimpleContainer implements RecipeInput {
    public RecipeInputContainer(int size) {
        super(size);
    }

    @Override
    public int size() {
        return getContainerSize();
    }
}
