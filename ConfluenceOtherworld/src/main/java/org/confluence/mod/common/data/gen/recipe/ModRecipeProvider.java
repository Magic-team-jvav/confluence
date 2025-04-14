package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends AbstractRecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.AMBER_ORE_SMELTING), MaterialItems.AMBER.toStack(), 0.4F, 100);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.AMBER_ORE_SMELTING), MaterialItems.AMBER.toStack(), 0.5F, 200);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.1F, 100);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "_from_atlantic_cod", Ingredient.of(FoodItems.ATLANTIC_COD), Items.COOKED_COD.getDefaultInstance(), 0.1F, 200);

        recipeOutput.accept(Confluence.asResource("smithing/amber_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_BLOCK),
                HookItems.AMBER_HOOK.toStack()
        ), null);

        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_ICE_BRICKS.toStack(4), Ingredient.of(Blocks.BLUE_ICE));

        skyMill(recipeOutput, DecorativeBlocks.BOUNCY_CLOUD_BLOCK.toStack(), Ingredient.of(MaterialItems.PINK_GEL), Ingredient.of(NatureBlocks.CLOUD_BLOCK));

        workshop(recipeOutput, AccessoryItems.ANGLER_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.HIGH_TEST_FISHING_LINE), Ingredient.of(AccessoryItems.TACKLE_BOX), Ingredient.of(TCItems.ANGLER_EARRING));

        hellforge(recipeOutput, MaterialItems.HELLSTONE_INGOT.toStack(), 0, 100, true, AmountIngredient.of(3, MaterialItems.RAW_HELLSTONE), Ingredient.of(Items.OBSIDIAN));

        fletchingTable(recipeOutput, "", ArrowItems.FLAMING_ARROW.toStack(25), Ingredient.EMPTY, AmountIngredient.of(25, Items.ARROW), Ingredient.of(ModTags.Items.TORCH));

        cookingPot(recipeOutput, FoodItems.APPLE_PIE.toStack(), Ingredient.of(PotionItems.BOTTLE), CookingPotRecipe.HeatSourcePredicate.EMPTY, 100, Ingredient.of(Items.APPLE));

        altar(recipeOutput, ConsumableItems.BLOODY_SPINE.toStack(), AmountIngredient.of(30, ConsumableItems.VICIOUS_POWDER), AmountIngredient.of(15, MaterialItems.VERTEBRA));

        alchemyTable(recipeOutput, PotionItems.ARCHERY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.LENS), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.SWIFTNESS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.CACTUS));

        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS)
        ), List.of(
                " ## ",
                " ## ",
                " ## ",
                "####"
        )), StatueBlocks.A_STATUE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,ModTags.Items.LEAD_AND_IRON),
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

    protected void stonecutting(RecipeOutput recipeOutput, String suffix, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new StonecutterRecipe("", ingredient, result), null);
    }

    protected <T extends AbstractCookingRecipe> void cooking(RecipeOutput recipeOutput, AbstractCookingRecipe.Factory<T> factory, String prefix, String suffix, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, factory.create("", CookingBookCategory.MISC, ingredient, result, experience, cookingTime), null);
    }

    protected void skyMill(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sky_mill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SkyMillRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients)), null);
    }

    protected void workshop(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("workshop/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients)), null);
    }

    protected void hellforge(RecipeOutput recipeOutput, ItemStack result, float experience, int cookingTime, boolean requiresFuel, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hellforge/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HellforgeRecipe(result, zingredients, experience, cookingTime, requiresFuel), null);
    }

    protected void fletchingTable(RecipeOutput recipeOutput, String suffix, ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        ResourceLocation id = Confluence.asResource("fletching_table/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new FletchingTableRecipe(result, tail, body, head), null);
    }

    protected void cookingPot(RecipeOutput recipeOutput, ItemStack result, Ingredient container, CookingPotRecipe.HeatSourcePredicate heatSource, int cookingTime, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("cooking_pot/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new CookingPotRecipe(result, zingredients, container, heatSource, cookingTime), null);
    }

    protected void altar(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("altar/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new AltarRecipe(result, zingredients), null);
    }

    protected void alchemyTable(RecipeOutput recipeOutput, ItemStack result, Ingredient base, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("alchemy_table/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new AlchemyTableRecipe(result, base, zingredients), null);
    }
}
