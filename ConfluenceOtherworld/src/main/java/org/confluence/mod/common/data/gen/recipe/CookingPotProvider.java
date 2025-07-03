package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.confluence.mod.common.recipe.CookingPotRecipe.HeatSourcePredicate;

import java.util.concurrent.CompletableFuture;

public class CookingPotProvider extends AbstractRecipeProvider {
    public CookingPotProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        Ingredient bottleContainer = Ingredient.of(PotionItems.BOTTLE);
        Ingredient bowlContainer = Ingredient.of(Items.BOWL);
        HeatSourcePredicate campfireHeatSource = HeatSourcePredicate.builder().of(BlockTags.CAMPFIRES).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LIT, true)).build();
        HeatSourcePredicate snowHeatSource = HeatSourcePredicate.builder().of(Blocks.SNOW_BLOCK).build();
        cookingPot(recipeOutput, SwordItems.SWEET_SWORD.toStack(), Ingredient.of(Items.WOODEN_SWORD), HeatSourcePredicate.EMPTY, 100, Ingredient.of(Items.COOKIE), Ingredient.of(Items.SUGAR), Ingredient.of(Items.COCOA_BEANS));   // 糖果剑
        cookingPot(recipeOutput, FoodItems.APPLE_PIE.toStack(), bowlContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(Items.APPLE));
        cookingPot(recipeOutput, FoodItems.BLOODY_MOSCATO.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.BLOOD_ORANGE), Ingredient.of(FoodItems.RAMBUTAN));
        cookingPot(recipeOutput, FoodItems.BOWL_OF_SOUP.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(MaterialItems.LIFE_MUSHROOM), Ingredient.of(FoodItems.GOLDFISH));
        cookingPot(recipeOutput, FoodItems.BUNNY_STEW.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(Items.RABBIT));
        cookingPot(recipeOutput, FoodItems.COOK_FISH.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(Tags.Items.FOODS_RAW_FISH));
        cookingPot(recipeOutput, FoodItems.COOKED_SHRIMP.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.SHRIMP));
        cookingPot(recipeOutput, FoodItems.ESCARGOT.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(BaitItems.SNAIL));
        cookingPot(recipeOutput, FoodItems.FROGGLE_BUNWICH.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, AmountIngredient.of(2, FoodItems.RAW_FROG));
        cookingPot(recipeOutput, FoodItems.FROZEN_BANANA_DAIQUIRI.toStack(), bottleContainer, snowHeatSource, 100, Ingredient.of(FoodItems.BANANA));
        cookingPot(recipeOutput, FoodItems.FRUIT_JUICE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(3, Tags.Items.FOODS_FRUIT));
        cookingPot(recipeOutput, FoodItems.FRUIT_SALAD.toStack(), bowlContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(3, Tags.Items.FOODS_FRUIT));
        cookingPot(recipeOutput, FoodItems.GOLDEN_DELIGHT.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(ModTags.Items.GOLD_COOKING));
        cookingPot(recipeOutput, FoodItems.GRAPE_JUICE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(2, FoodItems.GRAPE));
        cookingPot(recipeOutput, FoodItems.GRILLED_SQUIRREL.toStack(), Ingredient.of(Items.STICK), campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_SQUIRREL));
        cookingPot(recipeOutput, FoodItems.GRUB_SOUP.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(BaitItems.GRUBBY), Ingredient.of(BaitItems.SLUGGY), Ingredient.of(BaitItems.BUGGY));
        cookingPot(recipeOutput, FoodItems.LEMONADE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.LEMON));
        cookingPot(recipeOutput, FoodItems.LOBSTER_TAIL.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(FoodItems.ROCK_LOBSTER));
        cookingPot(recipeOutput, FoodItems.LONGEVITY_NOODLES.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(Items.EGG), Ingredient.of(Items.BEETROOT), AmountIngredient.of(2, Items.WHEAT));
        cookingPot(recipeOutput, "_from_rotten_chunk", FoodItems.MONSTER_LASAGNA.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(8, MaterialItems.ROTTEN_CHUNK));
        cookingPot(recipeOutput, "_from_vertebra", FoodItems.MONSTER_LASAGNA.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(8, MaterialItems.VERTEBRA));
        cookingPot(recipeOutput, FoodItems.PEACH_SANGRIA.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.PEACH));
        cookingPot(recipeOutput, FoodItems.PINA_COLADA.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.PINEAPPLE), Ingredient.of(FoodItems.COCONUT));
        cookingPot(recipeOutput, FoodItems.PRISMATIC_PUNCH.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.STAR_FRUIT), Ingredient.of(FoodItems.DRAGON_FRUIT));
        cookingPot(recipeOutput, FoodItems.ROASTED_BIRD.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_BIRD));
        cookingPot(recipeOutput, FoodItems.ROASTED_DUCK.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_DUCK));
        cookingPot(recipeOutput, FoodItems.SAUTEED_FROG_LEGS.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_FROG));
        cookingPot(recipeOutput, FoodItems.SEAFOOD_DINNER.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(2, ModTags.Items.SEAFOOD_DINNER_MATERIALS));
        cookingPot(recipeOutput, FoodItems.SMOOTHIE_OF_DARKNESS.toStack(), bottleContainer, campfireHeatSource, 200, Ingredient.of(FoodItems.ELDERBERRY), Ingredient.of(FoodItems.BLACKCURRANT));
        cookingPot(recipeOutput, FoodItems.TROPICAL_SMOOTHIE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.MANGO), Ingredient.of(FoodItems.PINEAPPLE));
        cookingPot(recipeOutput, FoodItems.ZONGZI.toStack(), Ingredient.of(Items.LILY_PAD), campfireHeatSource, 200, Ingredient.of(Items.SUGAR), Ingredient.of(Items.STRING), Ingredient.of(Items.WHEAT));
        cookingPot(recipeOutput, FoodItems.MEAT_STUFFED_ZONGZI.toStack(), Ingredient.of(Items.LILY_PAD), campfireHeatSource, 200, Ingredient.of(Tags.Items.FOODS_RAW_MEAT), Ingredient.of(Items.STRING), Ingredient.of(Items.WHEAT));
    }

    protected void cookingPot(RecipeOutput recipeOutput, ItemStack result, Ingredient container, HeatSourcePredicate heatSource, int cookingTime, Ingredient... ingredients) {
        cookingPot(recipeOutput, "", result, container, heatSource, cookingTime, ingredients);
    }

    protected void cookingPot(RecipeOutput recipeOutput, String suffix, ItemStack result, Ingredient container, HeatSourcePredicate heatSource, int cookingTime, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("cooking_pot/" + getItemName(result.getItem()) + suffix);
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new CookingPotRecipe(result, zingredients, container, heatSource, cookingTime), null);
    }
}
