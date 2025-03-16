package org.confluence.mod.mixed;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.Collection;

public interface IRecipeManager {
    <I extends RecipeInput, T extends Recipe<I>> Collection<RecipeHolder<T>> confluence$byType(RecipeType<T> type);
}
