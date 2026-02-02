package org.confluence.terra_furniture.common.datagen;

import net.minecraft.core.HolderLookup;
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
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.terra_furniture.TerraFurniture;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.recipe.GlassKilnRecipe;
import org.confluence.terra_furniture.common.recipe.IceMachineRecipe;
import org.confluence.terra_furniture.common.recipe.LivingLoomRecipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TFRecipeProvider extends AbstractRecipeProvider {
    public TFRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        Ingredient glass = Ingredient.of(Tags.Items.GLASS_BLOCKS_COLORLESS);
        glassKiln(recipeOutput, TFBlocks.GLASS_DOOR.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass
        ), List.of(
                "GG",
                "GG",
                "GG"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_CHAIR.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass
        ), List.of(
                "G  ",
                "GGG",
                "G G"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_TOILET.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass
        ), List.of(
                "G  ",
                "G G",
                "GG "
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_TABLE.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', AmountIngredient.of(2, Items.GLASS)
        ), List.of(
                "GGGG",
                " AA "
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_SOFA.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', Ingredient.of(ItemTags.WOOL)
        ), List.of(
                "GAAG",
                "GGGG"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_SINK.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', Ingredient.of(Items.WATER_BUCKET)
        ), List.of(
                "G  ",
                "GAG",
                " G "
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_CANDLE.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', Ingredient.of(Items.TORCH)
        ), List.of(
                " G ",
                "GAG"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_LANTERN.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', Ingredient.of(Items.TORCH)
        ), List.of(
                " G ",
                "GAG",
                "GGG"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_LAMP.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', Ingredient.of(Items.TORCH)
        ), List.of(
                "A",
                "G",
                "G",
                "G"
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_CANDELABRAS.toStack(), ShapedRecipePattern.of(Map.of(
                'G', glass,
                'A', AmountIngredient.of(2, Items.TORCH)
        ), List.of(
                "AGA",
                "GGG",
                " G "
        )), 0.1F, 200);
        glassKiln(recipeOutput, TFBlocks.GLASS_CLOCK.toStack(), ShapedRecipePattern.of(Map.of(
                'G', AmountIngredient.of(2, Items.GLASS),
                'I', Ingredient.of(Items.IRON_INGOT)
        ), List.of(// 上面三格玻璃是时钟固有材料，下面五格在做其他钟时替换为其他材料.
                "IGI",
                "GIG",
                "G G",
                "GGG"
        )), 0.1F, 200);
    }

    protected void glassKiln(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern, float experience, int cookingTime) {
        ResourceLocation id = TerraFurniture.asResource("glass_kiln/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new GlassKilnRecipe(result, pattern, experience, cookingTime), null);

    }

    protected void livingLoom(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = TerraFurniture.asResource("living_loom/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new LivingLoomRecipe(result, pattern), null);
    }

    protected void iceMachine(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = TerraFurniture.asResource("ice_machine/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new IceMachineRecipe(result, pattern), null);
    }
}
