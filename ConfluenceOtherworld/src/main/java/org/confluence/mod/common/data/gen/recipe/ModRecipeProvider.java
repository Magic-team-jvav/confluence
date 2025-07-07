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
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
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
        //高炉
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.AMBER_ORE_SMELTING), MaterialItems.AMBER.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.RUBY_ORE_SMELTING), MaterialItems.RUBY.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.TOPAZ_ORE_SMELTING), MaterialItems.TOPAZ.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.JADE_ORE_SMELTING), MaterialItems.JADE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.SAPPHIRE_ORE_SMELTING), MaterialItems.SAPPHIRE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(ModTags.Items.AMBER_ORE_SMELTING), MaterialItems.AMETHYST.toStack(), 1.0F, 100);
        
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
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.METEORITE_ORE), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.COLD_CRYSTAL_ORE), MaterialItems.COLD_CRYSTAL.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.GELSTONE_ORE), MaterialItems.GELSTONE.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.SPORE_ROOT_BLOCK), MaterialItems.SPORE_ROOT.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.OPAL_ORE), MaterialItems.OPAL.toStack(), 1.0F, 100);
        cooking(recipeOutput, BlastingRecipe::new, "blasting/", "", Ingredient.of(OreBlocks.WINTER_MARROW_BLOCK), MaterialItems.WINTER_MARROW.toStack(), 1.0F, 100);
        // 熔炉
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.AMBER_ORE_SMELTING), MaterialItems.AMBER.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.RUBY_ORE_SMELTING), MaterialItems.RUBY.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.TOPAZ_ORE_SMELTING), MaterialItems.TOPAZ.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.JADE_ORE_SMELTING), MaterialItems.JADE.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.SAPPHIRE_ORE_SMELTING), MaterialItems.SAPPHIRE.toStack(), 1.0F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.AMETHYST_ORE_SMELTING), MaterialItems.AMETHYST.toStack(), 1.0F, 200);

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
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(OreBlocks.METEORITE_ORE), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 200);
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
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(ModTags.Items.GOLD_COOKING), Items.GOLD_NUGGET.getDefaultInstance(), 0.1F, 200);


        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 200);
        cooking(recipeOutput, SmeltingRecipe::new, "smelting/", "", Ingredient.of(FoodItems.ATLANTIC_COD,FoodItems.PISCES_FIN_COD,FoodItems.SEA_BASS,FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);

        cooking(recipeOutput, SmokingRecipe::new, "smoking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.1F, 100);
        cooking(recipeOutput, CampfireCookingRecipe::new, "campfire_cooking/", "_from_atlantic_cod", Ingredient.of(FoodItems.ATLANTIC_COD), Items.COOKED_COD.getDefaultInstance(), 0.1F, 200);

        recipeOutput.accept(Confluence.asResource("smithing/amber_hook"), new SmithingTransformRecipe(
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_BLOCK),
                HookItems.AMBER_HOOK.toStack()
        ), null);


        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_ICE_BRICKS.toStack(4), Ingredient.of(Blocks.BLUE_ICE));
        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_ICE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.BLUE_ICE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_ICE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.BLUE_ICE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PACKED_ICE_BRICKS.toStack(4), Ingredient.of(Blocks.PACKED_ICE));
        stonecutting(recipeOutput, "", DecorativeBlocks.PACKED_ICE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PACKED_ICE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PACKED_ICE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.PACKED_ICE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.AETHERIUM_BRICKS.toStack(4), Ingredient.of(NatureBlocks.AETHERIUM_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.OBSIDIAN_BRICKS.toStack(4), Ingredient.of(Blocks.OBSIDIAN));
        stonecutting(recipeOutput, "", DecorativeBlocks.OBSIDIAN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.OBSIDIAN_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_BRICK_COLUMN.toStack(1), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.BLUE_BRICK_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.BLUE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.GREEN_BRICK_COLUMN.toStack(1), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.GREEN_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.GREEN_BRICK_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.GREEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PINK_BRICK_COLUMN.toStack(1), Ingredient.of(DecorativeBlocks.PINK_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PINK_BRICK_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PINK_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PINK_BRICK_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.PINK_BRICKS));


        stonecutting(recipeOutput, "", MaterialItems.CHINA_BOWL.toStack(1), Ingredient.of(Items.WHITE_TERRACOTTA));
        stonecutting(recipeOutput, "", MaterialItems.CHINA_PLATE.toStack(1), Ingredient.of(Items.WHITE_TERRACOTTA));
        stonecutting(recipeOutput, "", DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(1), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.GRANITE_COLUMN.toStack(1), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));
        stonecutting(recipeOutput, "", DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(1), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(1), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.SNOW_BRICKS.toStack(1), Ingredient.of(Items.SNOW_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.SNOW_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SNOW_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.SNOW_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.SNOW_BRICKS));

        stonecutting(recipeOutput, "", DecorativeBlocks.GOLDEN_BRICKS.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.GOLDEN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.GOLDEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.GOLDEN_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.GOLDEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.COPPER_BRICKS.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.COPPER_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.COPPER_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.COPPER_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.COPPER_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.IRON_BRICKS.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.IRON_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.IRON_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.IRON_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.IRON_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.TIN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.TIN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.TIN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.TIN_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.TIN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.LEAD_BRICKS.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.LEAD_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.LEAD_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.LEAD_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.LEAD_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.SILVER_BRICKS.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.SILVER_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SILVER_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.SILVER_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.SILVER_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.TUNGSTEN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.TUNGSTEN_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.TUNGSTEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.TUNGSTEN_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.TUNGSTEN_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PLATINUM_BRICKS.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.PLATINUM_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PLATINUM_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PLATINUM_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.PLATINUM_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.DEMONITE_ORE_BRICKS.toStack(9), Ingredient.of(OreBlocks.DEMONITE_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.DEMONITE_ORE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.DEMONITE_ORE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.DEMONITE_ORE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.DEMONITE_ORE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMTANE_ORE_BRICKS.toStack(9), Ingredient.of(OreBlocks.CRIMTANE_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMTANE_ORE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.CRIMTANE_ORE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMTANE_ORE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.CRIMTANE_ORE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.METEORITE_BRICKS.toStack(9), Ingredient.of(OreBlocks.METEORITE_BLOCK));
        stonecutting(recipeOutput, "", DecorativeBlocks.METEORITE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.METEORITE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.METEORITE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.METEORITE_BRICKS));

        stonecutting(recipeOutput, "", DecorativeBlocks.EBONSTONE_BRICKS.toStack(1), Ingredient.of(NatureBlocks.EBONSTONE));
        stonecutting(recipeOutput, "", DecorativeBlocks.EBONSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.EBONSTONE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.EBONSTONE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.EBONSTONE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMSTONE_BRICKS.toStack(9), Ingredient.of(NatureBlocks.CRIMSTONE));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.CRIMSTONE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.CRIMSTONE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.CRIMSTONE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PEARLSTONE_BRICKS.toStack(9), Ingredient.of(NatureBlocks.PEARLSTONE));
        stonecutting(recipeOutput, "", DecorativeBlocks.PEARLSTONE_BRICKS_SLAB.toStack(2), Ingredient.of(DecorativeBlocks.PEARLSTONE_BRICKS));
        stonecutting(recipeOutput, "", DecorativeBlocks.PEARLSTONE_BRICKS_STAIRS.toStack(1), Ingredient.of(DecorativeBlocks.PEARLSTONE_BRICKS));


        skyMill(recipeOutput, DecorativeBlocks.BOUNCY_CLOUD_BLOCK.toStack(), Ingredient.of(MaterialItems.PINK_GEL), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(recipeOutput, ChestBlocks.SKYWARE_CHEST.toStack(), AmountIngredient.of(5,DecorativeBlocks.SUN_PLATE));

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

        hellforge(recipeOutput, MaterialItems.HELLSTONE_INGOT.toStack(), 0.2f, 100, true, AmountIngredient.of(3, MaterialItems.RAW_HELLSTONE), Ingredient.of(Items.OBSIDIAN));

        fletchingTable(recipeOutput, "", ArrowItems.FLAMING_ARROW.toStack(25), Ingredient.EMPTY, AmountIngredient.of(25, Items.ARROW), Ingredient.of(ModTags.Items.TORCH));
        fletchingTable(recipeOutput, "_from_feather", new ItemStack(Items.ARROW, 20), Ingredient.of(Items.FEATHER), AmountIngredient.of(5, Items.STICK), Ingredient.of(Items.FLINT));
        fletchingTable(recipeOutput, "_from_wool", new ItemStack(Items.ARROW, 35), Ingredient.of(ItemTags.WOOL), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS));
        fletchingTable(recipeOutput, "", ArrowItems.FLY_FISH_ARROW.toStack(10), Ingredient.of(MaterialItems.FILAMENTOUS_FIN), AmountIngredient.of(10, Items.ARROW), Ingredient.of());
        fletchingTable(recipeOutput, "", ArrowItems.FOSSIL_ARROW.toStack(25), Ingredient.of(), AmountIngredient.of(25, Items.ARROW), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        fletchingTable(recipeOutput, "", ArrowItems.HELLFIRE_ARROW.toStack(64), Ingredient.of(), AmountIngredient.of(64, Items.ARROW), Ingredient.of(MaterialItems.HELLSTONE_INGOT));
        fletchingTable(recipeOutput, "", ArrowItems.STAR_ARROW.toStack(10), Ingredient.of(), AmountIngredient.of(10, Items.ARROW), Ingredient.of(MaterialItems.FALLING_STAR));
        fletchingTable(recipeOutput, "", ArrowItems.UNHOLY_ARROW.toStack(5), Ingredient.of(), AmountIngredient.of(5, Items.ARROW), Ingredient.of(ModTags.Items.EVIL_MATERIAL));

        altar(recipeOutput, ConsumableItems.BLOODY_SPINE.toStack(), AmountIngredient.of(30, ConsumableItems.VICIOUS_POWDER), AmountIngredient.of(15, MaterialItems.VERTEBRA));
        altar(recipeOutput, ConsumableItems.WORM_FOOD.toStack(), AmountIngredient.of(30, ConsumableItems.VILE_POWDER), AmountIngredient.of(15, MaterialItems.ROTTEN_BONE));
        altar(recipeOutput, ConsumableItems.SUSPICIOUS_LOOKING_EYE.toStack(), AmountIngredient.of(6, MaterialItems.LENS));
        altar(recipeOutput, SwordItems.NIGHTS_EDGE.toStack(), Ingredient.of(SwordItems.BLOOD_BUTCHERER, SwordItems.LIGHTS_BANE), Ingredient.of(SwordItems.MURAMASA), Ingredient.of(SwordItems.BLADE_OF_GRASS), Ingredient.of(SwordItems.VOLCANO));
        altar(recipeOutput, ToolItems.METEOR_COMPASS.toStack(), AmountIngredient.of(4,ModTags.Items.EVIL_INGOT),AmountIngredient.of(4,MaterialItems.FALLING_STAR));
        altar(recipeOutput, ConsumableItems.SLIME_CROWN.toStack(), AmountIngredient.of(20,MaterialItems.GEL),Ingredient.of(VanityArmorItems.GOLD_CROWN,VanityArmorItems.PLATINUM_CROWN));

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

        sawmill(recipeOutput, FunctionalBlocks.KEG.toStack(), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Tags.Items.BARRELS_WOODEN),
                'B', Ingredient.of(Tags.Items.FENCES_WOODEN),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER)
        ), List.of(
                "AC",
                "B "
        )));
        sawmill(recipeOutput, new ItemStack(Items.OAK_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.OAK_LOG,Items.OAK_WOOD,Items.STRIPPED_OAK_LOG,Items.STRIPPED_OAK_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.ACACIA_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.ACACIA_LOG,Items.ACACIA_WOOD,Items.STRIPPED_ACACIA_LOG,Items.STRIPPED_ACACIA_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.BAMBOO_PLANKS), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.BAMBOO)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.CHERRY_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.CHERRY_LOG,Items.CHERRY_WOOD,Items.STRIPPED_CHERRY_LOG,Items.STRIPPED_CHERRY_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.CRIMSON_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.CRIMSON_STEM,Items.STRIPPED_CRIMSON_STEM)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.DARK_OAK_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.DARK_OAK_LOG,Items.DARK_OAK_WOOD,Items.STRIPPED_DARK_OAK_LOG,Items.STRIPPED_DARK_OAK_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.JUNGLE_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.JUNGLE_LOG,Items.JUNGLE_WOOD,Items.STRIPPED_JUNGLE_LOG,Items.STRIPPED_JUNGLE_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.BIRCH_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.BIRCH_LOG,Items.BIRCH_WOOD,Items.STRIPPED_BIRCH_LOG,Items.STRIPPED_BIRCH_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.MANGROVE_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.MANGROVE_LOG,Items.MANGROVE_WOOD,Items.STRIPPED_MANGROVE_LOG,Items.STRIPPED_MANGROVE_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.SPRUCE_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.SPRUCE_LOG,Items.SPRUCE_WOOD,Items.STRIPPED_SPRUCE_LOG,Items.STRIPPED_SPRUCE_WOOD)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(Items.WARPED_PLANKS, 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Items.WARPED_STEM,Items.STRIPPED_WARPED_STEM)
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getLog(),NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(),NatureBlocks.EBONY_LOG_BLOCKS.getWood(),NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getLog(),NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(),NatureBlocks.SHADOW_LOG_BLOCKS.getWood(),NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog(),NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedLog(),NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getWood(),NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getLog(),NatureBlocks.LIVING_LOG_BLOCKS.getStrippedLog(),NatureBlocks.LIVING_LOG_BLOCKS.getWood(),NatureBlocks.LIVING_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getLog(),NatureBlocks.ASH_LOG_BLOCKS.getStrippedLog(),NatureBlocks.ASH_LOG_BLOCKS.getWood(),NatureBlocks.ASH_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLog(),NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStrippedLog(),NatureBlocks.LIVING_MAHOGANY_BLOCKS.getWood(),NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getLog(),NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedLog(),NatureBlocks.BAOBAB_LOG_BLOCKS.getWood(),NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getLog(),NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(),NatureBlocks.PALM_LOG_BLOCKS.getWood(),NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));
        sawmill(recipeOutput, new ItemStack(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), 9), ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getLog(),NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(),NatureBlocks.PEARL_LOG_BLOCKS.getWood(),NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood())
        ), List.of(
                "A"
        )));

        baseWooden(recipeOutput, new ItemStack(Items.OAK_SLAB, 4), Ingredient.of(Items.OAK_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.ACACIA_SLAB, 4), Ingredient.of(Items.ACACIA_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.BAMBOO_SLAB, 4), Ingredient.of(Items.BAMBOO_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.CHERRY_SLAB, 4), Ingredient.of(Items.CHERRY_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.CRIMSON_SLAB, 4), Ingredient.of(Items.CRIMSON_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.DARK_OAK_SLAB, 4), Ingredient.of(Items.DARK_OAK_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.JUNGLE_SLAB, 4), Ingredient.of(Items.JUNGLE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.BIRCH_SLAB, 4), Ingredient.of(Items.BIRCH_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.MANGROVE_SLAB, 4), Ingredient.of(Items.MANGROVE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.SPRUCE_SLAB, 4), Ingredient.of(Items.SPRUCE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.WARPED_SLAB, 4), Ingredient.of(Items.WARPED_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.EBONY_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.SHADOW_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.LIVING_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.ASH_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.BAOBAB_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.PALM_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.PEARL_LOG_BLOCKS.getSlab(), 4), Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()), List.of("A"));

        baseWooden(recipeOutput, new ItemStack(Items.OAK_STAIRS, 2), Ingredient.of(Items.OAK_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.ACACIA_STAIRS, 2), Ingredient.of(Items.ACACIA_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.BAMBOO_STAIRS, 2), Ingredient.of(Items.BAMBOO_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.CHERRY_STAIRS, 2), Ingredient.of(Items.CHERRY_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.CRIMSON_STAIRS, 2), Ingredient.of(Items.CRIMSON_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.DARK_OAK_STAIRS, 2), Ingredient.of(Items.DARK_OAK_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.JUNGLE_STAIRS, 2), Ingredient.of(Items.JUNGLE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.BIRCH_STAIRS, 2), Ingredient.of(Items.BIRCH_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.MANGROVE_STAIRS, 2), Ingredient.of(Items.MANGROVE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.SPRUCE_STAIRS, 2), Ingredient.of(Items.SPRUCE_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(Items.WARPED_STAIRS, 2), Ingredient.of(Items.WARPED_PLANKS), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.EBONY_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.SHADOW_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.LIVING_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.ASH_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.BAOBAB_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.PALM_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.PALM_LOG_BLOCKS.getPlanks()), List.of("A"));
        baseWooden(recipeOutput, new ItemStack(NatureBlocks.PEARL_LOG_BLOCKS.getStairs(), 2), Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()), List.of("A"));
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

    protected void sawmill(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SawmillRecipe(result, pattern), null);
    }

    protected void baseWooden(RecipeOutput recipeOutput, ItemStack result, Ingredient wooden, List<String> pattern) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new SawmillRecipe(
                result,
                ShapedRecipePattern.of(Map.of('A', wooden), pattern)
        ), null);
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
