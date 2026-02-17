package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.commons.lang3.stream.Streams;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.special.BoomBunnyRecipe;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
import org.confluence.terraentity.init.item.TEYoyosItems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.data.gen.recipe.ModRecipeProvider.createAdvancementHolder;

@SuppressWarnings("all")
public class CraftingRecipeProvider extends AbstractRecipeProvider {
    // 基础弓
    private final List<String> BOW_PATTERN = List.of(" #/", "# /", " #/");
    private final List<String> SHORT_BOW_PATTERN = List.of(" #", "#/", " #");

    //  基础工具，阔剑短剑
    private final List<String> AXE_PATTERN = List.of("##", "#/", " /");
    private final List<String> PICKAXE_PATTERN = List.of("###", " / ", " / ");
    private final List<String> HAMMER_PATTERN = List.of("###", "#/#", " / ");
    private final List<String> BROADSWORD_PATTERN = List.of("#", "#", "/");
    private final List<String> SHORT_SWORD_PATTERN = List.of(" #", "/ ");
    private final List<String> SHOVEL_PATTERN = List.of("#", "/", "/");
    private final List<String> HOE_PATTERN = List.of("##", " /", " /");

    // 基础砖
    private final List<String> BRICKS_PATTERN = List.of("##", "##");

    // 锁链
    private final List<String> CHAINS_PATTERN = List.of("#", "#", "#");

    // 纯净玻璃
    private final List<String> PURE_GLASS_PATTERN = List.of("###", "#/#", "###");

    // 基础桌
    private final List<String> TABLES_PATTERN = List.of("###", " # ");

    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        // 注册矿物块的合成与分解配方
        compressAndDecompressNine(output, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN, OreBlocks.TIN_BLOCK, ModTags.Items.STORAGE_BLOCKS_TIN);
        compressAndDecompressNine(output, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD, OreBlocks.LEAD_BLOCK, ModTags.Items.STORAGE_BLOCKS_LEAD);
        compressAndDecompressNine(output, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER, OreBlocks.SILVER_BLOCK, ModTags.Items.STORAGE_BLOCKS_SILVER);
        compressAndDecompressNine(output, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN, OreBlocks.TUNGSTEN_BLOCK, ModTags.Items.STORAGE_BLOCKS_TUNGSTEN);
        compressAndDecompressNine(output, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM, OreBlocks.PLATINUM_BLOCK, ModTags.Items.STORAGE_BLOCKS_PLATINUM);
        compressAndDecompressNine(output, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE, OreBlocks.METEORITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_METEORITE);
        compressAndDecompressNine(output, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE, OreBlocks.DEMONITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_DEMONITE);
        compressAndDecompressNine(output, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMTANE, OreBlocks.CRIMTANE_BLOCK, ModTags.Items.STORAGE_BLOCKS_CRIMTANE);
        compressAndDecompressNine(output, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE, OreBlocks.HELLSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_HELLSTONE);

        compressAndDecompressNine(output, MaterialItems.RUBY, ModTags.Items.GEMS_RUBY, DecorativeBlocks.RUBY_BLOCK, ModTags.Items.STORAGE_BLOCKS_RUBY);
        compressAndDecompressNine(output, MaterialItems.AMBER, ModTags.Items.GEMS_AMBER, DecorativeBlocks.AMBER_BLOCK, ModTags.Items.STORAGE_BLOCKS_AMBER);
        compressAndDecompressNine(output, MaterialItems.TOPAZ, ModTags.Items.GEMS_TOPAZ, DecorativeBlocks.TOPAZ_BLOCK, ModTags.Items.STORAGE_BLOCKS_TOPAZ);
        compressAndDecompressNine(output, MaterialItems.JADE, ModTags.Items.GEMS_JADE, DecorativeBlocks.JADE_BLOCK, ModTags.Items.STORAGE_BLOCKS_JADE);
        compressAndDecompressNine(output, MaterialItems.SAPPHIRE, ModTags.Items.GEMS_SAPPHIRE, DecorativeBlocks.SAPPHIRE_BLOCK, ModTags.Items.STORAGE_BLOCKS_SAPPHIRE);
        compressAndDecompressNine(output, MaterialItems.AMETHYST, ModTags.Items.GEMS_AMETHYST, DecorativeBlocks.AMETHYST_BLOCK, ModTags.Items.STORAGE_BLOCKS_AMETHYST);

        compressAndDecompressNine(output, MaterialItems.STURDY_FOSSIL, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL, OreBlocks.STURDY_FOSSIL_BLOCK, ModTags.Items.STORAGE_BLOCKS_STURDY_FOSSIL);
        compressAndDecompressNine(output, MaterialItems.OPAL, ModTags.Items.RAW_MATERIALS_OPAL, OreBlocks.OPAL_BLOCK, ModTags.Items.STORAGE_BLOCKS_OPAL);
        compressAndDecompressNine(output, MaterialItems.GELSTONE, ModTags.Items.RAW_MATERIALS_GELSTONE, OreBlocks.GELSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_GELSTONE);
        compressAndDecompressNine(output, MaterialItems.COLD_CRYSTAL, ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL, OreBlocks.COLD_CRYSTAL_BLOCK, ModTags.Items.STORAGE_BLOCKS_COLD_CRYSTAL);
        // 粗矿
        compressAndDecompressNine(output, MaterialItems.RAW_TIN, ModTags.Items.RAW_MATERIALS_TIN, OreBlocks.RAW_TIN_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_TIN);
        compressAndDecompressNine(output, MaterialItems.RAW_LEAD, ModTags.Items.RAW_MATERIALS_LEAD, OreBlocks.RAW_LEAD_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_LEAD);
        compressAndDecompressNine(output, MaterialItems.RAW_SILVER, ModTags.Items.RAW_MATERIALS_SILVER, OreBlocks.RAW_SILVER_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_SILVER);
        compressAndDecompressNine(output, MaterialItems.RAW_TUNGSTEN, ModTags.Items.RAW_MATERIALS_TUNGSTEN, OreBlocks.RAW_TUNGSTEN_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_TUNGSTEN);
        compressAndDecompressNine(output, MaterialItems.RAW_PLATINUM, ModTags.Items.RAW_MATERIALS_PLATINUM, OreBlocks.RAW_PLATINUM_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_PLATINUM);
        compressAndDecompressNine(output, MaterialItems.RAW_METEORITE, ModTags.Items.RAW_MATERIALS_METEORITE, OreBlocks.RAW_METEORITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_METEORITE);
        compressAndDecompressNine(output, MaterialItems.RAW_DEMONITE, ModTags.Items.RAW_MATERIALS_DEMONITE, OreBlocks.RAW_DEMONITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_DEMONITE);
        compressAndDecompressNine(output, MaterialItems.RAW_CRIMTANE, ModTags.Items.RAW_MATERIALS_CRIMTANE, OreBlocks.RAW_CRIMTANE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_CRIMTANE);
        compressAndDecompressNine(output, MaterialItems.RAW_HELLSTONE, ModTags.Items.RAW_MATERIALS_HELLSTONE, OreBlocks.RAW_HELLSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_HELLSTONE);
        // 飘飘麦捆
        compressAndDecompressNine(output, MaterialItems.FLOATING_WHEAT_HEADS, ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT, DecorativeBlocks.FLOATING_WHEAT_BALE, ModTags.Items.STORAGE_BLOCKS_FLOATING_WHEAT_BALE);
        // 矿物粒
        compressAndDecompressNine(output, MaterialItems.TIN_NUGGET, ModTags.Items.NUGGETS_TIN, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN);
        compressAndDecompressNine(output, MaterialItems.LEAD_NUGGET, ModTags.Items.NUGGETS_LEAD, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD);
        compressAndDecompressNine(output, MaterialItems.SILVER_NUGGET, ModTags.Items.NUGGETS_SILVER, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER);
        compressAndDecompressNine(output, MaterialItems.TUNGSTEN_NUGGET, ModTags.Items.NUGGETS_TUNGSTEN, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN);
        compressAndDecompressNine(output, MaterialItems.PLATINUM_NUGGET, ModTags.Items.NUGGETS_PLATINUM, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM);
        compressAndDecompressNine(output, MaterialItems.METEORITE_NUGGET, ModTags.Items.NUGGETS_METEORITE, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE);
        compressAndDecompressNine(output, MaterialItems.DEMONITE_NUGGET, ModTags.Items.NUGGETS_DEMONITE, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE);
        compressAndDecompressNine(output, MaterialItems.CRIMTANE_NUGGET, ModTags.Items.NUGGETS_CRIMTANE, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMTANE);
        compressAndDecompressNine(output, MaterialItems.HELLSTONE_NUGGET, ModTags.Items.NUGGETS_HELLSTONE, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE);
        // 铅砧
        shaped(output, ShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(ModTags.Items.STORAGE_BLOCKS_LEAD),
                'i', Ingredient.of(ModTags.Items.INGOTS_LEAD)
        ), List.of(
                "III",
                " i ",
                "iii"
        )), FunctionalBlocks.LEAD_ANVIL.toStack());
        // 房屋探测器
        shaped(output, ShapedRecipePattern.of(Map.of(
                'B', Ingredient.of(ItemTags.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                " B ",
                "B/B",
                "/ /"
        )), TEItems.HOUSE_DETECTOR.toStack());
        // 蛛网
        shaped(output, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                "/ /",
                " / ",
                "/ /"
        )), Items.COBWEB.getDefaultInstance());
        //
        shaped(output, "", "_from_lead_and_iron", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.REDSTONE_TORCH),
                'S', Ingredient.of(Items.STICK),
                'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "XSX",
                "X#X",
                "XSX"
        )), new ItemStack(Items.ACTIVATOR_RAIL, 6));
        // 广播盒
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.SIGNS),
                'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                'R', Ingredient.of(Items.REDSTONE)
        ), List.of(
                "RIR",
                "I#I",
                "RIR"
        )), FunctionalBlocks.ANNOUNCEMENT_BOX_ITEM.toStack());
        // 蜂蜜瓶
        shapeless(output, "", "_from_glass_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE)
        );
        shapeless(output, "", "_from_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );
        // 广播盒
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.PURE_GLASS)
        ), List.of(
                "# #",
                " # "
        )), PotionItems.BOTTLE.toStack(3));
        // 玻璃瓶
        shapeless(output, "", "_from_bottle",
                new ItemStack(Items.GLASS_BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );
        shapeless(output, "", "_from_glass_bottle",
                new ItemStack(PotionItems.BOTTLE.get()),
                Ingredient.of(Items.GLASS_BOTTLE)
        );

        // 各种片
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.SAND)
        ), List.of(
                "###"
        )), NatureBlocks.SAND_LAYER_BLOCK.toStack(6));

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.RED_SAND)
        ), List.of(
                "###"
        )), NatureBlocks.RED_SAND_LAYER_BLOCK.toStack(6));

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONSAND)
        ), List.of(
                "###"
        )), NatureBlocks.EBONSAND_LAYER_BLOCK.toStack(6));

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.CRIMSAND)
        ), List.of(
                "###"
        )), NatureBlocks.CRIMSAND_LAYER_BLOCK.toStack(6));

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARLSAND)
        ), List.of(
                "###"
        )), NatureBlocks.PEARLSAND_LAYER_BLOCK.toStack(6));
        // 砂岩箱
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.SANDSTONE)
        ), List.of(
                "###",
                "# #",
                "###"
        )), ChestBlocks.SANDSTONE_CHEST.toStack());

        // 大理石箱
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.MARBLE)
        ), List.of(
                "###",
                "# #",
                "###"
        )), ChestBlocks.MARBLE_CHEST.toStack());

        // 花岗岩箱
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.GRANITE)
        ), List.of(
                "###",
                "# #",
                "###"
        )), ChestBlocks.GRANITE_CHEST.toStack());

        for (LogBlockSet logBlockSet : LogBlockSet.LOG_BLOCK_SETS) {
            registerWoodRecipes(output, logBlockSet);
        }

        shaped(output, ShapedRecipePattern.of(Map.of(
                '|', Ingredient.of(Items.CHAIN),
                '#', Ingredient.of(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK)
        ), List.of(
                "| |",
                "###",
                "###"
        )), NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.HANGING_SIGN.toStack());

        // 木椅
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.OAK_PLANKS)
        ), List.of(
                "#  ",
                "#  ",
                "## "
        )), TFBlocks.OAK_SET.CHAIR.toStack());

        // 桌
        registeTableRecipes(output, Ingredient.of(Items.OAK_PLANKS), TFBlocks.OAK_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.ACACIA_PLANKS), TFBlocks.ACACIA_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.BAMBOO_PLANKS), TFBlocks.BAMBOO_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.BIRCH_PLANKS), TFBlocks.BIRCH_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.CHERRY_PLANKS), TFBlocks.CHERRY_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.CRIMSON_PLANKS), TFBlocks.CRIMSON_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.DARK_OAK_PLANKS), TFBlocks.DARK_OAK_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.JUNGLE_PLANKS), TFBlocks.JUNGLE_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.MANGROVE_PLANKS), TFBlocks.MANGROVE_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.SPRUCE_PLANKS), TFBlocks.SPRUCE_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.WARPED_PLANKS), TFBlocks.WARPED_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.PLANKS), TFBlocks.PINE_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.PLANKS), TFBlocks.BAOBAB_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), TFBlocks.ASH_WOOD_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), TFBlocks.SHADEWOOD_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), TFBlocks.EBONWOOD_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), TFBlocks.PEARLWOOD_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.SPOOKY_LOG_BLOCKS.PLANKS), TFBlocks.SPOOKY_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.FEY_LOG_BLOCKS.PLANKS), TFBlocks.FEYWOOD_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.PLANKS), TFBlocks.DYNASTY_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.PLANKS), TFBlocks.MUSHROOM_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.POLISHED_BLACKSTONE), TFBlocks.POLISHED_BLACKSTONE_SET.TABLE, 1);
        registeTableRecipes(output, Ingredient.of(Items.STONE), TFBlocks.STONE_SET.TABLE, 1);


        // 船
        registerBoatRecipes(output, NatureBlocks.EBONY_LOG_BLOCKS, BoatItems.EBONY_BOAT, BoatItems.EBONY_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.PEARL_LOG_BLOCKS, BoatItems.PEARL_BOAT, BoatItems.PEARL_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.SHADOW_LOG_BLOCKS, BoatItems.SHADOW_BOAT, BoatItems.SHADOW_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.PALM_LOG_BLOCKS, BoatItems.PALM_BOAT, BoatItems.PALM_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.BAOBAB_LOG_BLOCKS, BoatItems.BAOBAB_BOAT, BoatItems.BAOBAB_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS, BoatItems.GLOWING_MUSHROOM_BOAT, BoatItems.GLOWING_MUSHROOM_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS, BoatItems.YELLOW_WILLOW_BOAT, BoatItems.YELLOW_WILLOW_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.LIVING_LOG_BLOCKS, BoatItems.LIVING_BOAT, BoatItems.LIVING_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS, BoatItems.LIVING_MAHOGANY_BOAT, BoatItems.LIVING_MAHOGANY_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.ASH_LOG_BLOCKS, BoatItems.ASH_BOAT, BoatItems.ASH_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.SPOOKY_LOG_BLOCKS, BoatItems.SPOOKY_BOAT, BoatItems.SPOOKY_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.DYNASTY_LOG_BLOCKS, BoatItems.DYNASTY_BOAT, BoatItems.DYNASTY_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.PINE_LOG_BLOCKS, BoatItems.PINE_BOAT, BoatItems.PINE_CHEST_BOAT);
        registerBoatRecipes(output, NatureBlocks.FEY_LOG_BLOCKS, BoatItems.FEY_BOAT, BoatItems.FEY_CHEST_BOAT);

        // 基础盔甲
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), ArmorItems.ASH_HELMET, ArmorItems.ASH_CHESTPLATE, ArmorItems.ASH_LEGGINGS, ArmorItems.ASH_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), ArmorItems.EBONY_HELMET, ArmorItems.EBONY_CHESTPLATE, ArmorItems.EBONY_LEGGINGS, ArmorItems.EBONY_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), ArmorItems.SHADOW_PLANK_HELMET, ArmorItems.SHADOW_PLANK_CHESTPLATE, ArmorItems.SHADOW_PLANK_LEGGINGS, ArmorItems.SHADOW_PLANK_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), ArmorItems.PEARL_HELMET, ArmorItems.PEARL_CHESTPLATE, ArmorItems.PEARL_LEGGINGS, ArmorItems.PEARL_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INITIAL_WOOD), ArmorItems.PLANK_HELMET, ArmorItems.PLANK_CHESTPLATE, ArmorItems.PLANK_LEGGINGS, ArmorItems.PLANK_BOOTS);
        registerArmorRecipes(output, Ingredient.of(Items.CACTUS), ArmorItems.CACTUS_HELMET, ArmorItems.CACTUS_CHESTPLATE, ArmorItems.CACTUS_LEGGINGS, ArmorItems.CACTUS_BOOTS);
        registerArmorRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER), ArmorItems.COPPER_HELMET, ArmorItems.COPPER_CHESTPLATE, ArmorItems.COPPER_LEGGINGS, ArmorItems.COPPER_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN), ArmorItems.TIN_HELMET, ArmorItems.TIN_CHESTPLATE, ArmorItems.TIN_LEGGINGS, ArmorItems.TIN_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD), ArmorItems.LEAD_HELMET, ArmorItems.LEAD_CHESTPLATE, ArmorItems.LEAD_LEGGINGS, ArmorItems.LEAD_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER), ArmorItems.SILVER_HELMET, ArmorItems.SILVER_CHESTPLATE, ArmorItems.SILVER_LEGGINGS, ArmorItems.SILVER_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), ArmorItems.TUNGSTEN_HELMET, ArmorItems.TUNGSTEN_CHESTPLATE, ArmorItems.TUNGSTEN_LEGGINGS, ArmorItems.TUNGSTEN_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), ArmorItems.PLATINUM_HELMET, ArmorItems.PLATINUM_CHESTPLATE, ArmorItems.PLATINUM_LEGGINGS, ArmorItems.PLATINUM_BOOTS);
        // 基础弓
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER), BowItems.COPPER_BOW, BowItems.COPPER_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_GOLD), BowItems.GOLDEN_BOW, BowItems.GOLDEN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_IRON), BowItems.IRON_BOW, BowItems.IRON_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN), BowItems.TIN_BOW, BowItems.TIN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD), BowItems.LEAD_BOW, BowItems.LEAD_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER), BowItems.SILVER_BOW, BowItems.SILVER_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), BowItems.TUNGSTEN_BOW, BowItems.TUNGSTEN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), BowItems.PLATINUM_BOW, BowItems.PLATINUM_SHORT_BOW);
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.STICK),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.WOODEN_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.EBONWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.EBONWOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.SHADEWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.SHADEWOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.ASH_WOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.ASH_WOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.PEARLWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.PEARLWOOD_SHORT_BOW.toStack());
        // 基础工具，阔剑短剑
        registerToolRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER), AxeItems.COPPER_AXE, PickaxeItems.COPPER_PICKAXE, HammerItems.COPPER_HAMMER, SwordItems.COPPER_BROADSWORD, SwordItems.COPPER_SHORT_SWORD, ShovelItems.COPPER_SHOVEL, HoeItems.COPPER_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN), AxeItems.TIN_AXE, PickaxeItems.TIN_PICKAXE, HammerItems.TIN_HAMMER, SwordItems.TIN_BROADSWORD, SwordItems.TIN_SHORT_SWORD, ShovelItems.TIN_SHOVEL, HoeItems.TIN_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD), AxeItems.LEAD_AXE, PickaxeItems.LEAD_PICKAXE, HammerItems.LEAD_HAMMER, SwordItems.LEAD_BROADSWORD, SwordItems.LEAD_SHORT_SWORD, ShovelItems.LEAD_SHOVEL, HoeItems.LEAD_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER), AxeItems.SILVER_AXE, PickaxeItems.SILVER_PICKAXE, HammerItems.SILVER_HAMMER, SwordItems.SILVER_BROADSWORD, SwordItems.SILVER_SHORT_SWORD, ShovelItems.SILVER_SHOVEL, HoeItems.SILVER_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), AxeItems.TUNGSTEN_AXE, PickaxeItems.TUNGSTEN_PICKAXE, HammerItems.TUNGSTEN_HAMMER, SwordItems.TUNGSTEN_BROADSWORD, SwordItems.TUNGSTEN_SHORT_SWORD, ShovelItems.TUNGSTEN_SHOVEL, HoeItems.TUNGSTEN_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), AxeItems.PLATINUM_AXE, PickaxeItems.PLATINUM_PICKAXE, HammerItems.PLATINUM_HAMMER, SwordItems.PLATINUM_BROADSWORD, SwordItems.PLATINUM_SHORT_SWORD, ShovelItems.PLATINUM_SHOVEL, HoeItems.PLATINUM_HOE);
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Tags.Items.INGOTS_IRON),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                " #",
                "/ "
        )), SwordItems.IRON_SHORT_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Tags.Items.INGOTS_GOLD),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                " #",
                "/ "
        )), SwordItems.GOLDEN_SHORT_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INITIAL_WOOD),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.WOODEN_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.EBONWOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.SHADEWOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.ASH_WOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.PEARLWOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Tags.Items.INGOTS_GOLD),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.GOLDEN_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Tags.Items.INGOTS_IRON),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.IRON_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.CACTUS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.CACTUS_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.CACTUS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                " / ",
                " / "
        )), PickaxeItems.CACTUS_PICKAXE.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.EBONWOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.SHADEWOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.ASH_WOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.PEARLWOOD_SWORD.toStack());
        // 鱼竿
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "/ S",
                " /S",
                "  /"
        )), FishingPoleItems.WOOD_FISHING_POLE.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "  /",
                " /S",
                "/ S"
        )), FishingPoleItems.REINFORCED_FISHING_POLE.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "  /",
                " /S",
                "/ S"
        )), FishingPoleItems.FISHER_OF_SOULS.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "  /",
                " /S",
                "/ S"
        )), FishingPoleItems.FLESHCATCHER.toStack());
        // 木悠悠球
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(ItemTags.LOGS),
                '/', Ingredient.of(Items.COBWEB)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.WOODEN_YOYO.toStack());
        // 箭
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS),
                'O', Ingredient.of(ItemTags.LOGS),
                'a', Ingredient.of(ItemTags.WOOL)
        ), List.of(
                "#",
                "O",
                "a"
        )), new ItemStack(Items.ARROW, 12));
        // 王朝木衍生系列
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.PAPER)
        ), List.of(
                " S ",
                "S/S",
                " S "
        )), DecorativeBlocks.WHITE_PAPER_PANE.toStack());
        shapeless(output,
                DecorativeBlocks.WHITE_PAPER_PANE_LAMP.toStack(),
                Ingredient.of(DecorativeBlocks.WHITE_PAPER_PANE),
                Ingredient.of(Items.TORCH)
        );
        shapeless(output,
                DecorativeBlocks.MALACHITE_PAPER_PANE.toStack(),
                Ingredient.of(DecorativeBlocks.WHITE_PAPER_PANE),
                Ingredient.of(Items.CYAN_DYE)
        );
        shapeless(output,
                DecorativeBlocks.MALACHITE_PAPER_PANE_LAMP.toStack(),
                Ingredient.of(DecorativeBlocks.MALACHITE_PAPER_PANE),
                Ingredient.of(Items.TORCH)
        );
        shapeless(output,
                DecorativeBlocks.TRADITIONAL_DYNASTY_DOOR.toStack(),
                Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.DOOR),
                Ingredient.of(Items.RED_DYE),
                Ingredient.of(Items.BLACK_DYE)
        );
        // 松针手工绳套
        shapeless(output,
                ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET.toStack(),
                Ingredient.of(NatureBlocks.PINE_DROOPING_VINE),
                Ingredient.of(NatureBlocks.PINE_DROOPING_VINE)
        );
        shapeless(output,
                DecorativeBlocks.CHRISTMAS_PINE_TRAPDOOR.toStack(),
                Ingredient.of(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET),
                Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.TRAPDOOR)
        );
        shapeless(output,
                DecorativeBlocks.CHRISTMAS_PINE_DOOR.toStack(),
                Ingredient.of(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET),
                Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.DOOR)
        );
        // 生命篝火
        shaped(output, ShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.WOOD, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.WOOD, MaterialItems.LIFE_MUSHROOM),
                '/', Ingredient.of(Items.CAMPFIRE)
        ), List.of(
                " S ",
                "S/S"
        )), FunctionalBlocks.LIFE_CAMPFIRE.toStack());
        // 香蒲造纸
        shaped(output, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(ModItems.CATTAIL, ModItems.JUNGLE_CATTAIL, ModItems.GLOWING_MUSHROOM_CATTAIL, ModItems.EBONY_CATTAIL, ModItems.CRIMSON_CATTAIL, ModItems.HALLOW_CATTAIL)
        ), List.of(
                "///"
        )), new ItemStack(Items.PAPER, 3));

        // 地毯
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.COLORED_WOOL)
        ), List.of(
                "##"
        )), DecorativeBlocks.COLORED_CARPET.toStack(3));
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.FLINX_FUR_BLOCK)
        ), List.of(
                "##"
        )), DecorativeBlocks.FLINX_FUR_CARPET.toStack(3));
        // 便捷合成，无序合成
        shapeless(output, "", "_from_raw_copper",
                new ItemStack(Items.COPPER_INGOT),
                Ingredient.of(Items.RAW_COPPER),
                Ingredient.of(Items.RAW_COPPER),
                Ingredient.of(Items.RAW_COPPER)
        );
        shapeless(output, "", "_from_raw_tin",
                new ItemStack(MaterialItems.TIN_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_TIN),
                Ingredient.of(MaterialItems.RAW_TIN),
                Ingredient.of(MaterialItems.RAW_TIN)
        );
        shapeless(output, "", "_from_raw_copper",
                new ItemStack(Items.IRON_INGOT),
                Ingredient.of(Items.RAW_IRON),
                Ingredient.of(Items.RAW_IRON),
                Ingredient.of(Items.RAW_IRON)
        );
        shapeless(output, "", "_from_raw_lead",
                new ItemStack(MaterialItems.LEAD_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD)
        );
        shapeless(output, "", "_from_raw_silver",
                new ItemStack(MaterialItems.SILVER_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER)
        );
        shapeless(output, "", "_from_raw_tungsten",
                new ItemStack(MaterialItems.TUNGSTEN_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN)
        );

        shapeless(output, ToolItems.VINE_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE));
        shapeless(output, ToolItems.WEB_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE));
        shapeless(output, ToolItems.SILK_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE));
        shapeless(output, ToolItems.ROPE_COIL.toStack(), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE));

        shapeless(output, ArmorItems.GOLDEN_HELMET.toStack(), Ingredient.of(Items.GOLDEN_HELMET), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, ArmorItems.GOLDEN_CHESTPLATE.toStack(), Ingredient.of(Items.GOLDEN_CHESTPLATE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, ArmorItems.GOLDEN_LEGGINGS.toStack(), Ingredient.of(Items.GOLDEN_LEGGINGS), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, ArmorItems.GOLDEN_BOOTS.toStack(), Ingredient.of(Items.GOLDEN_BOOTS), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, SwordItems.GOLDEN_BROADSWORD.toStack(), Ingredient.of(Items.GOLDEN_SWORD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, AxeItems.GOLDEN_AXE.toStack(), Ingredient.of(Items.GOLDEN_AXE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, PickaxeItems.GOLDEN_PICKAXE.toStack(), Ingredient.of(Items.GOLDEN_PICKAXE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, HoeItems.GOLDEN_HOE.toStack(), Ingredient.of(Items.GOLDEN_HOE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(output, ShovelItems.GOLDEN_SHOVEL.toStack(), Ingredient.of(Items.GOLDEN_SHOVEL), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));

        shapeless(output, VanityArmorItems.GUY_FAWKES_MASK_SET.toStack(), Ingredient.of(VanityArmorItems.GUY_FAWKES_HAT), Ingredient.of(VanityArmorItems.GUY_FAWKES_MASK));


        shapeless(output, new ItemStack(Items.STRING, 2), Ingredient.of(ItemTags.WOOL));
        shapeless(output, "", "_from_stony_log", new ItemStack(Items.COBBLESTONE, 2), Ingredient.of(NatureBlocks.STONY_LOG));
        shapeless(output, "", "_from_gel", new ItemStack(Items.TORCH, 3), Ingredient.of(MaterialItems.GEL), Ingredient.of(Items.STICK));
        shapeless(output, "", "_from_slime_ball", new ItemStack(Items.TORCH, 3), Ingredient.of(Items.SLIME_BALL), Ingredient.of(Items.STICK));

        shapeless(output, ConsumableItems.MANA_CRYSTAL.toStack(), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(output, MaterialItems.FALLING_STAR.toStack(), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS));
        shapeless(output, FoodItems.CLOUD_DOUGH.toStack(), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS));
        shapeless(output, DecorativeBlocks.FLINX_FUR_BLOCK.toStack(20), Ingredient.of(MaterialItems.FLINX_FUR));

        //生鱼片
        shapeless(output, "", "_from_partial_mouth_fish",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.PARTIAL_MOUTH_FISH),
                Ingredient.of(FoodItems.PARTIAL_MOUTH_FISH)
        );
        shapeless(output, "", "_from_red_snapper",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.RED_SNAPPER),
                Ingredient.of(FoodItems.RED_SNAPPER)
        );
        shapeless(output,
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.SALMON),
                Ingredient.of(FoodItems.SALMON)
        );
        shapeless(output, "", "_from_salmon",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(Items.SALMON),
                Ingredient.of(Items.SALMON)
        );
        shapeless(output, "", "_from_tuna",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.TUNA),
                Ingredient.of(FoodItems.TUNA)
        );
        // 机关箱用陷阱箱合成方式
        shapeless(output, ChestBlocks.DEATH_GOLDEN_CHEST.toStack(), Ingredient.of(ChestBlocks.GOLDEN_CHEST), Ingredient.of(Items.TRIPWIRE_HOOK));
        shapeless(output, ChestBlocks.DEATH_WOODEN_CHEST.toStack(), Ingredient.of(Items.TRAPPED_CHEST), Ingredient.of(Items.TRIPWIRE_HOOK));

        shapeless(output, ConsumableItems.VILE_POWDER.toStack(5), Ingredient.of(NatureBlocks.VILE_MUSHROOM));
        shapeless(output, ConsumableItems.VICIOUS_POWDER.toStack(5), Ingredient.of(NatureBlocks.VICIOUS_MUSHROOM));
        shapeless(output, DecorativeBlocks.WOOD_STONE_SLATTED_BLOCKS.toStack(4), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS), Ingredient.of(ItemTags.PLANKS));
        shapeless(output, ConsumableItems.BOUNCY_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(output, ConsumableItems.BOUNCY_DYNAMITE.toStack(), Ingredient.of(ConsumableItems.DYNAMITE), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(output, ConsumableItems.BOUNCY_GRENADE.toStack(), Ingredient.of(ConsumableItems.GRENADE), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(output, ConsumableItems.STICKY_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.GEL));
        shapeless(output, ConsumableItems.STICKY_DIRT_BOMB.toStack(), Ingredient.of(ConsumableItems.DIRT_BOMB), Ingredient.of(MaterialItems.GEL));
        shapeless(output, ConsumableItems.STICKY_DYNAMITE.toStack(), Ingredient.of(ConsumableItems.DYNAMITE), Ingredient.of(MaterialItems.GEL));
        shapeless(output, ConsumableItems.STICKY_GRENADE.toStack(), Ingredient.of(ConsumableItems.GRENADE), Ingredient.of(MaterialItems.GEL));
        shapeless(output, ConsumableItems.SCARAB_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        shapeless(output, TEBoomerangItems.TRIMARANG.toStack(), Ingredient.of(TEBoomerangItems.ENCHANTED_BOOMERANG), Ingredient.of(TEBoomerangItems.ICE_BOOMERANG), Ingredient.of(TEBoomerangItems.SHROOMERANG));
        shapeless(output, TEBoomerangItems.ENCHANTED_BOOMERANG.toStack(), Ingredient.of(TEBoomerangItems.WOOD_BOOMERANG), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(output, BaitItems.ENCHANTED_NIGHTCRAWLER.toStack(), Ingredient.of(BaitItems.WORM), Ingredient.of(MaterialItems.FALLING_STAR));
        // 宝石树苗
        shapeless(output, NatureBlocks.RUBY_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_RUBY));
        shapeless(output, NatureBlocks.AMBER_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_AMBER));
        shapeless(output, NatureBlocks.TOPAZ_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_TOPAZ));
        shapeless(output, NatureBlocks.JADE_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_JADE));
        shapeless(output, NatureBlocks.SAPPHIRE_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_SAPPHIRE));
        shapeless(output, NatureBlocks.DIAMOND_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(Tags.Items.GEMS_DIAMOND));
        shapeless(output, NatureBlocks.AMETHYST_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_AMETHYST));

        // 基础石砖
        registerBricksRecipes(output, Ingredient.of(NatureBlocks.EBONSTONE), DecorativeBlocks.EBONSTONE_BRICKS);
        registerBricksRecipes(output, Ingredient.of(NatureBlocks.CRIMSTONE), DecorativeBlocks.CRIMSTONE_BRICKS);
        registerBricksRecipes(output, Ingredient.of(NatureBlocks.PEARLSTONE), DecorativeBlocks.PEARLSTONE_BRICKS);
        registerBricksRecipes(output, Ingredient.of(NatureBlocks.MARBLE), DecorativeBlocks.MARBLE_BRICKS);
        // 锁链
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_AMBER), DecorativeBlocks.AMBER_CHAIN);
        registerChainsRecipes(output, Ingredient.of(Tags.Items.GEMS_DIAMOND), DecorativeBlocks.DIAMOND_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_SAPPHIRE), DecorativeBlocks.SAPPHIRE_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_AMETHYST), DecorativeBlocks.AMETHYST_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_JADE), DecorativeBlocks.JADE_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_RUBY), DecorativeBlocks.RUBY_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ModTags.Items.GEMS_TOPAZ), DecorativeBlocks.TOPAZ_CHAIN);
        registerChainsRecipes(output, Ingredient.of(ConsumableItems.DUNGEON_DEMON_BONE), DecorativeBlocks.BONE_CHAIN);
        // 纯净玻璃
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BLACK_DYE), DecorativeBlocks.BLACK_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BLUE_DYE), DecorativeBlocks.BLUE_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BROWN_DYE), DecorativeBlocks.BROWN_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.CYAN_DYE), DecorativeBlocks.CYAN_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.GRAY_DYE), DecorativeBlocks.GRAY_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.GREEN_DYE), DecorativeBlocks.GREEN_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIGHT_BLUE_DYE), DecorativeBlocks.LIGHT_BLUE_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIGHT_GRAY_DYE), DecorativeBlocks.LIGHT_GRAY_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIME_DYE), DecorativeBlocks.LIME_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.MAGENTA_DYE), DecorativeBlocks.MAGENTA_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.ORANGE_DYE), DecorativeBlocks.ORANGE_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.PINK_DYE), DecorativeBlocks.PINK_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.PURPLE_DYE), DecorativeBlocks.PURPLE_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.RED_DYE), DecorativeBlocks.RED_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.WHITE_DYE), DecorativeBlocks.WHITE_PURE_GLASS, 8);
        registerPureGlassRecipes(output, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.YELLOW_DYE), DecorativeBlocks.YELLOW_PURE_GLASS, 8);
        // 拐杖糖块
        registerPureGlassRecipes(output, Ingredient.of(Items.SUGAR), Ingredient.of(Items.RED_DYE), DecorativeBlocks.RED_CANDY_BLOCK, 8);
        registerPureGlassRecipes(output, Ingredient.of(Items.SUGAR), Ingredient.of(Items.GREEN_DYE), DecorativeBlocks.GREEN_CANDY_BLOCK, 8);


        // 镶金方解石
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        'A', Ingredient.of(Blocks.CALCITE),
                        'S', Ingredient.of(Tags.Items.INGOTS_GOLD)
                ), List.of(
                        "AAA",
                        "ASA",
                        "AAA"
                )),
                DecorativeBlocks.GILDED_MARBLE.toStack(8)
        );
        // 蜂蜜月饼
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.SUGAR),
                        'H', Ingredient.of(Items.HONEY_BOTTLE),
                        'W', Ingredient.of(Items.WHEAT),
                        'E', Ingredient.of(Items.EGG)
                ), List.of(
                        "#H#",
                        "WEW"
                )),
                FoodItems.HONEY_MOONCAKES.toStack(2)
        );
        shapeless(output, FoodItems.HONEY_MOONCAKES_CHUNKS.toStack(3), Ingredient.of(FoodItems.HONEY_MOONCAKES));
        // 蛋黄月饼
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.SUGAR),
                        'W', Ingredient.of(Items.WHEAT),
                        'E', Ingredient.of(Items.EGG)
                ), List.of(
                        "#E#",
                        "WEW"
                )),
                FoodItems.EGG_YOLK_MOONCAKES.toStack(2)
        );
        shapeless(output, FoodItems.EGG_YOLK_MOONCAKES_CHUNKS.toStack(2), Ingredient.of(FoodItems.EGG_YOLK_MOONCAKES));
        // 蛛丝绳
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STRING)
                ), List.of(
                        "#",
                        "#",
                        "#"
                )),
                ModBlocks.WEB_ROPE.toStack(3)
        );
        // 蛛丝锁链
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STRING),
                        'C', Ingredient.of(Items.CHAIN)
                ), List.of(
                        "#C#",
                        "###",
                        "#C#"
                )),
                DecorativeBlocks.SILK_CHAIN.toStack(2)
        );
        // 寒霜魔棒
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '/', Ingredient.of(ManaWeaponItems.WAND_OF_SPARKING),
                        '#', Ingredient.of(MaterialItems.WINTER_MARROW)
                ), List.of(
                        " ##",
                        " ##",
                        "/  "
                )),
                ManaWeaponItems.WAND_OF_FROSTING.toStack()
        );
        // 重型工作台
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.CRAFTING_TABLE),
                        'a', Ingredient.of(Items.ANVIL, FunctionalBlocks.LEAD_ANVIL)
                ), List.of(
                        "#",
                        "a",
                        "#"
                )),
                FunctionalBlocks.HEAVY_WORK_BENCH.toStack()
        );
        // 风向标
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "###",
                        " # ",
                        " # "
                )),
                FunctionalBlocks.WEATHER_VANE.toStack()
        );
        // 凝灰岩展台
        shaped(output, "", "",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.TUFF)
                ), List.of(
                        "###",
                        " # ",
                        "###"
                )),
                FunctionalBlocks.TUFF_BOOTH.toStack(7)
        );
        // 瞬爆tnt
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.TNT),
                        'a', Ingredient.of(Items.GUNPOWDER),
                        'b', Ingredient.of(Items.REDSTONE),
                        'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        " c ",
                        "a#a",
                        " b "
                )),
                FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT.toStack()
        );
        // 魔镜
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        'G', Ingredient.of(Items.GLASS),
                        'D', Ingredient.of(Tags.Items.GEMS_DIAMOND),
                        'g', Ingredient.of(ModTags.Items.GOLD_AND_PLATINUM)
                ), List.of(
                        "gGg",
                        "GDG",
                        "gGg"
                )),
                TCItems.MAGIC_MIRROR.toStack()
        );
        // 金冠
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        'G', Ingredient.of(ModTags.Items.GEMS_RUBY),
                        'g', Ingredient.of(Tags.Items.INGOTS_GOLD)
                ), List.of(
                        "gGg",
                        "ggg"
                )),
                VanityArmorItems.GOLD_CROWN.toStack()
        );
        // 铂金冠
        shaped(output,
                ShapedRecipePattern.of(Map.of(
                        'G', Ingredient.of(ModTags.Items.GEMS_RUBY),
                        'g', Ingredient.of(ModTags.Items.INGOTS_PLATINUM)
                ), List.of(
                        "gGg",
                        "ggg"
                )),
                VanityArmorItems.PLATINUM_CROWN.toStack()
        );
        // 铅铁共用相关
        shaped(output, "", "_from_nuggets_lead",
                ShapedRecipePattern.of(Map.of(
                        'S', Ingredient.of(ModTags.Items.NUGGETS_LEAD)
                ), List.of(
                        "SSS",
                        "SSS",
                        "SSS"
                )),
                MaterialItems.LEAD_INGOT.toStack()
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.SMOOTH_STONE),
                        'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        'X', Ingredient.of(Items.FURNACE)
                ), List.of(
                        "III",
                        "IXI",
                        "###"
                )),
                new ItemStack(Items.BLAST_FURNACE)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        " # "
                )),
                new ItemStack(Items.BUCKET)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        "# #",
                        "###"
                )),
                new ItemStack(Items.CAULDRON)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STICK),
                        '$', Ingredient.of(Items.TRIPWIRE_HOOK),
                        '&', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '~', Ingredient.of(Items.STRING)
                ), List.of(
                        "#&#",
                        "~$~",
                        " # "
                )),
                new ItemStack(Items.CROSSBOW)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STONE_PRESSURE_PLATE),
                        'R', Ingredient.of(Items.REDSTONE),
                        'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "X X",
                        "X#X",
                        "XRX"
                )),
                new ItemStack(Items.DETECTOR_RAIL, 6)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'C', Ingredient.of(Items.CHEST),
                        'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "I I",
                        "ICI",
                        " I "
                )),
                new ItemStack(Items.HOPPER)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        "###"
                )),
                new ItemStack(Items.MINECART)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.COBBLESTONE),
                        'R', Ingredient.of(Items.REDSTONE),
                        'T', Ingredient.of(ItemTags.PLANKS),
                        'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "TTT",
                        "#X#",
                        "#R#"
                )),
                new ItemStack(Items.PISTON)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STICK),
                        'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "X X",
                        "X#X",
                        "X X"
                )),
                new ItemStack(Items.RAIL, 16)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'i', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "i ",
                        " i"
                )),
                new ItemStack(Items.SHEARS)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(ItemTags.PLANKS)
                ), List.of(
                        "#O#",
                        "###",
                        " # "
                )),
                new ItemStack(Items.SHIELD)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(ItemTags.PLANKS)
                ), List.of(
                        "OO",
                        "##",
                        "##"
                )),
                new ItemStack(Items.SMITHING_TABLE)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(Items.STONE)
                ), List.of(
                        " O ",
                        "###"
                )),
                new ItemStack(Items.STONECUTTER)
        );
        shaped(output, "", "_from_lead_and_iron",
                ShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        'S', Ingredient.of(Items.STICK),
                        '#', Ingredient.of(ItemTags.PLANKS)
                ), List.of(
                        "O",
                        "S",
                        "#"
                )),
                new ItemStack(Items.TRIPWIRE_HOOK, 2)
        );
        shapeless(output, new ItemStack(Items.FLINT_AND_STEEL), Ingredient.of(ModTags.Items.LEAD_AND_IRON), Ingredient.of(Items.FLINT));
        shapeless(output, new ItemStack(NatureBlocks.FEY_LOG_BLOCKS.LOG,8), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS),Ingredient.of(MaterialItems.LIFE_MUSHROOM));

        // 石头及深板岩压力板
        shaped(output, ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.STONE)), List.of("##")), new ItemStack(FunctionalBlocks.STONE_PRESSURE_PLATE));
        shaped(output, ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.DEEPSLATE)), List.of("##")), new ItemStack(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE));

        shapeless(output, ToolItems.NPC_INVITATION.toStack(), Ingredient.of(Items.PAPER), Ingredient.of(Items.HONEYCOMB, MaterialItems.ROYAL_WAX));
        shapeless(output, ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.toStack(), Ingredient.of(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP), Ingredient.of(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION));

        shapeless(output, FunctionalBlocks.PEACE_CANDLE.toStack(), Ingredient.of(ItemTags.CANDLES), AmountIngredient.of(2, ModTags.Items.GOLD_AND_PLATINUM), Ingredient.of(MaterialItems.PINK_GEL));

        shapeless(output, FunctionalBlocks.HEART_LANTERN.toStack(), Ingredient.of(Items.CHAIN), Ingredient.of(ConsumableItems.LIFE_CRYSTAL));
        shapeless(output, FunctionalBlocks.STAR_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(output, FunctionalBlocks.SOUL_OF_FLIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_FLIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_LIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_SIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_SIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_MIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_MIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_FRIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_FRIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_BRIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_BRIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_NIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_NIGHT));
        shapeless(output, FunctionalBlocks.SOUL_OF_VOIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_VOIGHT));


        shapeless(output, ConsumableItems.DRY_BOMB.toStack(), Ingredient.of(ConsumableItems.WET_BOMB, ConsumableItems.HONEY_BOMB, ConsumableItems.LAVA_BOMB));

        shapeless(output, ConsumableItems.WET_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(Items.WATER_BUCKET));
        shapeless(output, ConsumableItems.HONEY_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(ToolItems.HONEY_BUCKET));
        shapeless(output, ConsumableItems.LAVA_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(Items.LAVA_BUCKET));

//        shapeless(output, ConsumableItems.FALLEN_SOUL_CORE.toStack(), Ingredient.of(Items.BONE), Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(MaterialItems.FALLING_STAR));

        shapeless(output, DecorativeBlocks.POO_BLOCK.toStack(4), Ingredient.of(ModBlocks.POO));

        // 暗影蜡烛 shapeless(output, ToolItems.SHADOW_CANDLE.toStack(), Ingredient.of(ItemTags.CANDLES), AmountIngredient.of(3,ModTags.Items.EVIL_INGOT));
        // 钱币
        shapeless(output, ModItems.COPPER_COIN.toStack(100), Ingredient.of(ModItems.SILVER_COIN));
        shapeless(output, ModItems.SILVER_COIN.toStack(100), Ingredient.of(ModItems.GOLD_COIN));
        shapeless(output, ModItems.GOLD_COIN.toStack(100), Ingredient.of(ModItems.PLATINUM_COIN));

        shapeless(output, MaterialItems.RAW_ASPHALT.toStack(), AmountIngredient.of(2, ItemTags.STONE_CRAFTING_MATERIALS), Ingredient.of(MaterialItems.GEL));

        output.accept(Confluence.asResource("boom_bunny"), BoomBunnyRecipe.getInstance(), null);
    }

    protected void shaped(RecipeOutput recipeOutput, String prefix, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, true), createAdvancementHolder(recipeOutput, id, pattern.ingredients()));
    }

    protected void shaped(RecipeOutput recipeOutput, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource(getItemName(result.getItem()));
        recipeOutput.accept(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, true), createAdvancementHolder(recipeOutput, id, pattern.ingredients()));
    }

    protected void shapeless(RecipeOutput recipeOutput, String prefix, String suffix, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new ShapelessRecipe("", CraftingBookCategory.MISC, result, zingredients), createAdvancementHolder(recipeOutput, id, zingredients));
    }

    protected void shapeless(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource(getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new ShapelessRecipe("", CraftingBookCategory.MISC, result, zingredients), createAdvancementHolder(recipeOutput, id, zingredients));
    }

    // 九原料合成一块的合成及分解配方
    protected void compressAndDecompressNine(RecipeOutput recipeOutput, ItemLike decompressed, TagKey<Item> decompressedTag, ItemLike compressed, TagKey<Item> compressedTag) {
        ResourceLocation id1 = Confluence.asResource(getItemName(decompressed).concat("_from_decompacting"));
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(compressedTag));
        recipeOutput.accept(id1, new ShapelessRecipe("", CraftingBookCategory.BUILDING, new ItemStack(decompressed, 9), ingredients), createAdvancementHolder(recipeOutput, id1, ingredients));
        ResourceLocation id2 = Confluence.asResource(getItemName(compressed).concat("_from_compacting"));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(Map.of('A', Ingredient.of(decompressedTag)), List.of("AAA", "AAA", "AAA"));
        recipeOutput.accept(id2, new ShapedRecipe("", CraftingBookCategory.BUILDING, pattern, compressed.asItem().getDefaultInstance()), createAdvancementHolder(recipeOutput, id2, pattern.ingredients()));
    }

    // 木头配方
    private void registerWoodRecipes(RecipeOutput output, LogBlockSet logBlockSet) {
        ItemLike[] logs = Streams.of(logBlockSet.LOG, logBlockSet.STRIPPED_LOG, logBlockSet.WOOD, logBlockSet.STRIPPED_WOOD).filter(DeferredHolder::isBound).toArray(ItemLike[]::new);
        if (logs.length > 0) shapeless(output, logBlockSet.PLANKS.toStack(4), Ingredient.of(logs));
        shapeless(output, logBlockSet.BUTTON.toStack(), Ingredient.of(logBlockSet.PLANKS));
        if (logBlockSet.LOG.isBound() && logBlockSet.WOOD.isBound())
            shaped(output, ShapedRecipePattern.of(Map.of(
                    '#', Ingredient.of(logBlockSet.LOG)
            ), List.of(
                    "##",
                    "##"
            )), logBlockSet.WOOD.toStack(3));
        if (logBlockSet.STRIPPED_LOG.isBound() && logBlockSet.STRIPPED_WOOD.isBound())
            shaped(output, ShapedRecipePattern.of(Map.of(
                    '#', Ingredient.of(logBlockSet.STRIPPED_LOG)
            ), List.of(
                    "##",
                    "##"
            )), logBlockSet.STRIPPED_WOOD.toStack(3));
        if (logBlockSet.STAIRS.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS)
        ), List.of(
                "#  ",
                "## ",
                "###"
        )), logBlockSet.STAIRS.toStack(4));
        if (logBlockSet.SLAB.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS)
        ), List.of(
                "###"
        )), logBlockSet.SLAB.toStack(6));
        if (logBlockSet.FENCE.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#/#",
                "#/#"
        )), logBlockSet.FENCE.toStack(3));
        if (logBlockSet.FENCE_GATE.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "/#/",
                "/#/"
        )), logBlockSet.FENCE_GATE.toStack());
        if (logBlockSet.DOOR.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS)
        ), List.of(
                "##",
                "##",
                "##"
        )), logBlockSet.DOOR.toStack(3));
        if (logBlockSet.TRAPDOOR.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS)
        ), List.of(
                "###",
                "###"
        )), logBlockSet.TRAPDOOR.toStack(2));
        if (logBlockSet.PRESSURE_PLATE.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS)
        ), List.of(
                "##"
        )), logBlockSet.PRESSURE_PLATE.toStack());
        if (logBlockSet.SIGN_ITEM.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "###",
                " / "
        )), logBlockSet.SIGN_ITEM.toStack(3));
        if (logBlockSet.CHISELED_PLANKS.isBound()) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.SLAB)
        ), List.of(
                "#",
                "#"
        )), logBlockSet.CHISELED_PLANKS.toStack());
        if (logBlockSet.HANGING_SIGN.isBound() && logBlockSet.STRIPPED_LOG.isBound())
            shaped(output, ShapedRecipePattern.of(Map.of(
                    '|', Ingredient.of(Blocks.CHAIN),
                    '#', Ingredient.of(logBlockSet.STRIPPED_LOG)
            ), List.of(
                    "| |",
                    "###",
                    "###"
            )), logBlockSet.HANGING_SIGN.toStack(6));
    }

    private void registerBoatRecipes(RecipeOutput output, LogBlockSet woodSet, ItemLike boatItem, ItemLike chestBoatItem) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(woodSet.PLANKS)
        ), List.of(
                "# #",
                "###"
        )), boatItem.asItem().getDefaultInstance());

        shapeless(output, chestBoatItem.asItem().getDefaultInstance(),
                Ingredient.of(boatItem),
                Ingredient.of(Tags.Items.CHESTS_WOODEN)
        );
    }

    private void registerArmorRecipes(RecipeOutput output, Ingredient materialIngredient, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "###",
                "# #"
        )), helmet.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "# #",
                "# #"
        )), boots.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "###",
                "# #",
                "# #"
        )), leggings.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "# #",
                "###",
                "###"
        )), chestplate.asItem().getDefaultInstance());
    }

    private void registerBowRecipes(RecipeOutput output, Ingredient material, ItemLike normalBow, ItemLike shortBow) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STRING)
        ), BOW_PATTERN), normalBow.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STRING)
        ), SHORT_BOW_PATTERN), shortBow.asItem().getDefaultInstance());
    }

    private void registerToolRecipes(RecipeOutput output, Ingredient material, ItemLike axe, ItemLike pickaxe, ItemLike hammer, ItemLike broadsword, ItemLike shortsword, ItemLike shovel, ItemLike hoe) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), AXE_PATTERN), axe.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), PICKAXE_PATTERN), pickaxe.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), HAMMER_PATTERN), hammer.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), BROADSWORD_PATTERN), broadsword.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), SHORT_SWORD_PATTERN), shortsword.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), SHOVEL_PATTERN), shovel.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), HOE_PATTERN), hoe.asItem().getDefaultInstance());
    }

    private void registerBricksRecipes(RecipeOutput output, Ingredient material, ItemLike bricks) {
        shaped(output, ShapedRecipePattern.of(Map.of('#', material), BRICKS_PATTERN), bricks.asItem().getDefaultInstance());
    }

    private void registerChainsRecipes(RecipeOutput output, Ingredient material, ItemLike chains) {
        shaped(output, ShapedRecipePattern.of(Map.of('#', material), CHAINS_PATTERN), chains.asItem().getDefaultInstance());
    }

    private void registerPureGlassRecipes(RecipeOutput output, Ingredient material, Ingredient material2, ItemLike block, int count) {
        ItemStack result = new ItemStack(block.asItem(), count);
        shaped(output, ShapedRecipePattern.of(Map.of('#', material, '/', material2), PURE_GLASS_PATTERN), result);
    }

    private void registeTableRecipes(RecipeOutput output, Ingredient material, ItemLike block, int count) {
        ItemStack result = new ItemStack(block.asItem(), count);
        shaped(output, ShapedRecipePattern.of(Map.of('#', material), TABLES_PATTERN), result);
    }
}
