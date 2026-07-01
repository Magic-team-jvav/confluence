package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.SimpleFinishedRecipe;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.CookingPotRecipe;
import org.confluence.mod.common.recipe.CookingPotRecipe.HeatSourcePredicate;
import org.mesdag.portlib.wrapper.common.PortTags;

import java.util.function.Consumer;

public class CookingPotRecipeProvider extends AbstractRecipeProvider {
    public CookingPotRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        Ingredient bottleContainer = Ingredient.of(PotionItems.BOTTLE);
        Ingredient bowlContainer = Ingredient.of(Items.BOWL);
        HeatSourcePredicate campfireHeatSource = HeatSourcePredicate.builder().of(BlockTags.CAMPFIRES).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.LIT, true)).build();
        HeatSourcePredicate snowHeatSource = HeatSourcePredicate.builder().of(Blocks.SNOW_BLOCK).build();
        cookingPot(writer, SwordItems.SWEET_SWORD.toStack(), Ingredient.of(Items.WOODEN_SWORD), HeatSourcePredicate.EMPTY, 100, Ingredient.of(Items.COOKIE), Ingredient.of(Items.SUGAR), Ingredient.of(Items.COCOA_BEANS));   // 糖果剑
        cookingPot(writer, FoodItems.BLOODY_MOSCATO.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.BLOOD_ORANGE), Ingredient.of(FoodItems.RAMBUTAN));
        cookingPot(writer, FoodItems.BOWL_OF_SOUP.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(MaterialItems.LIFE_MUSHROOM), Ingredient.of(FoodItems.GOLDFISH));
        cookingPot(writer, FoodItems.BUNNY_STEW.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(Items.RABBIT));
        cookingPot(writer, FoodItems.COOK_FISH.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(PortTags.Items.FOODS_RAW_FISH));
        cookingPot(writer, FoodItems.COOKED_SHRIMP.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.SHRIMP));
        cookingPot(writer, FoodItems.ESCARGOT.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(BaitItems.SNAIL));
        cookingPot(writer, FoodItems.FROGGLE_BUNWICH.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, AmountIngredient.of(2, FoodItems.RAW_FROG));
        cookingPot(writer, FoodItems.FROZEN_BANANA_DAIQUIRI.toStack(), bottleContainer, snowHeatSource, 100, Ingredient.of(FoodItems.BANANA));
        cookingPot(writer, FoodItems.FRUIT_JUICE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(3, PortTags.Items.FOODS_FRUIT));
        cookingPot(writer, FoodItems.FRUIT_SALAD.toStack(), bowlContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(3, PortTags.Items.FOODS_FRUIT));
        cookingPot(writer, FoodItems.GOLDEN_DELIGHT.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(ModTags.Items.GOLD_COOKING));
        cookingPot(writer, FoodItems.GRAPE_JUICE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, AmountIngredient.of(2, FoodItems.GRAPE));
        cookingPot(writer, FoodItems.GRILLED_SQUIRREL.toStack(), Ingredient.of(Items.STICK), campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_SQUIRREL));
        cookingPot(writer, FoodItems.GRUB_SOUP.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(BaitItems.GRUBBY), Ingredient.of(BaitItems.SLUGGY), Ingredient.of(BaitItems.BUGGY));
        cookingPot(writer, FoodItems.LEMONADE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.LEMON));
        cookingPot(writer, FoodItems.LOBSTER_TAIL.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(FoodItems.ROCK_LOBSTER));
        cookingPot(writer, FoodItems.LONGEVITY_NOODLES.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(Items.EGG), Ingredient.of(Items.BEETROOT), AmountIngredient.of(2, Items.WHEAT));
        cookingPot(writer, "_from_rotten_chunk", FoodItems.MONSTER_LASAGNA.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(8, MaterialItems.ROTTEN_CHUNK));
        cookingPot(writer, "_from_vertebra", FoodItems.MONSTER_LASAGNA.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(8, MaterialItems.VERTEBRA));
        cookingPot(writer, FoodItems.PEACH_SANGRIA.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.PEACH));
        cookingPot(writer, FoodItems.PINA_COLADA.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.PINEAPPLE), Ingredient.of(FoodItems.COCONUT));
        cookingPot(writer, FoodItems.PRISMATIC_PUNCH.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.STAR_FRUIT), Ingredient.of(FoodItems.DRAGON_FRUIT));
        cookingPot(writer, FoodItems.ROASTED_BIRD.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_BIRD));
        cookingPot(writer, FoodItems.ROASTED_DUCK.toStack(), Ingredient.EMPTY, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_DUCK));
        cookingPot(writer, FoodItems.SAUTEED_FROG_LEGS.toStack(), bowlContainer, campfireHeatSource, 200, Ingredient.of(FoodItems.RAW_FROG));
        cookingPot(writer, FoodItems.SEAFOOD_DINNER.toStack(), bowlContainer, campfireHeatSource, 200, AmountIngredient.of(2, ModTags.Items.SEAFOOD_DINNER_MATERIALS));
        cookingPot(writer, FoodItems.SMOOTHIE_OF_DARKNESS.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 200, Ingredient.of(FoodItems.ELDERBERRY), Ingredient.of(FoodItems.BLACKCURRANT));
        cookingPot(writer, FoodItems.TROPICAL_SMOOTHIE.toStack(), bottleContainer, HeatSourcePredicate.EMPTY, 100, Ingredient.of(FoodItems.MANGO), Ingredient.of(FoodItems.PINEAPPLE));
        cookingPot(writer, FoodItems.ZONGZI.toStack(), Ingredient.of(Items.LILY_PAD), campfireHeatSource, 200, Ingredient.of(Items.SUGAR), Ingredient.of(Items.STRING), Ingredient.of(Items.WHEAT));
        cookingPot(writer, FoodItems.MEAT_STUFFED_ZONGZI.toStack(), Ingredient.of(Items.LILY_PAD), campfireHeatSource, 200, Ingredient.of(PortTags.Items.FOODS_RAW_MEAT), Ingredient.of(Items.STRING), Ingredient.of(Items.WHEAT));
        cookingPot(writer, FoodItems.COOKED_MARSHMALLOW.toStack(), Ingredient.of(Items.STICK), campfireHeatSource, 200, Ingredient.of(FoodItems.MARSHMALLOW));
    }

    protected void cookingPot(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient container, HeatSourcePredicate heatSource, int cookingTime, Ingredient... ingredients) {
        cookingPot(writer, "", result, container, heatSource, cookingTime, ingredients);
    }

    protected void cookingPot(Consumer<FinishedRecipe> writer, String suffix, ItemStack result, Ingredient container, HeatSourcePredicate heatSource, int cookingTime, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("cooking_pot/" + getItemName(result.getItem()) + suffix);
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        CookingPotRecipe recipe = new CookingPotRecipe(result, zingredients, container, heatSource, cookingTime);
        writer.accept(new SimpleFinishedRecipe<>(id, recipe));
    }
}
