package org.confluence.mod.common.data.gen.recipe;

import com.xiaohunao.terra_moment.common.init.TMItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;
import org.confluence.terra_furniture.common.init.TFTags;
import org.confluence.terra_guns.common.init.TGItems;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends AbstractRecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        EnvironmentLevelAccess.SearchContext searchWater = searchWater(holderLookup);

        // 高炉
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_AMBER), MaterialItems.AMBER.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_RUBY), MaterialItems.RUBY.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_TOPAZ), MaterialItems.TOPAZ.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_JADE), MaterialItems.JADE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_SAPPHIRE), MaterialItems.SAPPHIRE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.ORES_AMETHYST), MaterialItems.AMETHYST.toStack(), 1.0F, 100);

        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.COAL_ORE_SMELTING), Items.COAL.getDefaultInstance(), 0.1F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.COPPER_ORE_SMELTING), Items.COPPER_INGOT.getDefaultInstance(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.IRON_ORE_SMELTING), Items.IRON_INGOT.getDefaultInstance(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.GOLD_ORE_SMELTING), Items.GOLD_INGOT.getDefaultInstance(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.DIAMOND_ORE_SMELTING), Items.DIAMOND.getDefaultInstance(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.EMERALD_ORE_SMELTING), Items.EMERALD.getDefaultInstance(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.REDSTONE_ORE_SMELTING), Items.REDSTONE.getDefaultInstance(), 0.7F, 100);

        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.TIN_ORE_SMELTING), MaterialItems.TIN_INGOT.toStack(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.LEAD_ORE_SMELTING), MaterialItems.LEAD_INGOT.toStack(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.SILVER_ORE_SMELTING), MaterialItems.SILVER_INGOT.toStack(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.TUNGSTEN_ORE_SMELTING), MaterialItems.TUNGSTEN_INGOT.toStack(), 0.7F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.PLATINUM_ORE_SMELTING), MaterialItems.PLATINUM_INGOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.CRIMTANE_ORE_SMELTING), MaterialItems.CRIMTANE_INGOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.DEMONITE_ORE_SMELTING), MaterialItems.DEMONITE_INGOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.METEORITE_ORE_SMELTING), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.COLD_CRYSTAL_ORE), MaterialItems.COLD_CRYSTAL.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.GELSTONE_ORE), MaterialItems.GELSTONE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.SPORE_ROOT_BLOCK), MaterialItems.SPORE_ROOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.OPAL_ORE), MaterialItems.OPAL.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.WINTER_MARROW_BLOCK), MaterialItems.WINTER_MARROW.toStack(), 1.0F, 100);
        // 熔炉
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_AMBER), MaterialItems.AMBER.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_RUBY), MaterialItems.RUBY.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_TOPAZ), MaterialItems.TOPAZ.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_JADE), MaterialItems.JADE.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_SAPPHIRE), MaterialItems.SAPPHIRE.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.ORES_AMETHYST), MaterialItems.AMETHYST.toStack(), 1.0F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.COAL_ORE_SMELTING), Items.COAL.getDefaultInstance(), 0.1F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.COPPER_ORE_SMELTING), Items.COPPER_INGOT.getDefaultInstance(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.IRON_ORE_SMELTING), Items.IRON_INGOT.getDefaultInstance(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.GOLD_ORE_SMELTING), Items.GOLD_INGOT.getDefaultInstance(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.DIAMOND_ORE_SMELTING), Items.DIAMOND.getDefaultInstance(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.EMERALD_ORE_SMELTING), Items.EMERALD.getDefaultInstance(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.REDSTONE_ORE_SMELTING), Items.REDSTONE.getDefaultInstance(), 0.7F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.TIN_ORE_SMELTING), MaterialItems.TIN_INGOT.toStack(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.LEAD_ORE_SMELTING), MaterialItems.LEAD_INGOT.toStack(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.SILVER_ORE_SMELTING), MaterialItems.SILVER_INGOT.toStack(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.TUNGSTEN_ORE_SMELTING), MaterialItems.TUNGSTEN_INGOT.toStack(), 0.7F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.PLATINUM_ORE_SMELTING), MaterialItems.PLATINUM_INGOT.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.CRIMTANE_ORE_SMELTING), MaterialItems.CRIMTANE_INGOT.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.DEMONITE_ORE_SMELTING), MaterialItems.DEMONITE_INGOT.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.METEORITE_ORE_SMELTING), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.COLD_CRYSTAL_ORE), MaterialItems.COLD_CRYSTAL.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.GELSTONE_ORE), MaterialItems.GELSTONE.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.SPORE_ROOT_BLOCK), MaterialItems.SPORE_ROOT.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.OPAL_ORE), MaterialItems.OPAL.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.WINTER_MARROW_BLOCK), MaterialItems.WINTER_MARROW.toStack(), 1.0F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_EBONSAND_BLOCK), NatureBlocks.EBONSAND.toStack(), 0.15F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_PEARLSAND_BLOCK), NatureBlocks.PEARLSAND.toStack(), 0.15F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_RED_SAND_BLOCK), Items.RED_SAND.getDefaultInstance(), 0.15F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_SAND_BLOCK), Items.SAND.getDefaultInstance(), 0.15F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(Tags.Items.GLASS_BLOCKS_COLORLESS), PotionItems.MUG.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(NatureBlocks.DIATOMACEOUS), DecorativeBlocks.PURE_GLASS.toStack(), 0.3F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "gold_nugget_from_gold_cooking", Ingredient.of(ModTags.Items.GOLD_COOKING), Items.GOLD_NUGGET.getDefaultInstance(), 0.1F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(DecorativeBlocks.MARBLE_BRICKS), DecorativeBlocks.CRACKED_MARBLE_BRICKS.toStack(), 0.1F, 200);


        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "glass_from_crimsand", Ingredient.of(NatureBlocks.CRIMSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "glass_from_ebonsand", Ingredient.of(NatureBlocks.EBONSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "glass_from_pearlsand", Ingredient.of(NatureBlocks.PEARLSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);

        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.GOLD_COOKING), Items.GOLD_NUGGET.getDefaultInstance(), 0.1F, 200);

        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 100);
        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "_from_atlantic_cod", Ingredient.of(FoodItems.ATLANTIC_COD), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 200);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);


        recipeOutput.accept(Confluence.asResource("smithing/amber_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_BLOCK),
                HookItems.AMBER_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/topaz_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN),
                Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN),
                Ingredient.of(DecorativeBlocks.TOPAZ_BLOCK),
                HookItems.TOPAZ_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/amethyst_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN),
                Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN),
                Ingredient.of(DecorativeBlocks.AMETHYST_BLOCK),
                HookItems.AMETHYST_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/diamond_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN),
                Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN),
                Ingredient.of(Items.DIAMOND_BLOCK),
                HookItems.DIAMOND_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/ruby_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.RUBY_CHAIN),
                Ingredient.of(DecorativeBlocks.RUBY_CHAIN),
                Ingredient.of(DecorativeBlocks.RUBY_BLOCK),
                HookItems.RUBY_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/sapphire_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN),
                Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN),
                Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK),
                HookItems.SAPPHIRE_HOOK.toStack()
        ), null);
        recipeOutput.accept(Confluence.asResource("smithing/grappling_hook"), new SmithingTransformRecipe(
                Ingredient.of(Items.CHAIN),
                Ingredient.of(Items.CHAIN),
                Ingredient.of(MaterialItems.HOOK),
                HookItems.GRAPPLING_HOOK.toStack()
        ), null);


        skyMill(recipeOutput, DecorativeBlocks.BOUNCY_CLOUD_BLOCK.toStack(), Ingredient.of(MaterialItems.PINK_GEL), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(recipeOutput, ChestBlocks.SKYWARE_CHEST.toStack(), AmountIngredient.of(8, DecorativeBlocks.SUN_PLATE));
        skyMill(recipeOutput, DecorativeBlocks.DISC_BLOCK.toStack(4), Ingredient.of(DecorativeBlocks.SUN_PLATE));
        skyMill(recipeOutput, DecorativeBlocks.SKYWARE_DOOR.toStack(), AmountIngredient.of(2, DecorativeBlocks.SUN_PLATE));
        skyMill(recipeOutput, DecorativeBlocks.SKYWARE_GLASS_DOOR.toStack(), AmountIngredient.of(2, DecorativeBlocks.SUN_PLATE), Ingredient.of(DecorativeBlocks.PURE_GLASS));
        skyMill(recipeOutput, DecorativeBlocks.SUN_PLATE.toStack(25), AmountIngredient.of(25, Items.COBBLESTONE), Ingredient.of(MaterialItems.FALLING_STAR));
        skyMill(recipeOutput, DecorativeBlocks.SUN_PLATE_STAIRS.toStack(), Ingredient.of(DecorativeBlocks.SUN_PLATE));
        skyMill(recipeOutput, DecorativeBlocks.SUN_PLATE_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SUN_PLATE));
        skyMill(recipeOutput, NatureBlocks.CLOUD_BLOCK.toStack(2), Ingredient.of(MaterialItems.WEAVING_CLOUD_COTTON));
        skyMill(recipeOutput, NatureBlocks.RAIN_CLOUD_BLOCK.toStack(), EnvironmentLevelAccess.matcher(null, searchWater, false), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(recipeOutput, NatureBlocks.SNOW_CLOUD_BLOCK.toStack(), EnvironmentLevelAccess.matcher(holderLookup.lookupOrThrow(Registries.BIOME).getOrThrow(Tags.Biomes.IS_COLD_OVERWORLD), null, false), Ingredient.of(NatureBlocks.CLOUD_BLOCK));

        loom(recipeOutput, ModBlocks.SILK_ROPE.toStack(30), Ingredient.of(MaterialItems.SILK));
        loom(recipeOutput, MaterialItems.SILK.toStack(), AmountIngredient.of(7, Items.COBWEB));
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, Tags.Items.GEMS_DIAMOND), Ingredient.of(Tags.Items.GEMS_DIAMOND), ArmorItems.DIAMOND_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_RUBY), Ingredient.of(ModTags.Items.GEMS_RUBY), ArmorItems.RUBY_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMBER), Ingredient.of(ModTags.Items.GEMS_AMBER), ArmorItems.AMBER_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_TOPAZ), Ingredient.of(ModTags.Items.GEMS_TOPAZ), ArmorItems.TOPAZ_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_JADE), Ingredient.of(ModTags.Items.GEMS_JADE), ArmorItems.JADE_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_SAPPHIRE), Ingredient.of(ModTags.Items.GEMS_SAPPHIRE), ArmorItems.SAPPHIRE_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMETHYST), Ingredient.of(ModTags.Items.GEMS_AMETHYST), ArmorItems.AMETHYST_ROBE.toStack());
        loom(recipeOutput, VanityArmorItems.ROBE.toStack(),
                ShapedRecipePattern.of(
                        Map.of(
                                '#', AmountIngredient.of(3, MaterialItems.SILK),
                                'a', AmountIngredient.of(2, MaterialItems.SILK)
                        ),
                        List.of(
                                "#a#",
                                "# #",
                                "# #"
                        )
                )
        );
        loom(recipeOutput, ArmorItems.FLINX_FUR_COAT.toStack(),
                ShapedRecipePattern.of(
                        Map.of(
                                'a', AmountIngredient.of(4, MaterialItems.FLINX_FUR),
                                'b', AmountIngredient.of(2, MaterialItems.SILK),
                                '#', AmountIngredient.of(8, ModTags.Items.GOLD_AND_PLATINUM)
                        ),
                        List.of(
                                "a#a",
                                "bbb",
                                "b b"
                        )
                )
        );

        // 哥布林战旗
        loom(recipeOutput, TMItems.GOBLIN_BATTLE_STANDARD.get().getDefaultInstance(),
                ShapedRecipePattern.of(Map.of(
                                '#', AmountIngredient.of(3, MaterialItems.TATTERED_CLOTH),
                                'b', Ingredient.of(MaterialItems.TATTERED_CLOTH),
                                'a', AmountIngredient.of(2, ItemTags.PLANKS)
                        ), List.of(
                                "###",
                                " b ",
                                "aaa"
                        )
                )
        );

        workshop(recipeOutput, AccessoryItems.ANGLER_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.HIGH_TEST_FISHING_LINE), Ingredient.of(AccessoryItems.TACKLE_BOX), Ingredient.of(TCItems.ANGLER_EARRING));
        workshop(recipeOutput, AccessoryItems.MAGIC_CUFFS.toStack(), Ingredient.of(AccessoryItems.MANA_REGENERATION_BAND), Ingredient.of(TCItems.SHACKLE));
        workshop(recipeOutput, AccessoryItems.CELESTIAL_CUFFS.toStack(), Ingredient.of(AccessoryItems.MAGIC_CUFFS), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET));
        workshop(recipeOutput, AccessoryItems.CELESTIAL_EMBLEM.toStack(), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET), Ingredient.of(TCItems.AVENGER_EMBLEM));
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
        workshop(recipeOutput, AccessoryItems.MANA_FLOWER.toStack(), Ingredient.of(PotionItems.MANA_POTION), Ingredient.of(AccessoryItems.NATURES_GIFT));
        workshop(recipeOutput, AccessoryItems.MANA_REGENERATION_BAND.toStack(), Ingredient.of(TCItems.BAND_OF_REGENERATION), Ingredient.of(AccessoryItems.BAND_OF_STARPOWER));
        workshop(recipeOutput, AccessoryItems.MAGNET_FLOWER.toStack(), Ingredient.of(AccessoryItems.MANA_FLOWER), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET));
        workshop(recipeOutput, AccessoryItems.MANA_CLOAK.toStack(), Ingredient.of(AccessoryItems.MANA_FLOWER), Ingredient.of(TCItems.STAR_CLOAK));
        workshop(recipeOutput, TCItems.FART_IN_A_JAR.toStack(), Ingredient.of(ModItems.WHOOPIE_CUSHION), Ingredient.of(TCItems.CLOUD_IN_A_BOTTLE));
        workshop(recipeOutput, TCItems.ARCHITECT_GIZMO_PACK.toStack(), Ingredient.of(TCItems.BRICK_LAYER), Ingredient.of(TCItems.EXTENDO_GRIP), Ingredient.of(TCItems.PORTABLE_CEMENT_MIXER), Ingredient.of(AccessoryItems.PAINT_SPRAYER));
        workshop(recipeOutput, AccessoryItems.ARCANE_FLOWER.toStack(), Ingredient.of(TCItems.PUTRID_SCENT), Ingredient.of(AccessoryItems.MANA_FLOWER));
        workshop(recipeOutput, TCItems.ANKH_CHARM.toStack(), Ingredient.of(AccessoryItems.ARMOR_BRACING), Ingredient.of(AccessoryItems.MEDICATED_BANDAGE), Ingredient.of(TCItems.THE_PLAN), Ingredient.of(AccessoryItems.COUNTERCURSE_MANTRA), Ingredient.of(AccessoryItems.REFLECTIVE_SHADES));
        workshop(recipeOutput, AccessoryItems.BAND_OF_STARPOWER.toStack(), EnvironmentLevelAccess.matcher(null, null, true), Ingredient.of(ConsumableItems.MANA_CRYSTAL), Ingredient.of(TCItems.PANIC_NECKLACE));
        workshop(recipeOutput, TCItems.PANIC_NECKLACE.toStack(), EnvironmentLevelAccess.matcher(null, null, true), Ingredient.of(ConsumableItems.LIFE_CRYSTAL), Ingredient.of(AccessoryItems.BAND_OF_STARPOWER));

        solidifier(recipeOutput, DecorativeBlocks.BLUE_GEL_BLOCK.toStack(),
                ShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(MaterialItems.GEL)),
                        List.of(
                                "##",
                                "##"
                        )
                )
        );
        solidifier(recipeOutput, DecorativeBlocks.PINK_GEL_BLOCK.toStack(),
                ShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(MaterialItems.PINK_GEL)
                        ),
                        List.of(
                                "##",
                                "##"
                        )
                )
        );
        solidifier(recipeOutput, DecorativeBlocks.FROZEN_GEL_BLOCK.toStack(),
                ShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(DecorativeBlocks.BLUE_GEL_BLOCK),
                                'a', Ingredient.of(Items.BLUE_ICE)
                        ),
                        List.of(
                                "a",
                                "#"
                        )
                )
        );


        hellforge(recipeOutput, MaterialItems.HELLSTONE_INGOT.toStack(), 0.2F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_HELLSTONE), Ingredient.of(Items.OBSIDIAN));
        hellforge(recipeOutput, MaterialItems.COBALT_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_COBALT));
        hellforge(recipeOutput, MaterialItems.PALLADIUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_PALLADIUM));
        hellforge(recipeOutput, MaterialItems.ORICHALCUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_ORICHALCUM));
        hellforge(recipeOutput, MaterialItems.MYTHRIL_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_MYTHRIL));
        hellforge(recipeOutput, ToolItems.LAVAPROOF_BUG_NET.toStack(), 0.3F, 200, false, AmountIngredient.of(15, MaterialItems.HELLSTONE_INGOT), Ingredient.of(ToolItems.BUG_NET));

        hellforge(recipeOutput, DecorativeBlocks.OBSIDIAN_BRICKS.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(Items.OBSIDIAN));
        hellforge(recipeOutput, DecorativeBlocks.METEORITE_BRICKS.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(ModTags.Items.RAW_MATERIALS_METEORITE));
        hellforge(recipeOutput, OreBlocks.HELLSTONE_BRICKS.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(ModTags.Items.RAW_MATERIALS_HELLSTONE));

        hellforge(recipeOutput, ArmorItems.OBSIDIAN_HELMET.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_HELMET));
        hellforge(recipeOutput, ArmorItems.OBSIDIAN_CHESTPLATE.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_CHESTPLATE));
        hellforge(recipeOutput, ArmorItems.OBSIDIAN_LEGGINGS.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_LEGGINGS));
        hellforge(recipeOutput, ArmorItems.OBSIDIAN_BOOTS.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_BOOTS));
        hellforge(recipeOutput, TCItems.OBSIDIAN_SKULL.toStack(), 0.5F, 200, false, AmountIngredient.of(10, Items.OBSIDIAN), Ingredient.of(Items.SKELETON_SKULL));

        fletchingTable(recipeOutput, ArrowItems.FLAMING_ARROW.toStack(25), Ingredient.EMPTY, AmountIngredient.of(25, Items.ARROW), Ingredient.of(ModTags.Items.TORCH));
        fletchingTable(recipeOutput, "_from_feather", new ItemStack(Items.ARROW, 20), Ingredient.of(Items.FEATHER), AmountIngredient.of(5, Items.STICK), Ingredient.of(Items.FLINT));
        fletchingTable(recipeOutput, "_from_wool", new ItemStack(Items.ARROW, 35), Ingredient.of(ItemTags.WOOL), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS));
        fletchingTable(recipeOutput, ArrowItems.FLY_FISH_ARROW.toStack(10), Ingredient.of(MaterialItems.FILAMENTOUS_FIN), AmountIngredient.of(10, Items.ARROW), Ingredient.of());
        fletchingTable(recipeOutput, ArrowItems.FOSSIL_ARROW.toStack(25), Ingredient.of(), AmountIngredient.of(25, Items.ARROW), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        fletchingTable(recipeOutput, ArrowItems.HELLFIRE_ARROW.toStack(64), Ingredient.of(), AmountIngredient.of(64, Items.ARROW), Ingredient.of(MaterialItems.HELLSTONE_INGOT));
        fletchingTable(recipeOutput, ArrowItems.STAR_ARROW.toStack(10), Ingredient.of(), AmountIngredient.of(10, Items.ARROW), Ingredient.of(MaterialItems.FALLING_STAR));
        fletchingTable(recipeOutput, ArrowItems.UNHOLY_ARROW.toStack(5), Ingredient.of(), AmountIngredient.of(5, Items.ARROW), Ingredient.of(ModTags.Items.EVIL_MATERIAL));

        altar(recipeOutput, ConsumableItems.BLOODY_SPINE.toStack(), AmountIngredient.of(30, ConsumableItems.VICIOUS_POWDER), AmountIngredient.of(15, MaterialItems.VERTEBRA));
        altar(recipeOutput, ConsumableItems.WORM_FOOD.toStack(), AmountIngredient.of(30, ConsumableItems.VILE_POWDER), AmountIngredient.of(15, MaterialItems.ROTTEN_CHUNK));
        altar(recipeOutput, ConsumableItems.SUSPICIOUS_LOOKING_EYE.toStack(), AmountIngredient.of(6, MaterialItems.LENS));
        altar(recipeOutput, SwordItems.NIGHTS_EDGE.toStack(), Ingredient.of(SwordItems.BLOOD_BUTCHERER, SwordItems.LIGHTS_BANE), Ingredient.of(SwordItems.MURAMASA), Ingredient.of(SwordItems.BLADE_OF_GRASS), Ingredient.of(SwordItems.VOLCANO));
        altar(recipeOutput, ToolItems.METEOR_COMPASS.toStack(), AmountIngredient.of(4, ModTags.Items.EVIL_INGOT), AmountIngredient.of(4, MaterialItems.FALLING_STAR));
        altar(recipeOutput, ConsumableItems.SLIME_CROWN.toStack(), AmountIngredient.of(20, MaterialItems.GEL), Ingredient.of(VanityArmorItems.GOLD_CROWN, VanityArmorItems.PLATINUM_CROWN));

        alchemyTable(recipeOutput, PotionItems.ARCHERY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.LENS), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.SWIFTNESS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.CACTUS));
        alchemyTable(recipeOutput, PotionItems.THORNS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(Items.CACTUS));
        alchemyTable(recipeOutput, PotionItems.BUILDER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.CRATE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.AMBER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.DANGERSENSE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(Items.COBWEB));
        alchemyTable(recipeOutput, PotionItems.ENDURANCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.ARMORED_CAVE_FISH), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.FEATHERFALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.FEATHER));
        alchemyTable(recipeOutput, ConsumableItems.FERTILIZER.toStack(), Ingredient.of(ModBlocks.POO.get()), AmountIngredient.of(3, NatureBlocks.ASH_BLOCK), AmountIngredient.of(3, Items.BONE));
        alchemyTable(recipeOutput, PotionItems.FISHING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(DecorativeBlocks.CRISPY_HONEY_BLOCK), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.FLIPPER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.GILLS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(ModTags.Items.CORALS));
        alchemyTable(recipeOutput, PotionItems.GRAVITATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.FEATHER));
        alchemyTable(recipeOutput, PotionItems.GREATER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PINK_PEARL));
        alchemyTable(recipeOutput, PotionItems.HEALING_POTION.toStack(), Ingredient.of(PotionItems.LESSER_HEALING_POTION), Ingredient.of(MaterialItems.GLOWING_MUSHROOM), Ingredient.of(PotionItems.LESSER_HEALING_POTION));
        alchemyTable(recipeOutput, PotionItems.HEART_REACH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.SCARLET_TIGER_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.HUNTER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(recipeOutput, PotionItems.INFERNO_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.FLASHFIN_KOI), AmountIngredient.of(2, FoodItems.OBSIDIFISH), Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(recipeOutput, PotionItems.INVISIBILITY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.IRON_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(Items.RAW_IRON, MaterialItems.RAW_LEAD));
        alchemyTable(recipeOutput, PotionItems.LESSER_HEALING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLE), Ingredient.of(MaterialItems.LIFE_MUSHROOM), AmountIngredient.of(2, MaterialItems.GEL));
        alchemyTable(recipeOutput, PotionItems.LESSER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PEARL));
        alchemyTable(recipeOutput, PotionItems.LIFEFORCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.COLORFUL_MINERAL_FISH), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(recipeOutput, PotionItems.LOVE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PRINCESS_FISH), Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(recipeOutput, PotionItems.LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.BLACK_PEARL));
        alchemyTable(recipeOutput, PotionItems.MAGIC_POWER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(recipeOutput, PotionItems.MANA_REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(recipeOutput, PotionItems.MINING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.ANTLION_MANDIBLE), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.NIGHT_OWL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.OBSIDIAN_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(Items.OBSIDIAN));
        alchemyTable(recipeOutput, PotionItems.RAGE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.BLOODY_PIRANHAS), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.RANDOM_TELEPORT_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.CHAOS_FISH), Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(recipeOutput, PotionItems.RECALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(recipeOutput, PotionItems.REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.LIFE_MUSHROOM));
        alchemyTable(recipeOutput, PotionItems.SHINE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.GLOWING_MUSHROOM));
        alchemyTable(recipeOutput, PotionItems.SPELUNKER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.RAW_PLATINUM, Items.RAW_GOLD));
        alchemyTable(recipeOutput, PotionItems.STINK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.STINKY_FISH), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.TITAN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(ConsumableItems.DUNGEON_DEMON_BONE), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(recipeOutput, PotionItems.WATER_WALKING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(recipeOutput, PotionItems.WORMHOLE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(recipeOutput, PotionItems.WRATH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.EBONY_KOI), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(recipeOutput, PotionItems.AMMO_RESERVATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PISCES_FIN_COD), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(recipeOutput, PotionItems.SUMMONING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MOTTLED_OILFISH), Ingredient.of(MaterialItems.MOONGLOW));

        Ingredient emptyDropper = Ingredient.of(ToolItems.EMPTY_DROPPER);
        crystalBlock(recipeOutput, TGItems.ENDLESS_MUSKET_POUCH.toStack(), AmountIngredient.of(3996, TGItems.MUSKET_BULLET));
        crystalBlock(recipeOutput, ManaWeaponItems.CURSED_FLAMES.toStack(), Ingredient.of(MaterialItems.SPELL_TOME),AmountIngredient.of(20, MaterialItems.CURSED_FLAME),AmountIngredient.of(15, MaterialItems.SOUL_OF_NIGHT));
        crystalBlock(recipeOutput, ManaWeaponItems.CRYSTAL_STORM.toStack(), Ingredient.of(MaterialItems.SPELL_TOME),AmountIngredient.of(20, MaterialItems.CRYSTAL_SHARDS),AmountIngredient.of(15, MaterialItems.SOUL_OF_LIGHT));
        crystalBlock(recipeOutput, ManaWeaponItems.GOLDEN_SHOWER.toStack(), Ingredient.of(MaterialItems.SPELL_TOME),AmountIngredient.of(20, MaterialItems.ICHOR),AmountIngredient.of(15, MaterialItems.SOUL_OF_NIGHT));
        crystalBlock(recipeOutput, ToolItems.MAGIC_SAND_DROPPER.toStack(3), AmountIngredient.of(3, emptyDropper), Ingredient.of(Tags.Items.SANDS));
        crystalBlock(recipeOutput, ToolItems.MAGIC_HONEY_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchHoney(holderLookup), false), emptyDropper);
        crystalBlock(recipeOutput, ToolItems.MAGIC_LAVA_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchLava(holderLookup), false), emptyDropper);
        crystalBlock(recipeOutput, ToolItems.MAGIC_WATER_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchWater, false), emptyDropper);

        hardmodeForge(recipeOutput, MaterialItems.ADAMANTITE_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_ADAMANTITE));
        hardmodeForge(recipeOutput, MaterialItems.TITANIUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_TITANIUM));
        hardmodeForge(recipeOutput, MaterialItems.CHLOROPHYTE_INGOT.toStack(), 1, 200, true, AmountIngredient.of(5, MaterialItems.RAW_CHLOROPHYTE));
        hardmodeForge(recipeOutput, DecorativeBlocks.CRYSTAL_BLOCK.toStack(5), 0.1F, 20, false, AmountIngredient.of(5, Blocks.STONE), Ingredient.of(MaterialItems.CRYSTAL_SHARDS));
        hardmodeForge(recipeOutput, MaterialItems.SPECTRE_INGOT.toStack(2), 2, 200, true, AmountIngredient.of(2, MaterialItems.CHLOROPHYTE_INGOT), Ingredient.of(MaterialItems.ECTOPLASM));


        dyeVat(recipeOutput, PaintItems.DEEP_RED_PAINT.toStack(), AmountIngredient.of(2, PaintItems.RED_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_ORANGE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.ORANGE_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_YELLOW_PAINT.toStack(), AmountIngredient.of(2, PaintItems.YELLOW_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_LIME_PAINT.toStack(), AmountIngredient.of(2, PaintItems.LIME_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_GREEN_PAINT.toStack(), AmountIngredient.of(2, PaintItems.GREEN_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_TEAL_PAINT.toStack(), AmountIngredient.of(2, PaintItems.TEAL_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_CYAN_PAINT.toStack(), AmountIngredient.of(2, PaintItems.CYAN_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_SKY_BLUE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.SKY_BLUE_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_BLUE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.BLUE_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_PURPLE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.PURPLE_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_VIOLET_PAINT.toStack(), AmountIngredient.of(2, PaintItems.VIOLET_PAINT));
        dyeVat(recipeOutput, PaintItems.DEEP_PINK_PAINT.toStack(), AmountIngredient.of(2, PaintItems.PINK_PAINT));
        Ingredient silverDye = Ingredient.of(VanityArmorItems.SILVER_DYE);
        dyeVat(recipeOutput, VanityArmorItems.BLACK_DYE.toStack(2), Ingredient.of(MaterialItems.BLACK_INK));
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_RED_DYE.toStack(), Ingredient.of(VanityArmorItems.RED_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_ORANGE_DYE.toStack(), Ingredient.of(VanityArmorItems.ORANGE_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_YELLOW_DYE.toStack(), Ingredient.of(VanityArmorItems.YELLOW_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_LIME_DYE.toStack(), Ingredient.of(VanityArmorItems.LIME_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_GREEN_DYE.toStack(), Ingredient.of(VanityArmorItems.GREEN_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_TEAL_DYE.toStack(), Ingredient.of(VanityArmorItems.TEAL_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_CYAN_DYE.toStack(), Ingredient.of(VanityArmorItems.CYAN_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_SKY_BLUE_DYE.toStack(), Ingredient.of(VanityArmorItems.SKY_BLUE_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_BLUE_DYE.toStack(), Ingredient.of(VanityArmorItems.BLUE_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_PURPLE_DYE.toStack(), Ingredient.of(VanityArmorItems.PURPLE_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_VIOLET_DYE.toStack(), Ingredient.of(VanityArmorItems.VIOLET_DYE), silverDye);
        dyeVat(recipeOutput, VanityArmorItems.BRIGHT_PINK_DYE.toStack(), Ingredient.of(VanityArmorItems.PINK_DYE), silverDye);
    }

    protected <T extends AbstractCookingRecipe> void cooking(RecipeOutput recipeOutput, AbstractCookingRecipe.Factory<T> factory, String prefix, String suffix, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, factory.create("", CookingBookCategory.MISC, ingredient, result, experience, cookingTime), createAdvancementHolder(recipeOutput, id, ingredient));
    }

    protected void solidifier(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("solidifier/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SolidifierRecipe(result, pattern), null);
    }

    protected void solidifier(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("solidifier/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new SolidifierRecipe(result, zingredients), null);
    }


    protected void skyMill(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sky_mill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SkyMillRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void skyMill(RecipeOutput recipeOutput, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sky_mill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SkyMillRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), environment), null);
    }

    protected void workshop(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("workshop/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void workshop(RecipeOutput recipeOutput, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("workshop/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), environment), null);
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

    protected void fletchingTable(RecipeOutput recipeOutput, ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        ResourceLocation id = Confluence.asResource("fletching_table/" + getItemName(result.getItem()));
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

    protected void crystalBlock(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("crystal_block/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new CrystalBallRecipe(result, zingredients, EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void crystalBlock(RecipeOutput recipeOutput, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("crystal_block/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new CrystalBallRecipe(result, zingredients, environment), null);
    }

    protected void hardmodeForge(RecipeOutput recipeOutput, ItemStack result, float experience, int cookingTime, boolean requiresFuel, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hardmode_forge/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HardmodeForgeRecipe(result, zingredients, experience, cookingTime, requiresFuel), null);
    }

    protected void loom(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("loom/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new LoomRecipe(result, pattern), null);
    }

    protected void loom(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("loom/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new LoomRecipe(result, zingredients), null);
    }

    protected void dyeVat(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("dye_vat/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new DyeVatRecipe(result, zingredients), null);
    }


    public static AdvancementHolder createAdvancementHolder(RecipeOutput recipeOutput, ResourceLocation id, NonNullList<Ingredient> ingredients) {
        Set<Item> itemCounter = new HashSet<>();
        Set<TagKey<Item>> tagCounter = new HashSet<>();
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        for (Ingredient ingredient : ingredients) {
            Ingredient.Value[] values;
            ICustomIngredient customIngredient = ingredient.getCustomIngredient();
            if (customIngredient == null) {
                values = ingredient.getValues();
            } else {
                values = customIngredient.getItems().map(Ingredient.ItemValue::new).toArray(Ingredient.Value[]::new);
            }
            for (Ingredient.Value value : values) {
                if (value instanceof Ingredient.ItemValue(ItemStack itemStack)) {
                    Item item = itemStack.getItem();
                    if (itemCounter.contains(item)) continue;
                    itemCounter.add(item);
                    builder.addCriterion(getHasName(item), has(item));
                } else if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) {
                    if (tagCounter.contains(tag)) continue;
                    tagCounter.add(tag);
                    builder.addCriterion("has_tag_" + tag.location().getPath(), has(tag));
                }
            }
        }
        return builder.build(id.withPrefix("recipes/confluence/"));
    }

    public static AdvancementHolder createAdvancementHolder(RecipeOutput recipeOutput, ResourceLocation id, Ingredient ingredient) {
        Set<Item> itemCounter = new HashSet<>();
        Set<TagKey<Item>> tagCounter = new HashSet<>();
        Advancement.Builder builder = recipeOutput.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);
        Ingredient.Value[] values;
        ICustomIngredient customIngredient = ingredient.getCustomIngredient();
        if (customIngredient == null) {
            values = ingredient.getValues();
        } else {
            values = customIngredient.getItems().map(Ingredient.ItemValue::new).toArray(Ingredient.Value[]::new);
        }
        for (Ingredient.Value value : values) {
            if (value instanceof Ingredient.ItemValue(ItemStack itemStack)) {
                Item item = itemStack.getItem();
                if (itemCounter.contains(item)) continue;
                itemCounter.add(item);
                builder.addCriterion(getHasName(item), has(item));
            } else if (value instanceof Ingredient.TagValue(TagKey<Item> tag)) {
                if (tagCounter.contains(tag)) continue;
                tagCounter.add(tag);
                builder.addCriterion("has_tag_" + tag.location().getPath(), has(tag));
            }
        }
        return builder.build(id.withPrefix("recipes/confluence/"));
    }

    public static EnvironmentLevelAccess.SearchContext searchWater(HolderLookup.Provider holderLookup) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(new OrHolderSet<>(
                        holderLookup.lookupOrThrow(Registries.BLOCK).getOrThrow(TFTags.SINKS),
                        HolderSet.direct(Blocks.WATER_CAULDRON.builtInRegistryHolder())
                )),
                List.of(new StatePropertiesPredicate(List.of(new StatePropertiesPredicate.PropertyMatcher(
                        BlockStateProperties.WATERLOGGED.getName(), new StatePropertiesPredicate.ExactMatcher("true")
                )))),
                Optional.of(holderLookup.lookupOrThrow(Registries.FLUID).getOrThrow(Tags.Fluids.WATER))
        );
    }

    public static EnvironmentLevelAccess.SearchContext searchHoney(HolderLookup.Provider holderLookup) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(HolderSet.direct(ModBlocks.HONEY_CAULDRON)),
                List.of(),
                Optional.of(holderLookup.lookupOrThrow(Registries.FLUID).getOrThrow(Tags.Fluids.HONEY))
        );
    }

    public static EnvironmentLevelAccess.SearchContext searchLava(HolderLookup.Provider holderLookup) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(HolderSet.direct(Blocks.LAVA_CAULDRON.builtInRegistryHolder())),
                List.of(),
                Optional.of(holderLookup.lookupOrThrow(Registries.FLUID).getOrThrow(Tags.Fluids.LAVA))
        );
    }

    private final List<String> baseRobePattern = List.of(
            "bbb",
            "a#a",
            "a a"
    );

    protected void baseRobe(RecipeOutput recipeOutput, Ingredient robe, Ingredient gem, Ingredient handle, ItemStack result) {
        loom(recipeOutput, result, ShapedRecipePattern.of(Map.of(
                '#', robe,
                'b', gem,
                'a', handle
        ), baseRobePattern));
    }
}
