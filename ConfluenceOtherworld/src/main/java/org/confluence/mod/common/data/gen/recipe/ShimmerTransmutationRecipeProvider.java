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
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.data.saved.GamePhase;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.ItemTransmutationRecipe;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terraentity.init.item.TESummonItems;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.init.item.AccessoryItems.*;
import static org.confluence.terra_curio.common.init.TCItems.*;

public class ShimmerTransmutationRecipeProvider extends AbstractRecipeProvider {
    public ShimmerTransmutationRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
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

        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            String id = blockSet.id;
            if (blockSet.LOG.isBound()) {
                item(recipeOutput, "replace_" + id + "_planks", addItem(blockSet.PLANKS, blockSet.LOG, 9));
                item(recipeOutput, id + "_log_from_oak_log", addItem(Blocks.OAK_LOG, blockSet.LOG));
            }
            if (blockSet.WOOD.isBound()) item(recipeOutput, "replace_" + id + "_planks_from_wood", addItem(blockSet.PLANKS, blockSet.WOOD, 9));
            if (blockSet.STRIPPED_LOG.isBound()) item(recipeOutput, "replace_" + id + "_planks_from_stripped_log", addItem(blockSet.PLANKS, blockSet.STRIPPED_LOG, 9));
            if (blockSet.STRIPPED_WOOD.isBound()) item(recipeOutput, "replace_" + id + "_planks_from_stripped_wood", addItem(blockSet.PLANKS, blockSet.STRIPPED_WOOD, 9));
            if (blockSet.SLAB.isBound()) item(recipeOutput, id + "_slab_to_planks", addItem(blockSet.SLAB, blockSet.PLANKS, 4));
            if (blockSet.STAIRS.isBound()) item(recipeOutput, id + "_stairs_to_planks", addItem(blockSet.STAIRS, blockSet.PLANKS, 2));
        }

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
        // 腐化与猩红转化
        item(recipeOutput, "raw_demonite_from_raw_crimtane", addItem(MaterialItems.RAW_CRIMTANE, MaterialItems.RAW_DEMONITE, 3));
        item(recipeOutput, "raw_crimtane_from_raw_demonite", addItem(MaterialItems.RAW_DEMONITE, MaterialItems.RAW_CRIMTANE, 3));
        item(recipeOutput, "vertebra_from_rotten_bone", addItem(MaterialItems.ROTTEN_BONE, MaterialItems.VERTEBRA, 3));
        item(recipeOutput, "rotten_bone_from_vertebra", addItem(MaterialItems.VERTEBRA, MaterialItems.ROTTEN_BONE, 3));
        item(recipeOutput, ModItems.CORRUPT_SEED, ModItems.CRIMSON_SEED);
        item(recipeOutput, ModItems.CRIMSON_SEED, ModItems.CORRUPT_SEED);
        // 盔甲转化
        item(recipeOutput, ArmorItems.NINJA_HELMET, ArmorItems.NINJA_CHESTPLATE);
        item(recipeOutput, ArmorItems.NINJA_CHESTPLATE, ArmorItems.NINJA_LEGGINGS);
        item(recipeOutput, ArmorItems.NINJA_LEGGINGS, ArmorItems.NINJA_BOOTS);
        item(recipeOutput, ArmorItems.NINJA_BOOTS, ArmorItems.NINJA_HELMET);

        item(recipeOutput, ArmorItems.MINING_CHESTPLATE, ArmorItems.MINING_LEGGINGS);
        item(recipeOutput, ArmorItems.MINING_LEGGINGS, ArmorItems.MINING_BOOTS);
        item(recipeOutput, ArmorItems.MINING_BOOTS, ArmorItems.MINING_CHESTPLATE);
        // 钥匙变箱子
        item(recipeOutput, ToolItems.GOLDEN_DUNGEON_KEY, ChestBlocks.DUNGEON_CHEST);
        item(recipeOutput, ToolItems.GOLDEN_KEY, ChestBlocks.GOLDEN_CHEST);
        item(recipeOutput, ToolItems.SHADOW_KEY, ChestBlocks.SHADOW_CHEST);
        item(recipeOutput, ToolItems.JUNGLE_KEY, ChestBlocks.JUNGLE_CHEST);
        item(recipeOutput, ToolItems.CORRUPTION_KEY, ChestBlocks.CORRUPTION_CHEST);
        item(recipeOutput, ToolItems.CRIMSON_KEY, ChestBlocks.CRIMSON_CHEST);
        item(recipeOutput, ToolItems.HALLOWED_KEY, ChestBlocks.HALLOWED_CHEST);
        item(recipeOutput, ToolItems.FROZEN_KEY, ChestBlocks.FROZEN_CHEST);
        item(recipeOutput, ToolItems.DESERT_KEY, ChestBlocks.DESERT_CHEST);
        item(recipeOutput, ToolItems.OCEAN_KEY, ChestBlocks.OCEAN_CHEST);
        item(recipeOutput, ToolItems.UNIVERSE_KEY, ChestBlocks.UNIVERSE_CHEST);
        item(recipeOutput, ToolItems.MECHANIC_SAFE_KEY, ChestBlocks.MECHANIC_SAFE_CHEST);
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
        item(recipeOutput, "clay_from_amethyst", addItem(MaterialItems.AMETHYST, Items.CLAY));
        // 锭到矿的转化
        item(recipeOutput, "raw_chlorophyte_from_ingot", addItem(Ingredient.of(MaterialItems.CHLOROPHYTE_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_CHLOROPHYTE.get().asItem(), 5)),1));
        item(recipeOutput, "raw_titanium_from_ingot", addItem(Ingredient.of(MaterialItems.TITANIUM_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_TITANIUM.get().asItem(), 4)),1));
        item(recipeOutput, "raw_adamantite_from_ingot", addItem(Ingredient.of(MaterialItems.ADAMANTITE_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_ADAMANTITE.get().asItem(), 4)),1));
        item(recipeOutput, "raw_orichalcum_from_ingot", addItem(Ingredient.of(MaterialItems.ORICHALCUM_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_ORICHALCUM.get().asItem(), 4)),1));
        item(recipeOutput, "raw_mythril_from_ingot", addItem(Ingredient.of(MaterialItems.MYTHRIL_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_MYTHRIL.get().asItem(), 4)),1));
        item(recipeOutput, "raw_palladium_from_ingot", addItem(Ingredient.of(MaterialItems.PALLADIUM_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_PALLADIUM.get().asItem(), 3)),1));
        item(recipeOutput, "raw_cobalt_from_ingot", addItem(Ingredient.of(MaterialItems.COBALT_INGOT), Collections.singletonList(new ItemStack(MaterialItems.RAW_COBALT.get().asItem(), 3)),1));
        item(recipeOutput, "raw_hellstone_from_ingot", addItem(Ingredient.of(MaterialItems.HELLSTONE_INGOT), Arrays.asList(new ItemStack(MaterialItems.RAW_HELLSTONE.get().asItem(), 3), new ItemStack(Items.OBSIDIAN.asItem(), 1)), 1));
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
        item(recipeOutput, "clay_from_raw_copper", addItem(Items.RAW_COPPER, Items.CLAY));
        item(recipeOutput, Items.CLAY, Items.COBBLESTONE);
        item(recipeOutput, Items.COBBLESTONE, Items.DIRT);
        // 矿的下级转化（陨石，魔矿，猩红矿不参与这一过程）
        item(recipeOutput, MaterialItems.RAW_LUMINITE, MaterialItems.RAW_CHLOROPHYTE);
        item(recipeOutput, MaterialItems.RAW_CHLOROPHYTE, MaterialItems.RAW_TITANIUM);
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
        // 其余转化
        item(recipeOutput, Items.WATER_BUCKET, Items.LAVA_BUCKET);
        item(recipeOutput, Items.LAVA_BUCKET, ToolItems.HONEY_BUCKET);
        item(recipeOutput, ToolItems.HONEY_BUCKET, Items.WATER_BUCKET);
        item(recipeOutput, SwordItems.ZOMBIE_ARM, ModItems.WHOOPIE_CUSHION);
        item(recipeOutput, TCItems.PUTRID_SCENT, TCItems.FLESH_KNUCKLES);
        item(recipeOutput, TCItems.FLESH_KNUCKLES, TCItems.PUTRID_SCENT);

        item(recipeOutput, ConsumableItems.LIFE_CRYSTAL, ConsumableItems.VITAL_CRYSTAL);
        item(recipeOutput, ConsumableItems.MANA_CRYSTAL, ConsumableItems.ARCANE_CRYSTAL);
        item(recipeOutput, ConsumableItems.LIFE_FRUIT, ConsumableItems.AEGIS_APPLE);
        item(recipeOutput, "ambrosia", addItem(Tags.Items.FOODS_FRUIT, ConsumableItems.AMBROSIA.get(), 1));
        item(recipeOutput, BaitItems.GOLD_WORM, ConsumableItems.GUMMY_WORM);
        item(recipeOutput, MaterialItems.PINK_PEARL, ConsumableItems.GALAXY_PEARL);

        item(recipeOutput, MaterialItems.GEL, Items.SLIME_BALL);
        item(recipeOutput, ConsumableItems.HERB_BAG, ConsumableItems.CAN_OF_WORMS);
        item(recipeOutput, ConsumableItems.CAN_OF_WORMS, ConsumableItems.HERB_BAG);
        item(recipeOutput, FunctionalBlocks.ALCHEMY_TABLE, FunctionalBlocks.BEWITCHING_TABLE);
        item(recipeOutput, FunctionalBlocks.BEWITCHING_TABLE, FunctionalBlocks.ALCHEMY_TABLE);
        item(recipeOutput, TESummonItems.FINCH_STAFF, ModItems.LIVING_WOOD_WAND);
        item(recipeOutput, MaterialItems.SPELL_TOME, ConsumableItems.ADVANCED_COMBAT_TECHNIQUES_VOLUME_TWO);

        item(recipeOutput, NatureBlocks.GRANITE, NatureBlocks.MARBLE);
        item(recipeOutput, "sand_from_sandstone", addItem(Blocks.SANDSTONE, Blocks.SAND));
        item(recipeOutput, "sand_from_hardened_sand", addItem(NatureBlocks.HARDENED_SAND_BLOCK, Blocks.SAND));
        item(recipeOutput, "red_sand_from_red_sandstone", addItem(Blocks.RED_SANDSTONE, Blocks.RED_SAND));
        item(recipeOutput, "red_sand_from_hardened_red_sand_block", addItem(NatureBlocks.HARDENED_RED_SAND_BLOCK, Blocks.RED_SAND));
        item(recipeOutput, "ebonsand_from_ebonsandstone", addItem(NatureBlocks.EBONSANDSTONE, NatureBlocks.EBONSAND));
        item(recipeOutput, "ebonsand_from_hardened_ebonsand", addItem(NatureBlocks.HARDENED_EBONSAND_BLOCK, NatureBlocks.EBONSAND));
        item(recipeOutput, "crimsand_from_crimsandstone", addItem(NatureBlocks.CRIMSANDSTONE, NatureBlocks.CRIMSAND));
        item(recipeOutput, "crimsand_from_hardened_crimsand", addItem(NatureBlocks.HARDENED_CRIMSAND_BLOCK, NatureBlocks.CRIMSAND));
        item(recipeOutput, "pearlsand_from_pearlsandstone", addItem(NatureBlocks.PEARLSANDSTONE, NatureBlocks.PEARLSAND));
        item(recipeOutput, "pearlsand_from_hardened_pearlsand", addItem(NatureBlocks.HARDENED_PEARLSAND_BLOCK, NatureBlocks.PEARLSAND));
        item(recipeOutput, "ice_from_snow_block", addItem(Blocks.SNOW_BLOCK, Blocks.ICE));
        item(recipeOutput, "blue_ice_from_snow_block", addItem(Blocks.SNOW_BLOCK, Blocks.BLUE_ICE));
        item(recipeOutput, "packed_ice_from_snow_block", addItem(Blocks.SNOW_BLOCK, Blocks.PACKED_ICE));
        item(recipeOutput, "jungle_hive_from_honey_block", addItem(Blocks.HONEY_BLOCK, NatureBlocks.JUNGLE_HIVE_BLOCK));
        item(recipeOutput, "jungle_hive_from_crispy_honey_block", addItem(DecorativeBlocks.CRISPY_HONEY_BLOCK,NatureBlocks.JUNGLE_HIVE_BLOCK));
        item(recipeOutput, Blocks.PUMPKIN, Blocks.CACTUS);
        item(recipeOutput, Blocks.CACTUS, Blocks.PUMPKIN);
        item(recipeOutput, Blocks.SNOW_BLOCK, NatureBlocks.AETHERIUM_BLOCK);
        item(recipeOutput, DecorativeBlocks.SUN_PLATE.FULL, DecorativeBlocks.MOON_PLATE.FULL);
        item(recipeOutput, DecorativeBlocks.MOON_PLATE.FULL, DecorativeBlocks.SUN_PLATE.FULL);
        item(recipeOutput, "acacia_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.ACACIA_LOG));
        item(recipeOutput, "birch_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.BIRCH_LOG));
        item(recipeOutput, "cherry_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.CHERRY_LOG));
        item(recipeOutput, "jungle_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.JUNGLE_LOG));
        item(recipeOutput, "dark_oak_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.DARK_OAK_LOG));
        item(recipeOutput, "mangrove_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.MANGROVE_LOG));
        item(recipeOutput, "spruce_log_from_oak_log", addItem(Blocks.OAK_LOG, Blocks.SPRUCE_LOG));
        item(recipeOutput, "purification_powder_from_vile_powder", addItem(ConsumableItems.VILE_POWDER, ConsumableItems.PURIFICATION_POWDER));
        item(recipeOutput, "purification_powder_from_vicious_powder", addItem(ConsumableItems.VICIOUS_POWDER, ConsumableItems.PURIFICATION_POWDER));

        item(recipeOutput, "bottomless_shimmer_bucket", addItem(ToolItems.BOTTOMLESS_WATER_BUCKET.get(), ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), GamePhase.MOON_LORD));
        item(recipeOutput, "bottomless_water_bucket", addItem(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get(), ToolItems.BOTTOMLESS_WATER_BUCKET.get(), GamePhase.MOON_LORD));

        item(recipeOutput, ModItems.JUNGLE_GRASS_SEED, ModItems.MUSHROOM_GRASS_SEED);
        item(recipeOutput, ModItems.MUSHROOM_GRASS_SEED, ModItems.JUNGLE_GRASS_SEED);
        item(recipeOutput, Blocks.CHERRY_SAPLING, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING);
        item(recipeOutput, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS.SAPLING,Blocks.CHERRY_SAPLING);
        item(recipeOutput, "helium_moss_from_lava_moss", addItem(NatureBlocks.LAVA_MOSS, NatureBlocks.HELIUM_MOSS));
        item(recipeOutput, "helium_moss_from_krypton_moss", addItem(NatureBlocks.KRYPTON_MOSS, NatureBlocks.HELIUM_MOSS));
        item(recipeOutput, "helium_moss_from_xenon_moss", addItem( NatureBlocks.XENON_MOSS, NatureBlocks.HELIUM_MOSS));
        item(recipeOutput, "helium_moss_from_argon_moss", addItem(NatureBlocks.ARGON_MOSS, NatureBlocks.HELIUM_MOSS));
        item(recipeOutput, "helium_moss_from_neon_moss", addItem( NatureBlocks.NEON_MOSS, NatureBlocks.HELIUM_MOSS));
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
