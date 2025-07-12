package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.recipe.SawmillRecipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class SawmillRecipeProvider extends AbstractRecipeProvider {
    public SawmillRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        shaped(recipeOutput, FunctionalBlocks.KEG.toStack(), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Tags.Items.BARRELS_WOODEN),
                'B', Ingredient.of(Tags.Items.FENCES_WOODEN),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER)
        ), List.of(
                "AC",
                "B "
        )));

        shapeless(recipeOutput, new ItemStack(Items.OAK_PLANKS, 9), Ingredient.of(ItemTags.OAK_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.ACACIA_PLANKS, 9), Ingredient.of(ItemTags.ACACIA_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.BAMBOO_PLANKS), Ingredient.of(Items.BAMBOO));
        shapeless(recipeOutput, new ItemStack(Items.CHERRY_PLANKS, 9), Ingredient.of(ItemTags.CHERRY_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.CRIMSON_PLANKS, 9), Ingredient.of(ItemTags.CRIMSON_STEMS));
        shapeless(recipeOutput, new ItemStack(Items.DARK_OAK_PLANKS, 9), Ingredient.of(ItemTags.DARK_OAK_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.JUNGLE_PLANKS, 9), Ingredient.of(ItemTags.JUNGLE_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.BIRCH_PLANKS, 9), Ingredient.of(ItemTags.BIRCH_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.MANGROVE_PLANKS, 9), Ingredient.of(ItemTags.MANGROVE_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.SPRUCE_PLANKS, 9), Ingredient.of(ItemTags.SPRUCE_LOGS));
        shapeless(recipeOutput, new ItemStack(Items.WARPED_PLANKS, 9), Ingredient.of(ItemTags.WARPED_STEMS));
        shapeless(recipeOutput, NatureBlocks.EBONY_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getLog(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(), NatureBlocks.EBONY_LOG_BLOCKS.getWood(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getLog(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedLog(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getWood(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.LIVING_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getLog(), NatureBlocks.LIVING_LOG_BLOCKS.getStrippedLog(), NatureBlocks.LIVING_LOG_BLOCKS.getWood(), NatureBlocks.LIVING_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.ASH_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getLog(), NatureBlocks.ASH_LOG_BLOCKS.getStrippedLog(), NatureBlocks.ASH_LOG_BLOCKS.getWood(), NatureBlocks.ASH_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getLog(), NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getStrippedLog(), NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getWood(), NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getLog(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedLog(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.PALM_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getLog(), NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(), NatureBlocks.PALM_LOG_BLOCKS.getWood(), NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood()));
        shapeless(recipeOutput, NatureBlocks.PEARL_LOG_BLOCKS.getPlanks().toStack(9), Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getLog(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(), NatureBlocks.PEARL_LOG_BLOCKS.getWood(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood()));

        shapeless(recipeOutput, new ItemStack(Items.OAK_SLAB, 4), Ingredient.of(Items.OAK_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.ACACIA_SLAB, 4), Ingredient.of(Items.ACACIA_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.BAMBOO_SLAB, 4), Ingredient.of(Items.BAMBOO_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.CHERRY_SLAB, 4), Ingredient.of(Items.CHERRY_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.CRIMSON_SLAB, 4), Ingredient.of(Items.CRIMSON_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.DARK_OAK_SLAB, 4), Ingredient.of(Items.DARK_OAK_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.JUNGLE_SLAB, 4), Ingredient.of(Items.JUNGLE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.BIRCH_SLAB, 4), Ingredient.of(Items.BIRCH_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.MANGROVE_SLAB, 4), Ingredient.of(Items.MANGROVE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.SPRUCE_SLAB, 4), Ingredient.of(Items.SPRUCE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.WARPED_SLAB, 4), Ingredient.of(Items.WARPED_PLANKS));
        shapeless(recipeOutput, NatureBlocks.EBONY_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.SHADOW_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.LIVING_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.ASH_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.BAOBAB_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.PALM_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.PEARL_LOG_BLOCKS.getSlab().toStack(4), Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()));

        shapeless(recipeOutput, new ItemStack(Items.OAK_STAIRS, 2), Ingredient.of(Items.OAK_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.ACACIA_STAIRS, 2), Ingredient.of(Items.ACACIA_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.BAMBOO_STAIRS, 2), Ingredient.of(Items.BAMBOO_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.CHERRY_STAIRS, 2), Ingredient.of(Items.CHERRY_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.CRIMSON_STAIRS, 2), Ingredient.of(Items.CRIMSON_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.DARK_OAK_STAIRS, 2), Ingredient.of(Items.DARK_OAK_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.JUNGLE_STAIRS, 2), Ingredient.of(Items.JUNGLE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.BIRCH_STAIRS, 2), Ingredient.of(Items.BIRCH_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.MANGROVE_STAIRS, 2), Ingredient.of(Items.MANGROVE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.SPRUCE_STAIRS, 2), Ingredient.of(Items.SPRUCE_PLANKS));
        shapeless(recipeOutput, new ItemStack(Items.WARPED_STAIRS, 2), Ingredient.of(Items.WARPED_PLANKS));
        shapeless(recipeOutput, NatureBlocks.EBONY_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.SHADOW_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.LIVING_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.ASH_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.BAOBAB_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.PALM_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getPlanks()));
        shapeless(recipeOutput, NatureBlocks.PEARL_LOG_BLOCKS.getStairs().toStack(2), Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()));
    }

    protected void shaped(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SawmillRecipe(result, pattern), null);
    }

    protected void shapeless(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        NonNullList<Ingredient> ingredientz = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new SawmillRecipe(result, ingredientz), null);
    }
}
