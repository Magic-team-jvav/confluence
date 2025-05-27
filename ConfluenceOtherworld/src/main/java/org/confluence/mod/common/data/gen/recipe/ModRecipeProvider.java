package org.confluence.mod.common.data.gen.recipe;

import com.mojang.datafixers.util.Either;
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
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
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
        stonecutting(recipeOutput, "", MaterialItems.CHINA_BOWL.toStack(1), Ingredient.of(Items.WHITE_TERRACOTTA));
        stonecutting(recipeOutput, "", MaterialItems.CHINA_PLATE.toStack(1), Ingredient.of(Items.WHITE_TERRACOTTA));

        skyMill(recipeOutput, DecorativeBlocks.BOUNCY_CLOUD_BLOCK.toStack(), Ingredient.of(MaterialItems.PINK_GEL), Ingredient.of(NatureBlocks.CLOUD_BLOCK));

        workshop(recipeOutput, AccessoryItems.ANGLER_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.HIGH_TEST_FISHING_LINE), Ingredient.of(AccessoryItems.TACKLE_BOX), Ingredient.of(TCItems.ANGLER_EARRING));
        workshop(recipeOutput, AccessoryItems.MAGIC_CUFFS.toStack(), Ingredient.of(AccessoryItems.MANA_REGENERATION_BAND), Ingredient.of(TCItems.SHACKLE));
        workshop(recipeOutput, AccessoryItems.CELESTIAL_CUFFS.toStack(), Ingredient.of(AccessoryItems.MAGIC_CUFFS), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET));
        workshop(recipeOutput, AccessoryItems.CELESTIAL_EMBLEM.toStack(), Ingredient.of(AccessoryItems.CELESTIAL_CUFFS), Ingredient.of(TCItems.AVENGER_EMBLEM));
        workshop(recipeOutput, AccessoryItems.LAVAPROOF_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.LAVAPROOF_FISHING_HOOK), Ingredient.of(AccessoryItems.ANGLER_TACKLE_BAG));
        workshop(recipeOutput, AccessoryItems.GLOWING_FISHING_BOBBER.toStack(), Ingredient.of(AccessoryItems.FISHING_BOBBER), AmountIngredient.of(5, MaterialItems.FALLING_STAR));
        workshop(recipeOutput, AccessoryItems.COIN_RING.toStack(), Ingredient.of(AccessoryItems.LUCKY_COIN), Ingredient.of(AccessoryItems.GOLD_RING));
        workshop(recipeOutput, AccessoryItems.GREEDY_RING.toStack(), Ingredient.of(AccessoryItems.COIN_RING), Ingredient.of(AccessoryItems.DISCOUNT_CARD));
        workshop(recipeOutput, AccessoryItems.CHARM_OF_MYTHS.toStack(), Ingredient.of(TCItems.BAND_OF_REGENERATION), Ingredient.of(AccessoryItems.PHILOSOPHERS_STONE));
        workshop(recipeOutput, AccessoryItems.PAPYRUS_SCARAB.toStack(), Ingredient.of(AccessoryItems.NECROMANTIC_SCROLL), Ingredient.of(AccessoryItems.HERCULES_BEETLE));
        workshop(recipeOutput, TCItems.AVENGER_EMBLEM.toStack(), Ingredient.of(ModTags.Items.EMBLEM), AmountIngredient.of(5, MaterialItems.SOUL_OF_MIGHT), AmountIngredient.of(5, MaterialItems.SOUL_OF_SIGHT), AmountIngredient.of(5, MaterialItems.SOUL_OF_FRIGHT));
        workshop(recipeOutput, AccessoryItems.MEDICATED_BANDAGE.toStack(), Ingredient.of(AccessoryItems.ADHESIVE_BANDAGE), Ingredient.of(TCItems.BEZOAR));
        workshop(recipeOutput, AccessoryItems.REFLECTIVE_SHADES.toStack(), Ingredient.of(AccessoryItems.POCKET_MIRROR), Ingredient.of(TCItems.BLINDFOLD));
        workshop(recipeOutput, AccessoryItems.ARMOR_BRACING.toStack(), Ingredient.of(AccessoryItems.ARMOR_POLISH), Ingredient.of(TCItems.VITAMINS));
        workshop(recipeOutput, AccessoryItems.COUNTERCURSE_MANTRA.toStack(), Ingredient.of(AccessoryItems.MEGAPHONE), Ingredient.of(AccessoryItems.NAZAR));
        workshop(recipeOutput, TCItems.ANKH_CHARM.toStack(), Ingredient.of(AccessoryItems.ARMOR_BRACING), Ingredient.of(AccessoryItems.MEDICATED_BANDAGE), Ingredient.of(TCItems.THE_PLAN), Ingredient.of(AccessoryItems.COUNTERCURSE_MANTRA), Ingredient.of(AccessoryItems.REFLECTIVE_SHADES));

        hellforge(recipeOutput, MaterialItems.HELLSTONE_INGOT.toStack(), 0, 100, true, AmountIngredient.of(3, MaterialItems.RAW_HELLSTONE), Ingredient.of(Items.OBSIDIAN));

        fletchingTable(recipeOutput, "", ArrowItems.FLAMING_ARROW.toStack(25), Ingredient.EMPTY, AmountIngredient.of(25, Items.ARROW), Ingredient.of(ModTags.Items.TORCH));
        fletchingTable(recipeOutput, "_from_feather", new ItemStack(Items.ARROW, 20), Ingredient.of(Items.FEATHER), AmountIngredient.of(5, Items.STICK), Ingredient.of(Items.FLINT));
        fletchingTable(recipeOutput, "_from_wool", new ItemStack(Items.ARROW, 35), Ingredient.of(ItemTags.WOOL), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS));
        fletchingTable(recipeOutput, "", ArrowItems.FLY_FISH_ARROW.toStack(10), Ingredient.of(MaterialItems.FILAMENTOUS_FIN), AmountIngredient.of(10, Items.ARROW), Ingredient.of());
        fletchingTable(recipeOutput, "", ArrowItems.FOSSIL_ARROW.toStack(25), Ingredient.of(), AmountIngredient.of(25, Items.ARROW), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        fletchingTable(recipeOutput, "", ArrowItems.HELLFIRE_ARROW.toStack(64), Ingredient.of(), AmountIngredient.of(64, Items.ARROW), Ingredient.of(MaterialItems.HELLSTONE_INGOT));
        fletchingTable(recipeOutput, "", ArrowItems.STAR_ARROW.toStack(10), Ingredient.of(), AmountIngredient.of(10, Items.ARROW), Ingredient.of(MaterialItems.FALLING_STAR));
        fletchingTable(recipeOutput, "", ArrowItems.UNHOLY_ARROW.toStack(5), Ingredient.of(), AmountIngredient.of(5, Items.ARROW), Ingredient.of(ModTags.Items.EVIL_MATERIAL));

        altar(recipeOutput, ConsumableItems.BLOODY_SPINE.toStack(), AmountIngredient.of(30, ConsumableItems.VICIOUS_POWDER), AmountIngredient.of(15, MaterialItems.VERTEBRA));
        altar(recipeOutput, SwordItems.NIGHT_EDGE.toStack(), Ingredient.of(SwordItems.BLOOD_BUTCHERER, SwordItems.LIGHTS_BANE), Ingredient.of(SwordItems.MURAMASA), Ingredient.of(SwordItems.BLADE_OF_GRASS), Ingredient.of(SwordItems.VOLCANO));

        alchemyTable(recipeOutput, PotionItems.ARCHERY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.LENS), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.SWIFTNESS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.CACTUS));
        alchemyTable(recipeOutput, PotionItems.THORNS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(Items.CACTUS));
        alchemyTable(recipeOutput, PotionItems.BUILDER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.CRATE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.AMBER),Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.SHIVERTHORN),Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.DANGERSENSE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(Items.COBWEB));
        alchemyTable(recipeOutput, PotionItems.ENDURANCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.ARMORED_CAVE_FISH), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.FEATHERFALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM),Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.BLINKROOT),Ingredient.of(Items.FEATHER));
        alchemyTable(recipeOutput, ConsumableItems.FERTILIZER.toStack(), Ingredient.of(ModBlocks.POO.get()), AmountIngredient.of(3,NatureBlocks.ASH_BLOCK),AmountIngredient.of(3,Items.BONE));
        alchemyTable(recipeOutput, PotionItems.FISHING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(DecorativeBlocks.CRISPY_HONEY_BLOCK),Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.FLIPPER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.GILLS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(ModTags.Items.CORALS));
        alchemyTable(recipeOutput, PotionItems.GRAVITATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.BLINKROOT),Ingredient.of(Items.FEATHER));
        alchemyTable(recipeOutput, PotionItems.GREATER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PINK_PEARL));
        alchemyTable(recipeOutput, PotionItems.HEALING_POTION.toStack(), Ingredient.of(PotionItems.LESSER_HEALING_POTION), Ingredient.of(MaterialItems.GLOWING_MUSHROOM), Ingredient.of(PotionItems.LESSER_HEALING_POTION));
        alchemyTable(recipeOutput, PotionItems.HEART_REACH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.SCARLET_TIGER_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.HUNTER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM),Ingredient.of(MaterialItems.BLINKROOT),Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(recipeOutput, PotionItems.INFERNO_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.FLASHFIN_KOI),AmountIngredient.of(2,FoodItems.OBSIDIAN_FISH),Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(recipeOutput, PotionItems.INVISIBILITY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH),Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.IRON_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM),Ingredient.of(Items.RAW_IRON,MaterialItems.RAW_LEAD));
        alchemyTable(recipeOutput, PotionItems.LESSER_HEALING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLE), Ingredient.of(MaterialItems.LIFE_MUSHROOM),AmountIngredient.of(2,MaterialItems.GEL));
        alchemyTable(recipeOutput, PotionItems.LESSER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PEARL));
        alchemyTable(recipeOutput, PotionItems.LIFEFORCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.COLORFUL_MINERAL_FISH),Ingredient.of(MaterialItems.MOONGLOW),Ingredient.of(MaterialItems.SHIVERTHORN),Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.LOVE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PRINCESS_FISH),Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(recipeOutput, PotionItems.LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.BLACK_PEARL));
        alchemyTable(recipeOutput, PotionItems.MAGIC_POWER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(recipeOutput, PotionItems.MANA_REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(recipeOutput, PotionItems.MINING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.ANTLION_MANDIBLE), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.NIGHT_OWL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.OBSIDIAN_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.WATERLEAF),Ingredient.of(Items.OBSIDIAN));
        alchemyTable(recipeOutput, PotionItems.RAGE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.BLOODY_PIRANHAS), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.RANDOM_TELEPORT_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.CHAOS_FISH), Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(recipeOutput, PotionItems.RECALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.LIFE_MUSHROOM));
        alchemyTable(recipeOutput, PotionItems.SHINE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.GLOWING_MUSHROOM));
        alchemyTable(recipeOutput, PotionItems.SPELUNKER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW),Ingredient.of(MaterialItems.RAW_PLATINUM,Items.RAW_GOLD));
        alchemyTable(recipeOutput, PotionItems.STINK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.STINKY_FISH), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.TITAN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DUNGEON_DEMON_BONE), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(recipeOutput, PotionItems.WATER_WALKING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(recipeOutput, PotionItems.WORMHOLE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.WRATH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.EBONY_KOI), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.AMMO_RESERVATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PISCES_FIN_COD), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.SUMMONING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MOTTLED_OILFISH), Ingredient.of(MaterialItems.MOONGLOW));

        hardmodeAnvil(recipeOutput, DrillItems.DRAX.toStack(), ShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(3, MaterialItems.HALLOWED_INGOT),
                'F', Ingredient.of(MaterialItems.SOUL_OF_FRIGHT),
                'M', Ingredient.of(MaterialItems.SOUL_OF_MIGHT),
                'S', Ingredient.of(MaterialItems.SOUL_OF_SIGHT)
        ), List.of(
                "HHF ",
                "H HM",
                "HHS "
        )));
        hardmodeAnvil(recipeOutput, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.toStack(), AmountIngredient.of(18, MaterialItems.CHLOROPHYTE_INGOT), Ingredient.of(FunctionalBlocks.EXTRACTINATOR));
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

    protected void hardmodeAnvil(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HardmodeAnvilRecipe(result, Either.left(pattern)), null);
    }

    protected void hardmodeAnvil(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HardmodeAnvilRecipe(result, Either.right(zingredients)), null);
    }
}
