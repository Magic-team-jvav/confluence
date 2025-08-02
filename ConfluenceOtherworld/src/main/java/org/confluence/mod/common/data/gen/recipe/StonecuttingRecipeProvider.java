package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.data.gen.recipe.ModRecipeProvider.createAdvancementHolder;

public class StonecuttingRecipeProvider extends AbstractRecipeProvider {
    public StonecuttingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_ICE_BRICKS.toStack(4), Ingredient.of(Blocks.BLUE_ICE));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_ICE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.BLUE_ICE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_ICE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.BLUE_ICE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PACKED_ICE_BRICKS.toStack(4), Ingredient.of(Blocks.PACKED_ICE));
        stonecutting(recipeOutput, DecorativeBlocks.PACKED_ICE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PACKED_ICE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PACKED_ICE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.PACKED_ICE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.AETHERIUM_BRICKS.toStack(4), Ingredient.of(NatureBlocks.AETHERIUM_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_BRICKS.toStack(4), Ingredient.of(Blocks.OBSIDIAN));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_BRICK_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.GREEN_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.GREEN_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.GREEN_BRICK_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PINK_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.PINK_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PINK_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PINK_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PINK_BRICK_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.PINK_BRICKS));

        stonecutting(recipeOutput, MaterialItems.CHINA_BOWL.toStack(), Ingredient.of(Items.WHITE_TERRACOTTA));
        stonecutting(recipeOutput, MaterialItems.CHINA_PLATE.toStack(), Ingredient.of(Items.WHITE_TERRACOTTA));


        stonecutting(recipeOutput, DecorativeBlocks.GRANITE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.GRANITE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.GRANITE_BRICKS.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(recipeOutput, DecorativeBlocks.POLISHED_GRANITE.toStack(), Ingredient.of(DecorativeBlocks.GRANITE_BRICKS));

        stonecutting(recipeOutput, NatureBlocks.MARBLE.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.MARBLE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.MARBLE_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, DecorativeBlocks.POLISHED_MARBLE.toStack(), Ingredient.of(DecorativeBlocks.MARBLE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.MARBLE_BRICKS));


        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));

        stonecutting(recipeOutput, DecorativeBlocks.SNOW_BRICKS.toStack(), Ingredient.of(Items.SNOW_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.SNOW_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SNOW_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.SNOW_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.SNOW_BRICKS));

        stonecutting(recipeOutput, DecorativeBlocks.GOLDEN_BRICKS.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.GOLDEN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.GOLDEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.GOLDEN_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.GOLDEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.COPPER_BRICKS.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.COPPER_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.COPPER_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.COPPER_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.COPPER_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.IRON_BRICKS.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.IRON_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.IRON_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.IRON_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.IRON_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.TIN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.TIN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.TIN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.TIN_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.TIN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.LEAD_BRICKS.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.LEAD_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.LEAD_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.LEAD_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.LEAD_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.SILVER_BRICKS.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.SILVER_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SILVER_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.SILVER_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.SILVER_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.TUNGSTEN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.TUNGSTEN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.TUNGSTEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.TUNGSTEN_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.TUNGSTEN_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PLATINUM_BRICKS.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.PLATINUM_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PLATINUM_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PLATINUM_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.PLATINUM_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.DEMONITE_ORE_BRICKS.toStack(9), Ingredient.of(OreBlocks.DEMONITE_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.DEMONITE_ORE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.DEMONITE_ORE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.DEMONITE_ORE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.DEMONITE_ORE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMTANE_ORE_BRICKS.toStack(9), Ingredient.of(OreBlocks.CRIMTANE_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMTANE_ORE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.CRIMTANE_ORE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMTANE_ORE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.CRIMTANE_ORE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.METEORITE_BRICKS.toStack(9), Ingredient.of(OreBlocks.METEORITE_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.METEORITE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.METEORITE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.METEORITE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.METEORITE_BRICKS));

        stonecutting(recipeOutput, DecorativeBlocks.EBONSTONE_BRICKS.toStack(), Ingredient.of(NatureBlocks.EBONSTONE));
        stonecutting(recipeOutput, DecorativeBlocks.EBONSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.EBONSTONE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.EBONSTONE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.EBONSTONE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMSTONE_BRICKS.toStack(9), Ingredient.of(NatureBlocks.CRIMSTONE));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.CRIMSTONE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMSTONE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.CRIMSTONE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PEARLSTONE_BRICKS.toStack(9), Ingredient.of(NatureBlocks.PEARLSTONE));
        stonecutting(recipeOutput, DecorativeBlocks.PEARLSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PEARLSTONE_BRICKS));
        stonecutting(recipeOutput, DecorativeBlocks.PEARLSTONE_BRICKS_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.PEARLSTONE_BRICKS));
    }

    protected void stonecutting(RecipeOutput recipeOutput, String suffix, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new StonecutterRecipe("", ingredient, result), createAdvancementHolder(recipeOutput, id, ingredient));
    }

    protected void stonecutting(RecipeOutput recipeOutput, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new StonecutterRecipe("", ingredient, result), createAdvancementHolder(recipeOutput, id, ingredient));
    }
}
