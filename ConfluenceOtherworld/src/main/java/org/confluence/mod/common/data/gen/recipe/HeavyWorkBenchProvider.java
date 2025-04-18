package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_furniture.common.init.TFBlocks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeavyWorkBenchProvider extends AbstractRecipeProvider {
    public HeavyWorkBenchProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        List<String> statuePattern = List.of(
                " ## ",
                " ## ",
                " ## ",
                "####"
        );
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS)
        ), statuePattern), StatueBlocks.A_STATUE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.LEAD_AND_IRON),
                'a', Ingredient.of(Items.TORCH)
        ), List.of(
                " ## ",
                "#  #",
                "#  #",
                "#aa#"
        )), TFBlocks.GLASS_KILN.toStack());
    }

    protected void heavyWorkBench(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }
}
