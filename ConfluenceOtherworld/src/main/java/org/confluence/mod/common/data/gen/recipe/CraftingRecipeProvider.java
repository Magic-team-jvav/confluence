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
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.confluence.mod.common.data.gen.recipe.ModRecipeProvider.createAdvancementHolder;

@SuppressWarnings("all")
public class CraftingRecipeProvider extends AbstractRecipeProvider {
    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    // 木头配方
    private void registerWoodRecipes(RecipeOutput output, LogBlockSet logBlockSet, TagKey<Item> woodLogTag) {
        shapeless(output, logBlockSet.getPlanks().toStack(4), Ingredient.of(woodLogTag));
        shapeless(output, logBlockSet.getButton().toStack(), Ingredient.of(logBlockSet.getPlanks()));
        if (logBlockSet.getLog() != null && logBlockSet.getWood() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getLog())
        ), List.of(
                "##",
                "##"
        )), logBlockSet.getWood().toStack(3));
        if (logBlockSet.getStrippedLog() != null && logBlockSet.getStrippedWood() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getStrippedLog())
        ), List.of(
                "##",
                "##"
        )), logBlockSet.getStrippedWood().toStack(3));
        if (logBlockSet.getStairs() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks())
        ), List.of(
                "#  ",
                "## ",
                "###"
        )), logBlockSet.getStairs().toStack(4));
        if (logBlockSet.getSlab() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks())
        ), List.of(
                "###"
        )), logBlockSet.getSlab().toStack(6));
        if (logBlockSet.getFence() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#/#",
                "#/#"
        )), logBlockSet.getFence().toStack(3));
        if (logBlockSet.getFenceGate() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "/#/",
                "/#/"
        )), logBlockSet.getFenceGate().toStack());
        if (logBlockSet.getDoor() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks())
        ), List.of(
                "##",
                "##",
                "##"
        )), logBlockSet.getDoor().toStack(3));
        if (logBlockSet.getTrapdoor() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks())
        ), List.of(
                "###",
                "###"
        )), logBlockSet.getTrapdoor().toStack(2));
        if (logBlockSet.getPressurePlate() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks())
        ), List.of(
                "##"
        )), logBlockSet.getPressurePlate().toStack());
        if (logBlockSet.getSignItem() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(logBlockSet.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "###",
                " / "
        )), logBlockSet.getSignItem().toStack());
        if (logBlockSet.getHangingSignItem() != null && logBlockSet.getStrippedLog() != null) shaped(output, ShapedRecipePattern.of(Map.of(
                '|', Ingredient.of(Blocks.CHAIN),
                '#', Ingredient.of(logBlockSet.getStrippedLog())
        ), List.of(
                "| |",
                "###",
                "###"
        )), logBlockSet.getHangingSignItem().toStack(6));
    }

    private void registerBoatRecipes(RecipeOutput output, LogBlockSet woodSet, ItemLike boatItem, ItemLike chestBoatItem) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(woodSet.getPlanks())
        ), List.of(
                "# #",
                "###"
        )), boatItem.asItem().getDefaultInstance());

        shapeless(output, chestBoatItem.asItem().getDefaultInstance(),
                Ingredient.of(boatItem),
                Ingredient.of(Tags.Items.CHESTS_WOODEN)
        );
    }

    private void registerArmorRecipes(RecipeOutput output, Ingredient materialIngredient,
                                      ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
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
    // 基础弓
    private static final List<String> BOW_PATTERN = List.of(" #/", "# /", " #/");
    private static final List<String> SHORT_BOW_PATTERN = List.of(" #", "#/", " #");
    private void registerBowRecipes(RecipeOutput output, Ingredient material,
                                    ItemLike normalBow, ItemLike shortBow) {
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STRING)
        ), BOW_PATTERN), normalBow.asItem().getDefaultInstance());

        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STRING)
        ), SHORT_BOW_PATTERN), shortBow.asItem().getDefaultInstance());
    }
    //  基础工具，阔剑短剑
    private static final List<String> AXE_PATTERN = List.of("##", "#/", " /");
    private static final List<String> PICKAXE_PATTERN = List.of("###", " / ", " / ");
    private static final List<String> HAMMER_PATTERN = List.of("###", "#/#", " / ");
    private static final List<String> BROADSWORD_PATTERN = List.of("#", "#", "/");
    private static final List<String> SHORT_SWORD_PATTERN = List.of(" #", "/ ");
    private static final List<String> SHOVEL_PATTERN = List.of("#", "/", "/");
    private static final List<String> HOE_PATTERN = List.of("##", " /", " /");
    private void registerToolRecipes(RecipeOutput output, Ingredient material,
                                     ItemLike axe, ItemLike pickaxe, ItemLike hammer,
                                     ItemLike broadsword,ItemLike shortsword, ItemLike shovel, ItemLike hoe) {
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
    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        // 注册矿物块的合成与分解配方
        compressAndDecompressNine(output, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN, OreBlocks.TIN_BLOCK, ModTags.Items.TIN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD, OreBlocks.LEAD_BLOCK, ModTags.Items.LEAD_BLOCK);
        compressAndDecompressNine(output, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER, OreBlocks.SILVER_BLOCK, ModTags.Items.SILVER_BLOCK);
        compressAndDecompressNine(output, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN, OreBlocks.TUNGSTEN_BLOCK, ModTags.Items.TUNGSTEN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM, OreBlocks.PLATINUM_BLOCK, ModTags.Items.PLATINUM_BLOCK);
        compressAndDecompressNine(output, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE, OreBlocks.METEORITE_BLOCK, ModTags.Items.METEORITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE, OreBlocks.DEMONITE_BLOCK, ModTags.Items.DEMONITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMTANE, OreBlocks.CRIMTANE_BLOCK, ModTags.Items.CRIMTANE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE, OreBlocks.HELLSTONE_BLOCK, ModTags.Items.HELLSTONE_BLOCK);

        compressAndDecompressNine(output, MaterialItems.RUBY, ModTags.Items.GEMS_RUBY, DecorativeBlocks.RUBY_BLOCK, ModTags.Items.RUBY_BLOCK);
        compressAndDecompressNine(output, MaterialItems.AMBER, ModTags.Items.GEMS_AMBER, DecorativeBlocks.AMBER_BLOCK, ModTags.Items.AMBER_BLOCK);
        compressAndDecompressNine(output, MaterialItems.TOPAZ, ModTags.Items.GEMS_TOPAZ, DecorativeBlocks.TOPAZ_BLOCK, ModTags.Items.TOPAZ_BLOCK);
        compressAndDecompressNine(output, MaterialItems.JADE, ModTags.Items.GEMS_JADE, DecorativeBlocks.JADE_BLOCK, ModTags.Items.JADE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.SAPPHIRE, ModTags.Items.GEMS_SAPPHIRE, DecorativeBlocks.SAPPHIRE_BLOCK, ModTags.Items.SAPPHIRE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.AMETHYST, ModTags.Items.GEMS_AMETHYST, DecorativeBlocks.AMETHYST_BLOCK, ModTags.Items.AMETHYST_BLOCK);

        compressAndDecompressNine(output, MaterialItems.STURDY_FOSSIL, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL, OreBlocks.STURDY_FOSSIL_BLOCK, ModTags.Items.STURDY_FOSSIL_BLOCK);
        compressAndDecompressNine(output, MaterialItems.OPAL, ModTags.Items.RAW_MATERIALS_OPAL, OreBlocks.OPAL_BLOCK, ModTags.Items.OPAL_BLOCK);
        compressAndDecompressNine(output, MaterialItems.GELSTONE, ModTags.Items.RAW_MATERIALS_GELSTONE, OreBlocks.GELSTONE_BLOCK, ModTags.Items.GELSTONE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.COLD_CRYSTAL, ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL, OreBlocks.COLD_CRYSTAL_BLOCK, ModTags.Items.COLD_CRYSTAL_BLOCK);
        // 粗矿
        compressAndDecompressNine(output, MaterialItems.RAW_TIN, ModTags.Items.RAW_MATERIALS_TIN, OreBlocks.RAW_TIN_BLOCK, ModTags.Items.RAW_MATERIALS_TIN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_LEAD, ModTags.Items.RAW_MATERIALS_LEAD, OreBlocks.RAW_LEAD_BLOCK, ModTags.Items.RAW_MATERIALS_LEAD_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_SILVER, ModTags.Items.RAW_MATERIALS_SILVER, OreBlocks.RAW_SILVER_BLOCK, ModTags.Items.RAW_MATERIALS_SILVER_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_TUNGSTEN, ModTags.Items.RAW_MATERIALS_TUNGSTEN, OreBlocks.RAW_TUNGSTEN_BLOCK, ModTags.Items.RAW_MATERIALS_TUNGSTEN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_PLATINUM, ModTags.Items.RAW_MATERIALS_PLATINUM, OreBlocks.RAW_PLATINUM_BLOCK, ModTags.Items.RAW_MATERIALS_PLATINUM_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_METEORITE, ModTags.Items.RAW_MATERIALS_METEORITE, OreBlocks.RAW_METEORITE_BLOCK, ModTags.Items.RAW_MATERIALS_METEORITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_DEMONITE, ModTags.Items.RAW_MATERIALS_DEMONITE, OreBlocks.RAW_DEMONITE_BLOCK, ModTags.Items.RAW_MATERIALS_DEMONITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_CRIMTANE, ModTags.Items.RAW_MATERIALS_CRIMTANE, OreBlocks.RAW_CRIMTANE_BLOCK, ModTags.Items.RAW_MATERIALS_CRIMTANE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.RAW_HELLSTONE, ModTags.Items.RAW_MATERIALS_HELLSTONE, OreBlocks.RAW_HELLSTONE_BLOCK, ModTags.Items.RAW_MATERIALS_HELLSTONE_BLOCK);
        // 飘飘麦捆
        compressAndDecompressNine(output, MaterialItems.FLOATING_WHEAT_HEADS, ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT, DecorativeBlocks.FLOATING_WHEAT_BALE, ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT_BALE);
        // 矿物粒
        // 铅砧
        shaped(output, ShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(ModTags.Items.LEAD_BLOCK),
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
        // 玻璃瓶
        // 广播盒
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.PURE_GLASS)
        ), List.of(
                "# #",
                " # "
        )), PotionItems.BOTTLE.toStack(3));
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

        // 木头系列
        registerWoodRecipes(output, NatureBlocks.EBONY_LOG_BLOCKS, ModTags.Items.EBONY_LOGS);
        registerWoodRecipes(output, NatureBlocks.PEARL_LOG_BLOCKS, ModTags.Items.PEARL_LOGS);
        registerWoodRecipes(output, NatureBlocks.SHADOW_LOG_BLOCKS, ModTags.Items.SHADOW_LOGS);
        registerWoodRecipes(output, NatureBlocks.PALM_LOG_BLOCKS, ModTags.Items.PALM_LOGS);
        registerWoodRecipes(output, NatureBlocks.BAOBAB_LOG_BLOCKS, ModTags.Items.BAOBAB_LOGS);
        registerWoodRecipes(output, NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS, ModTags.Items.GLOWING_MUSHROOM_STEMS);
        registerWoodRecipes(output, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS, ModTags.Items.YELLOW_WILLOW_LOGS);
        registerWoodRecipes(output, NatureBlocks.LIVING_LOG_BLOCKS, ModTags.Items.LIVING_LOGS);
        registerWoodRecipes(output, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS, ModTags.Items.LIVING_MAHOGANY_LOGS);
        registerWoodRecipes(output, NatureBlocks.ASH_LOG_BLOCKS, ModTags.Items.ASH_LOGS);
        registerWoodRecipes(output, NatureBlocks.SPOOKY_LOG_BLOCKS, ModTags.Items.SPOOKY_LOGS);

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

        // 基础盔甲
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()), ArmorItems.ASH_HELMET, ArmorItems.ASH_CHESTPLATE, ArmorItems.ASH_LEGGINGS, ArmorItems.ASH_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()), ArmorItems.EBONY_HELMET, ArmorItems.EBONY_CHESTPLATE, ArmorItems.EBONY_LEGGINGS, ArmorItems.EBONY_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()), ArmorItems.SHADOW_PLANK_HELMET, ArmorItems.SHADOW_PLANK_CHESTPLATE, ArmorItems.SHADOW_PLANK_LEGGINGS, ArmorItems.SHADOW_PLANK_BOOTS);
        registerArmorRecipes(output, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()), ArmorItems.PEARL_HELMET, ArmorItems.PEARL_CHESTPLATE, ArmorItems.PEARL_LEGGINGS, ArmorItems.PEARL_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INITIAL_WOOD), ArmorItems.PLANK_HELMET, ArmorItems.PLANK_CHESTPLATE, ArmorItems.PLANK_LEGGINGS, ArmorItems.PLANK_BOOTS);
        registerArmorRecipes(output, Ingredient.of(Items.CACTUS), ArmorItems.CACTUS_HELMET, ArmorItems.CACTUS_CHESTPLATE, ArmorItems.CACTUS_LEGGINGS, ArmorItems.CACTUS_BOOTS);
        registerArmorRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER), ArmorItems.COPPER_HELMET, ArmorItems.COPPER_CHESTPLATE, ArmorItems.COPPER_LEGGINGS, ArmorItems.COPPER_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN), ArmorItems.TIN_HELMET, ArmorItems.TIN_CHESTPLATE, ArmorItems.TIN_LEGGINGS, ArmorItems.TIN_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD), ArmorItems.LEAD_HELMET, ArmorItems.LEAD_CHESTPLATE, ArmorItems.LEAD_LEGGINGS, ArmorItems.LEAD_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER), ArmorItems.SILVER_HELMET, ArmorItems.SILVER_CHESTPLATE, ArmorItems.SILVER_LEGGINGS, ArmorItems.SILVER_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), ArmorItems.TUNGSTEN_HELMET, ArmorItems.TUNGSTEN_CHESTPLATE, ArmorItems.TUNGSTEN_LEGGINGS, ArmorItems.TUNGSTEN_BOOTS);
        registerArmorRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), ArmorItems.PLATINUM_HELMET, ArmorItems.PLATINUM_CHESTPLATE, ArmorItems.PLATINUM_LEGGINGS, ArmorItems.PLATINUM_BOOTS);
        // 基础弓
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER),BowItems.COPPER_BOW,BowItems.COPPER_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_GOLD),BowItems.GOLDEN_BOW,BowItems.GOLDEN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(Tags.Items.INGOTS_IRON),BowItems.IRON_BOW,BowItems.IRON_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN),BowItems.TIN_BOW,BowItems.TIN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD),BowItems.LEAD_BOW,BowItems.LEAD_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER),BowItems.SILVER_BOW,BowItems.SILVER_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN),BowItems.TUNGSTEN_BOW,BowItems.TUNGSTEN_SHORT_BOW);
        registerBowRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM),BowItems.PLATINUM_BOW,BowItems.PLATINUM_SHORT_BOW);
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.STICK),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.WOODEN_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.BOW)
                ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.EBONWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.EBONWOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.SHADEWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.SHADEWOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.ASH_WOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.ASH_WOOD_SHORT_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.PEARLWOOD_BOW.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(BowItems.WOODEN_SHORT_BOW)
        ), List.of(
                " #",
                "#/",
                " #"
        )), BowItems.PEARLWOOD_SHORT_BOW.toStack());
        // 基础工具，阔剑短剑
        registerToolRecipes(output, Ingredient.of(Tags.Items.INGOTS_COPPER),AxeItems.COPPER_AXE,PickaxeItems.COPPER_PICKAXE,HammerItems.COPPER_HAMMER,SwordItems.COPPER_BROADSWORD,SwordItems.COPPER_SHORT_SWORD,ShovelItems.COPPER_SHOVEL,HoeItems.COPPER_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TIN),AxeItems.TIN_AXE,PickaxeItems.TIN_PICKAXE,HammerItems.TIN_HAMMER,SwordItems.TIN_BROADSWORD,SwordItems.TIN_SHORT_SWORD,ShovelItems.TIN_SHOVEL,HoeItems.TIN_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_LEAD),AxeItems.LEAD_AXE,PickaxeItems.LEAD_PICKAXE,HammerItems.LEAD_HAMMER,SwordItems.LEAD_BROADSWORD,SwordItems.LEAD_SHORT_SWORD,ShovelItems.LEAD_SHOVEL,HoeItems.LEAD_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_SILVER),AxeItems.SILVER_AXE,PickaxeItems.SILVER_PICKAXE,HammerItems.SILVER_HAMMER,SwordItems.SILVER_BROADSWORD,SwordItems.SILVER_SHORT_SWORD,ShovelItems.SILVER_SHOVEL,HoeItems.SILVER_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN),AxeItems.TUNGSTEN_AXE,PickaxeItems.TUNGSTEN_PICKAXE,HammerItems.TUNGSTEN_HAMMER,SwordItems.TUNGSTEN_BROADSWORD,SwordItems.TUNGSTEN_SHORT_SWORD,ShovelItems.TUNGSTEN_SHOVEL,HoeItems.TUNGSTEN_HOE);
        registerToolRecipes(output, Ingredient.of(ModTags.Items.INGOTS_PLATINUM),AxeItems.PLATINUM_AXE,PickaxeItems.PLATINUM_PICKAXE,HammerItems.PLATINUM_HAMMER,SwordItems.PLATINUM_BROADSWORD,SwordItems.PLATINUM_SHORT_SWORD,ShovelItems.PLATINUM_SHOVEL,HoeItems.PLATINUM_HOE);
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
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.EBONWOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.SHADEWOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "#/#",
                " / "
        )), HammerItems.ASH_WOOD_HAMMER.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()),
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
                '#', Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.EBONWOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.SHADEWOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.getPlanks()),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.ASH_WOOD_SWORD.toStack());
        shaped(output, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.getPlanks()),
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
        shapeless(output, ToolItems.VINE_ROPE_COIL.toStack(),Ingredient.of(ModBlocks.VINE_ROPE),Ingredient.of(ModBlocks.VINE_ROPE),Ingredient.of(ModBlocks.VINE_ROPE),Ingredient.of(ModBlocks.VINE_ROPE));
        shapeless(output, ToolItems.WEB_ROPE_COIL.toStack(),Ingredient.of(ModBlocks.WEB_ROPE),Ingredient.of(ModBlocks.WEB_ROPE),Ingredient.of(ModBlocks.WEB_ROPE),Ingredient.of(ModBlocks.WEB_ROPE));
        shapeless(output, ToolItems.SILK_ROPE_COIL.toStack(),Ingredient.of(ModBlocks.SILK_ROPE),Ingredient.of(ModBlocks.SILK_ROPE),Ingredient.of(ModBlocks.SILK_ROPE),Ingredient.of(ModBlocks.SILK_ROPE));
        shapeless(output, ToolItems.ROPE_COIL.toStack(),Ingredient.of(ModBlocks.ROPE),Ingredient.of(ModBlocks.ROPE),Ingredient.of(ModBlocks.ROPE),Ingredient.of(ModBlocks.ROPE));
        shapeless(output, ConsumableItems.MANA_CRYSTAL.toStack(),Ingredient.of(MaterialItems.FALLING_STAR),Ingredient.of(MaterialItems.FALLING_STAR),Ingredient.of(MaterialItems.FALLING_STAR),Ingredient.of(MaterialItems.FALLING_STAR),Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(output, MaterialItems.FALLING_STAR.toStack(),Ingredient.of(MaterialItems.STAR_PETALS),Ingredient.of(MaterialItems.STAR_PETALS),Ingredient.of(MaterialItems.STAR_PETALS),Ingredient.of(MaterialItems.STAR_PETALS),Ingredient.of(MaterialItems.STAR_PETALS));
        shapeless(output, FoodItems.CLOUD_DOUGH.toStack(),Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS),Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS),Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS));

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
        shapeless(output, TEBoomerangItems.TRIMARANG.toStack(), Ingredient.of(TEBoomerangItems.ENCHANTED_BOOMERANG),Ingredient.of(TEBoomerangItems.ICE_BOOMERANG),Ingredient.of(TEBoomerangItems.SHROOMERANG));
        shapeless(output, TEBoomerangItems.ENCHANTED_BOOMERANG.toStack(), Ingredient.of(TEBoomerangItems.WOOD_BOOMERANG),Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(output, BaitItems.ENCHANTED_NIGHTCRAWLER.toStack(), Ingredient.of(BaitItems.WORM),Ingredient.of(MaterialItems.FALLING_STAR));



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
        shapeless(output, MaterialItems.LEAD_NUGGET.toStack(9));

        // 石头及深板岩压力板
        shaped(output, ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.STONE)), List.of("##")), new ItemStack(FunctionalBlocks.STONE_PRESSURE_PLATE));
        shaped(output, ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.DEEPSLATE)), List.of("##")), new ItemStack(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE));

        shapeless(output, ToolItems.NPC_INVITATION.toStack(), Ingredient.of(Items.PAPER), Ingredient.of(Items.HONEYCOMB, MaterialItems.ROYAL_WAX));
        // 钱币
        shapeless(output, ModItems.COPPER_COIN.toStack(100), Ingredient.of(ModItems.SILVER_COIN));
        shapeless(output, ModItems.SILVER_COIN.toStack(100), Ingredient.of(ModItems.GOLDEN_COIN));
        shapeless(output, ModItems.GOLDEN_COIN.toStack(100), Ingredient.of(ModItems.PLATINUM_COIN));

        shapeless(output, MaterialItems.RAW_ASPHALT.toStack(), AmountIngredient.of(2, ItemTags.STONE_CRAFTING_MATERIALS), Ingredient.of(MaterialItems.GEL));
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
        ResourceLocation id1 = Confluence.asResource(getItemName(decompressed));
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(compressedTag));
        recipeOutput.accept(id1, new ShapelessRecipe("", CraftingBookCategory.BUILDING, new ItemStack(decompressed, 9), ingredients), createAdvancementHolder(recipeOutput, id1, ingredients));
        ResourceLocation id2 = Confluence.asResource(getItemName(compressed));
        ShapedRecipePattern pattern = ShapedRecipePattern.of(Map.of('A', Ingredient.of(decompressedTag)), List.of("AAA", "AAA", "AAA"));
        recipeOutput.accept(id2, new ShapedRecipe("", CraftingBookCategory.BUILDING, pattern, compressed.asItem().getDefaultInstance()), createAdvancementHolder(recipeOutput, id2, pattern.ingredients()));
    }
}
