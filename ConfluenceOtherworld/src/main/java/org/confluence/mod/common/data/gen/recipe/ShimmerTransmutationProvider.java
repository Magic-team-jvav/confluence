package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.CrateBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.init.item.AccessoryItems.*;
import static org.confluence.terra_curio.common.init.TCItems.*;

public class ShimmerTransmutationProvider extends AbstractRecipeProvider {
    public ShimmerTransmutationProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        // 黑名单
        item(recipeOutput, "deny_stairs", blacklist(ItemTags.STAIRS));
        item(recipeOutput, "deny_band_of_star_power", blacklist(AccessoryItems.BAND_OF_STARPOWER.get()));
        // 顶替
        item(recipeOutput, "replace_wool", addItem(ItemTags.WOOL, Items.WHITE_WOOL, 1));
        item(recipeOutput, "replace_wool_carpet", addItem(ItemTags.WOOL_CARPETS, Items.WHITE_CARPET, 1));
        item(recipeOutput, "replace_crafting_table", addItem(Items.CRAFTING_TABLE, Items.OAK_PLANKS));
        // 木头顶替
        item(recipeOutput, "replace_oak_planks", addItem(Items.OAK_PLANKS, Items.OAK_LOG, 9));
        item(recipeOutput, "replace_acacia_planks", addItem(Items.ACACIA_PLANKS, Items.ACACIA_LOG, 9));
        item(recipeOutput, "replace_birch_planks", addItem(Items.BIRCH_PLANKS, Items.BIRCH_LOG, 9));
        item(recipeOutput, "replace_dark_oak_planks", addItem(Items.DARK_OAK_PLANKS, Items.DARK_OAK_LOG, 9));
        item(recipeOutput, "replace_jungle_planks", addItem(Items.JUNGLE_PLANKS, Items.JUNGLE_LOG, 9));
        item(recipeOutput, "replace_mangrove_planks", addItem(Items.MANGROVE_PLANKS, Items.MANGROVE_LOG, 9));
        item(recipeOutput, "replace_spruce_planks", addItem(Items.SPRUCE_PLANKS, Items.SPRUCE_LOG, 9));
        item(recipeOutput, "replace_cherry_planks", addItem(Items.CHERRY_PLANKS, Items.CHERRY_LOG, 9));
        item(recipeOutput, "replace_crimson_planks", addItem(Items.CRIMSON_PLANKS, Items.CRIMSON_STEM, 9));
        item(recipeOutput, "replace_warped_planks", addItem(Items.WARPED_PLANKS, Items.WARPED_STEM, 9));

        item(recipeOutput, "replace_oak_planks_from_wood", addItem(Items.OAK_PLANKS, Items.OAK_WOOD, 9));
        item(recipeOutput, "replace_acacia_planks_from_wood", addItem(Items.ACACIA_PLANKS, Items.ACACIA_WOOD, 9));
        item(recipeOutput, "replace_birch_planks_from_wood", addItem(Items.BIRCH_PLANKS, Items.BIRCH_WOOD, 9));
        item(recipeOutput, "replace_dark_oak_planks_from_wood", addItem(Items.DARK_OAK_PLANKS, Items.DARK_OAK_WOOD, 9));
        item(recipeOutput, "replace_jungle_planks_from_wood", addItem(Items.JUNGLE_PLANKS, Items.JUNGLE_WOOD, 9));
        item(recipeOutput, "replace_mangrove_planks_from_wood", addItem(Items.MANGROVE_PLANKS, Items.MANGROVE_WOOD, 9));
        item(recipeOutput, "replace_spruce_planks_from_wood", addItem(Items.SPRUCE_PLANKS, Items.SPRUCE_WOOD, 9));
        item(recipeOutput, "replace_cherry_planks_from_wood", addItem(Items.CHERRY_PLANKS, Items.CHERRY_WOOD, 9));

        item(recipeOutput, "replace_oak_planks_from_stripped_log", addItem(Items.OAK_PLANKS, Items.STRIPPED_OAK_LOG, 9));
        item(recipeOutput, "replace_acacia_planks_from_stripped_log", addItem(Items.ACACIA_PLANKS, Items.STRIPPED_ACACIA_LOG, 9));
        item(recipeOutput, "replace_birch_planks_from_stripped_log", addItem(Items.BIRCH_PLANKS, Items.STRIPPED_BIRCH_LOG, 9));
        item(recipeOutput, "replace_dark_oak_planks_from_stripped_log", addItem(Items.DARK_OAK_PLANKS, Items.STRIPPED_DARK_OAK_LOG, 9));
        item(recipeOutput, "replace_jungle_planks_from_stripped_log", addItem(Items.JUNGLE_PLANKS, Items.STRIPPED_JUNGLE_LOG, 9));
        item(recipeOutput, "replace_mangrove_planks_from_stripped_log", addItem(Items.MANGROVE_PLANKS, Items.STRIPPED_MANGROVE_LOG, 9));
        item(recipeOutput, "replace_spruce_planks_from_stripped_log", addItem(Items.SPRUCE_PLANKS, Items.STRIPPED_SPRUCE_LOG, 9));
        item(recipeOutput, "replace_cherry_planks_from_stripped_log", addItem(Items.CHERRY_PLANKS, Items.STRIPPED_CHERRY_LOG, 9));
        item(recipeOutput, "replace_crimson_planks_from_stripped_log", addItem(Items.CRIMSON_PLANKS, Items.STRIPPED_CRIMSON_STEM, 9));
        item(recipeOutput, "replace_warped_planks_from_stripped_log", addItem(Items.WARPED_PLANKS, Items.STRIPPED_WARPED_STEM, 9));

        item(recipeOutput, "replace_oak_planks_from_stripped_wood", addItem(Items.OAK_PLANKS, Items.STRIPPED_OAK_WOOD, 9));
        item(recipeOutput, "replace_acacia_planks_from_stripped_wood", addItem(Items.ACACIA_PLANKS, Items.STRIPPED_ACACIA_WOOD, 9));
        item(recipeOutput, "replace_birch_planks_from_stripped_wood", addItem(Items.BIRCH_PLANKS, Items.STRIPPED_BIRCH_WOOD, 9));
        item(recipeOutput, "replace_dark_oak_planks_from_stripped_wood", addItem(Items.DARK_OAK_PLANKS, Items.STRIPPED_DARK_OAK_WOOD, 9));
        item(recipeOutput, "replace_jungle_planks_from_stripped_wood", addItem(Items.JUNGLE_PLANKS, Items.STRIPPED_JUNGLE_WOOD, 9));
        item(recipeOutput, "replace_mangrove_planks_from_stripped_wood", addItem(Items.MANGROVE_PLANKS, Items.STRIPPED_MANGROVE_WOOD, 9));
        item(recipeOutput, "replace_spruce_planks_from_stripped_wood", addItem(Items.SPRUCE_PLANKS, Items.STRIPPED_SPRUCE_WOOD, 9));
        item(recipeOutput, "replace_cherry_planks_from_stripped_wood", addItem(Items.CHERRY_PLANKS, Items.STRIPPED_CHERRY_WOOD, 9));

        item(recipeOutput, "replace_ebony_planks", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), NatureBlocks.EBONY_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_shadow_planks", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), NatureBlocks.SHADOW_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_yellow_willow_planks", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_living_planks", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), NatureBlocks.LIVING_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_ash_planks", addItem(NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), NatureBlocks.ASH_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_living_mahogany_planks", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_baobab_planks", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), NatureBlocks.BAOBAB_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_palm_planks", addItem(NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), NatureBlocks.PALM_LOG_BLOCKS.getLog(), 9));
        item(recipeOutput, "replace_pearl_planks", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), NatureBlocks.PEARL_LOG_BLOCKS.getLog(), 9));

        item(recipeOutput, "replace_ebony_planks_from_wood", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), NatureBlocks.EBONY_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_shadow_planks_from_wood", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), NatureBlocks.SHADOW_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_yellow_willow_planks_from_wood", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_living_planks_from_wood", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), NatureBlocks.LIVING_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_ash_planks_from_wood", addItem(NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), NatureBlocks.ASH_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_living_mahogany_planks_from_wood", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_baobab_planks_from_wood", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), NatureBlocks.BAOBAB_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_palm_planks_from_wood", addItem(NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), NatureBlocks.PALM_LOG_BLOCKS.getWood(), 9));
        item(recipeOutput, "replace_pearl_planks_from_wood", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), NatureBlocks.PEARL_LOG_BLOCKS.getWood(), 9));

        item(recipeOutput, "replace_ebony_planks_from_stripped_log", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_shadow_planks_from_stripped_log", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_yellow_willow_planks_from_stripped_log", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_living_planks_from_stripped_log", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), NatureBlocks.LIVING_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_ash_planks_from_stripped_log", addItem(NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), NatureBlocks.ASH_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_living_mahogany_planks_from_stripped_log", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_baobab_planks_from_stripped_log", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_palm_planks_from_stripped_log", addItem(NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), NatureBlocks.PALM_LOG_BLOCKS.getStrippedLog(), 9));
        item(recipeOutput, "replace_pearl_planks_from_stripped_log", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedLog(), 9));

        item(recipeOutput, "replace_ebony_planks_from_stripped_wood", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), NatureBlocks.EBONY_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_shadow_planks_from_stripped_wood", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), NatureBlocks.SHADOW_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_yellow_willow_planks_from_stripped_wood", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_living_planks_from_stripped_wood", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), NatureBlocks.LIVING_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_ash_planks_from_stripped_wood", addItem(NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), NatureBlocks.ASH_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_living_mahogany_planks_from_stripped_wood", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_baobab_planks_from_stripped_wood", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), NatureBlocks.BAOBAB_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_palm_planks_from_stripped_wood", addItem(NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), NatureBlocks.PALM_LOG_BLOCKS.getStrippedWood(), 9));
        item(recipeOutput, "replace_pearl_planks_from_stripped_wood", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), NatureBlocks.PEARL_LOG_BLOCKS.getStrippedWood(), 9));

        item(recipeOutput, "oak_slab_to_planks", addItem(Items.OAK_SLAB, Items.OAK_PLANKS, 4));
        item(recipeOutput, "acacia_slab_to_planks", addItem(Items.ACACIA_SLAB, Items.ACACIA_PLANKS, 4));
        item(recipeOutput, "birch_slab_to_planks", addItem(Items.BIRCH_SLAB, Items.BIRCH_PLANKS, 4));
        item(recipeOutput, "dark_oak_slab_to_planks", addItem(Items.DARK_OAK_SLAB, Items.DARK_OAK_PLANKS, 4));
        item(recipeOutput, "jungle_slab_to_planks", addItem(Items.JUNGLE_SLAB, Items.JUNGLE_PLANKS, 4));
        item(recipeOutput, "mangrove_slab_to_planks", addItem(Items.MANGROVE_SLAB, Items.MANGROVE_PLANKS, 4));
        item(recipeOutput, "spruce_slab_to_planks", addItem(Items.SPRUCE_SLAB, Items.SPRUCE_PLANKS, 4));
        item(recipeOutput, "cherry_slab_to_planks", addItem(Items.CHERRY_SLAB, Items.CHERRY_PLANKS, 4));
        item(recipeOutput, "crimson_slab_to_planks", addItem(Items.CRIMSON_SLAB, Items.CRIMSON_PLANKS, 4));
        item(recipeOutput, "warped_slab_to_planks", addItem(Items.WARPED_SLAB, Items.WARPED_PLANKS, 4));

        item(recipeOutput, "ebony_slab_to_planks", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getSlab(), NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "shadow_slab_to_planks", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getSlab(), NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "yellow_willow_slab_to_planks", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getSlab(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "living_slab_to_planks", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getSlab(), NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "ash_slab_to_planks", addItem(NatureBlocks.ASH_LOG_BLOCKS.getSlab(), NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "living_mahogany_slab_to_planks", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getSlab(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "baobab_slab_to_planks", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getSlab(), NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "palm_slab_to_planks", addItem(NatureBlocks.PALM_LOG_BLOCKS.getSlab(), NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), 4));
        item(recipeOutput, "pearl_slab_to_planks", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getSlab(), NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), 4));

        item(recipeOutput, "oak_stairs_to_planks", addItem(Items.OAK_STAIRS, Items.OAK_PLANKS, 2));
        item(recipeOutput, "acacia_stairs_to_planks", addItem(Items.ACACIA_STAIRS, Items.ACACIA_PLANKS, 2));
        item(recipeOutput, "birch_stairs_to_planks", addItem(Items.BIRCH_STAIRS, Items.BIRCH_PLANKS, 2));
        item(recipeOutput, "dark_oak_stairs_to_planks", addItem(Items.DARK_OAK_STAIRS, Items.DARK_OAK_PLANKS, 2));
        item(recipeOutput, "jungle_stairs_to_planks", addItem(Items.JUNGLE_STAIRS, Items.JUNGLE_PLANKS, 2));
        item(recipeOutput, "mangrove_stairs_to_planks", addItem(Items.MANGROVE_STAIRS, Items.MANGROVE_PLANKS, 2));
        item(recipeOutput, "spruce_stairs_to_planks", addItem(Items.SPRUCE_STAIRS, Items.SPRUCE_PLANKS, 2));
        item(recipeOutput, "cherry_stairs_to_planks", addItem(Items.CHERRY_STAIRS, Items.CHERRY_PLANKS, 2));
        item(recipeOutput, "crimson_stairs_to_planks", addItem(Items.CRIMSON_STAIRS, Items.CRIMSON_PLANKS, 2));
        item(recipeOutput, "warped_stairs_to_planks", addItem(Items.WARPED_STAIRS, Items.WARPED_PLANKS, 2));

        item(recipeOutput, "ebony_stairs_to_planks", addItem(NatureBlocks.EBONY_LOG_BLOCKS.getStairs(), NatureBlocks.EBONY_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "shadow_stairs_to_planks", addItem(NatureBlocks.SHADOW_LOG_BLOCKS.getStairs(), NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "yellow_willow_stairs_to_planks", addItem(NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getStairs(), NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "living_stairs_to_planks", addItem(NatureBlocks.LIVING_LOG_BLOCKS.getStairs(), NatureBlocks.LIVING_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "ash_stairs_to_planks", addItem(NatureBlocks.ASH_LOG_BLOCKS.getStairs(), NatureBlocks.ASH_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "living_mahogany_stairs_to_planks", addItem(NatureBlocks.LIVING_MAHOGANY_BLOCKS.getStairs(), NatureBlocks.LIVING_MAHOGANY_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "baobab_stairs_to_planks", addItem(NatureBlocks.BAOBAB_LOG_BLOCKS.getStairs(), NatureBlocks.BAOBAB_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "palm_stairs_to_planks", addItem(NatureBlocks.PALM_LOG_BLOCKS.getStairs(), NatureBlocks.PALM_LOG_BLOCKS.getPlanks(), 2));
        item(recipeOutput, "pearl_stairs_to_planks", addItem(NatureBlocks.PEARL_LOG_BLOCKS.getStairs(), NatureBlocks.PEARL_LOG_BLOCKS.getPlanks(), 2));
        // 下界合金装备嬗变
        item(recipeOutput, "netherite_ingot_from_chestplate", addItem(Items.NETHERITE_CHESTPLATE, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_helmet", addItem(Items.NETHERITE_HELMET, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_leggings", addItem(Items.NETHERITE_LEGGINGS, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_boots", addItem(Items.NETHERITE_BOOTS, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_sword", addItem(Items.NETHERITE_SWORD, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_shovel", addItem(Items.NETHERITE_SHOVEL, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_hoe", addItem(Items.NETHERITE_HOE, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_axe", addItem(Items.NETHERITE_AXE, Items.NETHERITE_INGOT));
        item(recipeOutput, "netherite_ingot_from_pickaxe", addItem(Items.NETHERITE_PICKAXE, Items.NETHERITE_INGOT));
        // 饰品转化
        item(recipeOutput, BALLOON_PUFFERFISH, SHINY_RED_BALLOON);
        item(recipeOutput, MAGMA_STONE, LAVA_CHARM);
        item(recipeOutput, LAVA_CHARM, MAGMA_STONE);
        item(recipeOutput, SEXTANT, WEATHER_RADIO);
        item(recipeOutput, WEATHER_RADIO, FISHERMANS_POCKET_GUIDE);
        item(recipeOutput, FISHERMANS_POCKET_GUIDE, SEXTANT);
        item(recipeOutput, BEZOAR, ADHESIVE_BANDAGE);
        item(recipeOutput, ADHESIVE_BANDAGE, BEZOAR);
        item(recipeOutput, ARMOR_POLISH, VITAMINS);
        item(recipeOutput, VITAMINS, ARMOR_POLISH);
        item(recipeOutput, POCKET_MIRROR, BLINDFOLD);
        item(recipeOutput, BLINDFOLD, POCKET_MIRROR);
        item(recipeOutput, FAST_CLOCK, TRIFOLD_MAP);
        item(recipeOutput, TRIFOLD_MAP, FAST_CLOCK);
        item(recipeOutput, NAZAR, MEGAPHONE);
        item(recipeOutput, MEGAPHONE, NAZAR);
        item(recipeOutput, HIGH_TEST_FISHING_LINE, ANGLER_EARRING);
        item(recipeOutput, ANGLER_EARRING, TACKLE_BOX);
        item(recipeOutput, TACKLE_BOX, HIGH_TEST_FISHING_LINE);
        item(recipeOutput, STAR_CLOAK, CHROMATIC_CLOAK);
        item(recipeOutput, SUMMONER_EMBLEM, WARRIOR_EMBLEM);
        item(recipeOutput, WARRIOR_EMBLEM, RANGER_EMBLEM);
        item(recipeOutput, RANGER_EMBLEM, SORCERER_EMBLEM);
        item(recipeOutput, SORCERER_EMBLEM, SUMMONER_EMBLEM);
        // todo 火把转化
        // 临时转化
        item(recipeOutput, Blocks.SCULK, FunctionalBlocks.ECHO_BLOCK);
        item(recipeOutput, PaintItems.ECHO_COATING, PaintItems.ILLUMINANT_COATING);
        item(recipeOutput, PaintItems.ILLUMINANT_COATING, PaintItems.NEGATIVE_PAINT);
        item(recipeOutput, PaintItems.NEGATIVE_PAINT, PaintItems.SHADOW_PAINT);
        item(recipeOutput, PaintItems.SHADOW_PAINT, PaintItems.ECHO_COATING);
        item(recipeOutput, AccessoryItems.MECHANICAL_LENS, AccessoryItems.SPECTRE_GOGGLES);
        // 腐化与猩红转化
        item(recipeOutput, "raw_demonite_from_raw_crimtane", addItem(MaterialItems.RAW_CRIMTANE, MaterialItems.RAW_DEMONITE, 3));
        item(recipeOutput, "raw_crimtane_from_raw_demonite", addItem(MaterialItems.RAW_DEMONITE, MaterialItems.RAW_CRIMTANE, 3));
        item(recipeOutput, "vertebra_from_rotten_bone", addItem(MaterialItems.ROTTEN_BONE, MaterialItems.VERTEBRA, 3));
        item(recipeOutput, "rotten_bone_from_vertebra", addItem(MaterialItems.VERTEBRA, MaterialItems.ROTTEN_BONE, 3));
        item(recipeOutput, ModItems.CORRUPT_SEED, ModItems.CRIMSON_SEED);
        item(recipeOutput, ModItems.CRIMSON_SEED, ModItems.CORRUPT_SEED);
        // 微光箭转化
        item(recipeOutput, "shimmer_arrow", addItem(ItemTags.ARROWS, ArrowItems.SHIMMER_ARROW.get(), 1));
        // 匣子转化
        item(recipeOutput, CrateBlocks.PEARLWOOD_CRATE, CrateBlocks.WOODEN_CRATE);
        item(recipeOutput, CrateBlocks.MYTHRIL_CRATE, CrateBlocks.IRON_CRATE);
        item(recipeOutput, CrateBlocks.TITANIUM_CRATE, CrateBlocks.GOLDEN_CRATE);
        item(recipeOutput, CrateBlocks.BRAMBLE_CRATE, CrateBlocks.JUNGLE_CRATE);
        item(recipeOutput, CrateBlocks.WILD_CRATE, CrateBlocks.SAVANNA_CRATE);
        item(recipeOutput, CrateBlocks.AZURE_CRATE, CrateBlocks.SKY_CRATE);
        item(recipeOutput, CrateBlocks.DEFILED_CRATE, CrateBlocks.CORRUPT_CRATE);
        item(recipeOutput, CrateBlocks.HEMATIC_CRATE, CrateBlocks.CRIMSON_CRATE);
        item(recipeOutput, CrateBlocks.DIVINE_CRATE, CrateBlocks.HALLOWED_CRATE);
        item(recipeOutput, CrateBlocks.STOCKADE_CRATE, CrateBlocks.DUNGEON_CRATE);
        item(recipeOutput, CrateBlocks.BOREAL_CRATE, CrateBlocks.FROZEN_CRATE);
        item(recipeOutput, CrateBlocks.MIRAGE_CRATE, CrateBlocks.OASIS_CRATE);
        item(recipeOutput, CrateBlocks.HELLSTONE_CRATE, CrateBlocks.OBSIDIAN_CRATE);
        item(recipeOutput, CrateBlocks.SEASIDE_CRATE, CrateBlocks.OCEAN_CRATE);
        // 宝石转化
        item(recipeOutput, MaterialItems.TOPAZ, MaterialItems.AMETHYST);
        item(recipeOutput, MaterialItems.SAPPHIRE, MaterialItems.TOPAZ);
        item(recipeOutput, MaterialItems.JADE, MaterialItems.SAPPHIRE);
        item(recipeOutput, MaterialItems.RUBY, MaterialItems.JADE);
        item(recipeOutput, Items.DIAMOND, MaterialItems.RUBY);
        item(recipeOutput, "cobblestone_from_amethyst", addItem(MaterialItems.AMETHYST, Items.COBBLESTONE));
        // 锭到矿的转化
        item(recipeOutput, MaterialItems.TITANIUM_INGOT, MaterialItems.RAW_TITANIUM);
        item(recipeOutput, "raw_adamantite_from_ingot", addItem(MaterialItems.ADAMANTITE_INGOT, MaterialItems.RAW_ADAMANTITE));
        item(recipeOutput, "raw_orichalcum_from_ingot", addItem(MaterialItems.ORICHALCUM_INGOT, MaterialItems.RAW_ORICHALCUM));
        item(recipeOutput, "raw_mythril_from_ingot", addItem(MaterialItems.MYTHRIL_INGOT, MaterialItems.RAW_MYTHRIL));
        item(recipeOutput, "raw_palladium_from_ingot", addItem(MaterialItems.PALLADIUM_INGOT, MaterialItems.RAW_PALLADIUM));
        item(recipeOutput, "raw_cobalt_from_ingot", addItem(MaterialItems.COBALT_INGOT, MaterialItems.RAW_COBALT));
        item(recipeOutput, MaterialItems.HELLSTONE_INGOT, MaterialItems.RAW_HELLSTONE);
        item(recipeOutput, MaterialItems.CRIMTANE_INGOT, MaterialItems.RAW_CRIMTANE);
        item(recipeOutput, MaterialItems.DEMONITE_INGOT, MaterialItems.RAW_DEMONITE);
        item(recipeOutput, MaterialItems.METEORITE_INGOT, MaterialItems.RAW_METEORITE);
        item(recipeOutput, "raw_platinum_from_ingot", addItem(ModTags.Items.INGOTS_PLATINUM, MaterialItems.RAW_PLATINUM.get(), 1));
        item(recipeOutput, "raw_gold_from_ingot", addItem(Items.GOLD_INGOT, Items.RAW_GOLD));
        item(recipeOutput, "raw_tungsten_from_ingot", addItem(ModTags.Items.INGOTS_TUNGSTEN, MaterialItems.RAW_TUNGSTEN.get(), 1));
        item(recipeOutput, "raw_silver_from_ingot", addItem(ModTags.Items.INGOTS_SILVER, MaterialItems.RAW_SILVER.get(), 1));
        item(recipeOutput, "raw_iron_from_ingot", addItem(Items.IRON_INGOT, Items.RAW_IRON));
        item(recipeOutput, "raw_lead_from_ingot", addItem(ModTags.Items.INGOTS_LEAD, MaterialItems.RAW_LEAD.get(), 1));
        item(recipeOutput, "raw_tin_from_ingot", addItem(ModTags.Items.INGOTS_TIN, MaterialItems.RAW_TIN.get(), 1));
        item(recipeOutput, "raw_copper_from_ingot", addItem(Items.COPPER_INGOT, Items.RAW_COPPER));
        item(recipeOutput, "cobblestone_from_raw_copper", addItem(Items.RAW_COPPER, Items.COBBLESTONE));
        item(recipeOutput, Items.COBBLESTONE, Items.DIRT);
        // 矿的下级转化（陨石，魔矿，猩红矿不参与这一过程）
        item(recipeOutput, MaterialItems.RAW_TITANIUM, MaterialItems.RAW_ADAMANTITE);
        item(recipeOutput, MaterialItems.RAW_ADAMANTITE, MaterialItems.RAW_ORICHALCUM);
        item(recipeOutput, MaterialItems.RAW_ORICHALCUM, MaterialItems.RAW_MYTHRIL);
        item(recipeOutput, MaterialItems.RAW_PALLADIUM, MaterialItems.RAW_COBALT);
        item(recipeOutput, MaterialItems.RAW_COBALT, MaterialItems.RAW_PLATINUM);
        item(recipeOutput, MaterialItems.RAW_PLATINUM, Items.RAW_GOLD);
        item(recipeOutput, Items.RAW_GOLD, MaterialItems.RAW_TUNGSTEN);
        item(recipeOutput, MaterialItems.RAW_TUNGSTEN, MaterialItems.RAW_SILVER);
        item(recipeOutput, MaterialItems.RAW_SILVER, MaterialItems.RAW_LEAD);
        item(recipeOutput, MaterialItems.RAW_LEAD, Items.RAW_IRON);
        item(recipeOutput, Items.RAW_IRON, MaterialItems.RAW_TIN);
        item(recipeOutput, MaterialItems.RAW_TIN, Items.RAW_COPPER);

        item(recipeOutput, Items.WATER_BUCKET, Items.LAVA_BUCKET);
        item(recipeOutput, Items.LAVA_BUCKET, ToolItems.HONEY_BUCKET);
        item(recipeOutput, ToolItems.HONEY_BUCKET, Items.WATER_BUCKET);
        item(recipeOutput, SwordItems.ZOMBIE_ARM, ModItems.WHOOPIE_CUSHION);

        item(recipeOutput, ConsumableItems.LIFE_CRYSTAL, ConsumableItems.VITAL_CRYSTAL);
        item(recipeOutput, ConsumableItems.MANA_CRYSTAL, ConsumableItems.ARCANE_CRYSTAL);
        item(recipeOutput, ConsumableItems.LIFE_FRUIT, ConsumableItems.AEGIS_APPLE);
        item(recipeOutput, "ambrosia", addItem(Tags.Items.FOODS_FRUIT, ConsumableItems.AMBROSIA.get(), 1));
        item(recipeOutput, BaitItems.GOLD_WORM, ConsumableItems.GUMMY_WORM);
        item(recipeOutput, MaterialItems.PINK_PEARL, ConsumableItems.GALAXY_PEARL);

        item(recipeOutput, MaterialItems.GEL, Items.SLIME_BALL);
        item(recipeOutput, ConsumableItems.HERB_BAG, ConsumableItems.CAN_OF_WORMS);
        item(recipeOutput, ConsumableItems.CAN_OF_WORMS, ConsumableItems.HERB_BAG);
        item(recipeOutput, "purification_powder_from_vile_powder", addItem(ConsumableItems.VILE_POWDER, ConsumableItems.PURIFICATION_POWDER));
        item(recipeOutput, "purification_powder_from_vicious_powder", addItem(ConsumableItems.VICIOUS_POWDER, ConsumableItems.PURIFICATION_POWDER));

        item(recipeOutput, "bottomless_shimmer_bucket", addItem(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), GamePhase.MOON_LORD));
        item(recipeOutput, "bottomless_water_bucket", addItem(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), ToolItems.BOTTOMLESS_WATER_BUCKET.get(), GamePhase.MOON_LORD));
    }


    protected void item(RecipeOutput recipeOutput, String path, ItemTransmutationRecipe recipe) {
        recipeOutput.accept(Confluence.asResource("item_transmutation/" + path), recipe, null);
    }

    protected void item(RecipeOutput recipeOutput, ItemLike source, ItemLike target) {
        ResourceLocation id = Confluence.asResource("item_transmutation/" + BuiltInRegistries.ITEM.getKey(target.asItem()).getPath());
        recipeOutput.accept(id, addItem(source.asItem(), target.asItem()), null);
    }

    public static ItemTransmutationRecipe addItem(TagKey<Item> source, List<ItemStack> target, int shrink) {
        return new ItemTransmutationRecipe(Ingredient.of(source), target, shrink, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(TagKey<Item> source, Item target, int shrink) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(Ingredient source, List<ItemStack> target, int shrink) {
        return new ItemTransmutationRecipe(source, target, shrink, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(Item source, Item target, int shrink) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(Item source, Item target) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), 1, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(ItemLike source, ItemLike target, int shrink) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.asItem().getDefaultInstance()), shrink, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(ItemLike source, ItemLike target) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.asItem().getDefaultInstance()), 1, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe addItem(TagKey<Item> source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        return new ItemTransmutationRecipe(Ingredient.of(source), target, shrink, gamePhase);
    }

    public static ItemTransmutationRecipe addItem(Ingredient source, List<ItemStack> target, int shrink, GamePhase gamePhase) {
        return new ItemTransmutationRecipe(source, target, shrink, gamePhase);
    }

    public static ItemTransmutationRecipe addItem(Item source, Item target, int shrink, GamePhase gamePhase) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, gamePhase);
    }

    public static ItemTransmutationRecipe addItem(Item source, Item target, GamePhase gamePhase) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), 1, gamePhase);
    }

    public static ItemTransmutationRecipe addItem(TagKey<Item> source, Item target, int shrink, GamePhase gamePhase) {
        return new ItemTransmutationRecipe(Ingredient.of(source), Collections.singletonList(target.getDefaultInstance()), shrink, gamePhase);
    }

    public static ItemTransmutationRecipe blacklist(Ingredient source) {
        return new ItemTransmutationRecipe(source, List.of(), 1, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe blacklist(TagKey<Item> tagKey) {
        return new ItemTransmutationRecipe(Ingredient.of(tagKey), List.of(), 1, GamePhase.BEFORE_SKELETRON);
    }

    public static ItemTransmutationRecipe blacklist(Item item) {
        return new ItemTransmutationRecipe(Ingredient.of(item), List.of(), 1, GamePhase.BEFORE_SKELETRON);
    }
}
