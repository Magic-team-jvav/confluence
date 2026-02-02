package org.confluence.terra_curio.common.data.gen;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.terra_curio.TerraCurio;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;

import java.util.concurrent.CompletableFuture;

public class WorkshopProvider extends AbstractRecipeProvider {
    public WorkshopProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        recipe(recipeOutput, TCItems.AMBER_HORSESHOE_BALLOON.toStack(), Ingredient.of(TCItems.HONEY_BALLOON), Ingredient.of(TCItems.LUCKY_HORSESHOE));
    }

    protected void recipe(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = TerraCurio.asResource(getItemName(result.getItem()));
        recipeOutput.accept(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void recipe(RecipeOutput recipeOutput, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = TerraCurio.asResource(getItemName(result.getItem()));
        recipeOutput.accept(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), environment), null);
    }
}
