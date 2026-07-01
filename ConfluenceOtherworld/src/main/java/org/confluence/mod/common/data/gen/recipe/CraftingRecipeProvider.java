package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.UnitFinishedRecipe;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.special.BoomBunnyRecipe;
import org.confluence.mod.common.recipe.special.DragonPepperExtractingRecipe;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.confluence.mod.common.data.gen.recipe.ModRecipeProvider.createAdvancementBuilder;

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
    // 剪刀
    private final List<String> SHEAR_PATTERN = List.of(" #", "# ");
    // 鱼竿
    private final List<String> FISHING_POLE_PATTERN = List.of("  /", " /S", "/ S");

    // 基础砖
    private final List<String> BRICKS_PATTERN = List.of("##", "##");

    // 锁链
    private final List<String> CHAINS_PATTERN = List.of("#", "#", "#");

    // 纯净玻璃
    private final List<String> PURE_GLASS_PATTERN = List.of("###", "#/#", "###");

    // 基础桌
    private final List<String> TABLES_PATTERN = List.of("###", " # ");

    // 箱子
    private final List<String> CHEST_PATTERN = List.of("###", "# #", "###");

    // 片
    private final List<String> LAYER_PATTERN = List.of("###");

    public CraftingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        // 注册矿物块的合成与分解配方
        compressAndDecompressNine(writer, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN, OreBlocks.TIN_BLOCK, ModTags.Items.STORAGE_BLOCKS_TIN);
        compressAndDecompressNine(writer, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD, OreBlocks.LEAD_BLOCK, ModTags.Items.STORAGE_BLOCKS_LEAD);
        compressAndDecompressNine(writer, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER, OreBlocks.SILVER_BLOCK, ModTags.Items.STORAGE_BLOCKS_SILVER);
        compressAndDecompressNine(writer, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN, OreBlocks.TUNGSTEN_BLOCK, ModTags.Items.STORAGE_BLOCKS_TUNGSTEN);
        compressAndDecompressNine(writer, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM, OreBlocks.PLATINUM_BLOCK, ModTags.Items.STORAGE_BLOCKS_PLATINUM);
        compressAndDecompressNine(writer, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE, OreBlocks.METEORITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_METEORITE);
        compressAndDecompressNine(writer, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE, OreBlocks.DEMONITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_DEMONITE);
        compressAndDecompressNine(writer, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMTANE, OreBlocks.CRIMTANE_BLOCK, ModTags.Items.STORAGE_BLOCKS_CRIMTANE);
        compressAndDecompressNine(writer, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE, OreBlocks.HELLSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_HELLSTONE);

        compressAndDecompressNine(writer, MaterialItems.RUBY, ModTags.Items.GEMS_RUBY, DecorativeBlocks.RUBY_BLOCK, ModTags.Items.STORAGE_BLOCKS_RUBY);
        compressAndDecompressNine(writer, MaterialItems.AMBER, ModTags.Items.GEMS_AMBER, DecorativeBlocks.AMBER_BLOCK, ModTags.Items.STORAGE_BLOCKS_AMBER);
        compressAndDecompressNine(writer, MaterialItems.TOPAZ, ModTags.Items.GEMS_TOPAZ, DecorativeBlocks.TOPAZ_BLOCK, ModTags.Items.STORAGE_BLOCKS_TOPAZ);
        compressAndDecompressNine(writer, MaterialItems.JADE, ModTags.Items.GEMS_JADE, DecorativeBlocks.JADE_BLOCK, ModTags.Items.STORAGE_BLOCKS_JADE);
        compressAndDecompressNine(writer, MaterialItems.SAPPHIRE, ModTags.Items.GEMS_SAPPHIRE, DecorativeBlocks.SAPPHIRE_BLOCK, ModTags.Items.STORAGE_BLOCKS_SAPPHIRE);
        compressAndDecompressNine(writer, MaterialItems.AMETHYST, ModTags.Items.GEMS_AMETHYST, DecorativeBlocks.AMETHYST_BLOCK, ModTags.Items.STORAGE_BLOCKS_AMETHYST);

        compressAndDecompressNine(writer, MaterialItems.STURDY_FOSSIL, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL, OreBlocks.STURDY_FOSSIL_BLOCK, ModTags.Items.STORAGE_BLOCKS_STURDY_FOSSIL);
        compressAndDecompressNine(writer, MaterialItems.OPAL, ModTags.Items.RAW_MATERIALS_OPAL, OreBlocks.OPAL_BLOCK, ModTags.Items.STORAGE_BLOCKS_OPAL);
        compressAndDecompressNine(writer, MaterialItems.GELSTONE, ModTags.Items.RAW_MATERIALS_GELSTONE, OreBlocks.GELSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_GELSTONE);
        compressAndDecompressNine(writer, MaterialItems.COLD_CRYSTAL, ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL, OreBlocks.COLD_CRYSTAL_BLOCK, ModTags.Items.STORAGE_BLOCKS_COLD_CRYSTAL);
        // 粗矿
        compressAndDecompressNine(writer, MaterialItems.RAW_TIN, ModTags.Items.RAW_MATERIALS_TIN, OreBlocks.RAW_TIN_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_TIN);
        compressAndDecompressNine(writer, MaterialItems.RAW_LEAD, ModTags.Items.RAW_MATERIALS_LEAD, OreBlocks.RAW_LEAD_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_LEAD);
        compressAndDecompressNine(writer, MaterialItems.RAW_SILVER, ModTags.Items.RAW_MATERIALS_SILVER, OreBlocks.RAW_SILVER_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_SILVER);
        compressAndDecompressNine(writer, MaterialItems.RAW_TUNGSTEN, ModTags.Items.RAW_MATERIALS_TUNGSTEN, OreBlocks.RAW_TUNGSTEN_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_TUNGSTEN);
        compressAndDecompressNine(writer, MaterialItems.RAW_PLATINUM, ModTags.Items.RAW_MATERIALS_PLATINUM, OreBlocks.RAW_PLATINUM_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_PLATINUM);
        compressAndDecompressNine(writer, MaterialItems.RAW_METEORITE, ModTags.Items.RAW_MATERIALS_METEORITE, OreBlocks.RAW_METEORITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_METEORITE);
        compressAndDecompressNine(writer, MaterialItems.RAW_DEMONITE, ModTags.Items.RAW_MATERIALS_DEMONITE, OreBlocks.RAW_DEMONITE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_DEMONITE);
        compressAndDecompressNine(writer, MaterialItems.RAW_CRIMTANE, ModTags.Items.RAW_MATERIALS_CRIMTANE, OreBlocks.RAW_CRIMTANE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_CRIMTANE);
        compressAndDecompressNine(writer, MaterialItems.RAW_HELLSTONE, ModTags.Items.RAW_MATERIALS_HELLSTONE, OreBlocks.RAW_HELLSTONE_BLOCK, ModTags.Items.STORAGE_BLOCKS_RAW_HELLSTONE);
        // 飘飘麦捆
        compressAndDecompressNine(writer, MaterialItems.FLOATING_WHEAT_HEADS, ModTags.Items.RAW_MATERIALS_FLOATING_WHEAT, DecorativeBlocks.FLOATING_WHEAT_BALE, ModTags.Items.STORAGE_BLOCKS_FLOATING_WHEAT_BALE);
        // 矿物粒
        compressAndDecompressNine(writer, MaterialItems.TIN_NUGGET, ModTags.Items.NUGGETS_TIN, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN);
        compressAndDecompressNine(writer, MaterialItems.LEAD_NUGGET, ModTags.Items.NUGGETS_LEAD, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD);
        compressAndDecompressNine(writer, MaterialItems.SILVER_NUGGET, ModTags.Items.NUGGETS_SILVER, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER);
        compressAndDecompressNine(writer, MaterialItems.TUNGSTEN_NUGGET, ModTags.Items.NUGGETS_TUNGSTEN, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN);
        compressAndDecompressNine(writer, MaterialItems.PLATINUM_NUGGET, ModTags.Items.NUGGETS_PLATINUM, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM);
        compressAndDecompressNine(writer, MaterialItems.METEORITE_NUGGET, ModTags.Items.NUGGETS_METEORITE, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE);
        compressAndDecompressNine(writer, MaterialItems.DEMONITE_NUGGET, ModTags.Items.NUGGETS_DEMONITE, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE);
        compressAndDecompressNine(writer, MaterialItems.CRIMTANE_NUGGET, ModTags.Items.NUGGETS_CRIMTANE, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMTANE);
        compressAndDecompressNine(writer, MaterialItems.HELLSTONE_NUGGET, ModTags.Items.NUGGETS_HELLSTONE, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE);

        // 铅砧
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(ModTags.Items.STORAGE_BLOCKS_LEAD),
                'i', Ingredient.of(ModTags.Items.INGOTS_LEAD)
        ), List.of(
                "III",
                " i ",
                "iii"
        )), FunctionalBlocks.LEAD_ANVIL.toStack());
        // 蛛网
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                "/ /",
                " / ",
                "/ /"
        )), Items.COBWEB.getDefaultInstance());
        //
        shaped(writer, "", "_from_lead_and_iron", PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.REDSTONE_TORCH),
                'S', Ingredient.of(Items.STICK),
                'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "XSX",
                "X#X",
                "XSX"
        )), new ItemStack(Items.ACTIVATOR_RAIL, 6));
        // 广播盒
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.SIGNS),
                'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                'R', Ingredient.of(Items.REDSTONE)
        ), List.of(
                "RIR",
                "I#I",
                "RIR"
        )), FunctionalBlocks.ANNOUNCEMENT_BOX_ITEM.toStack());
        // 蜂蜜瓶
        shapeless(writer, "", "_from_glass_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE)
        );
        shapeless(writer, "", "_from_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );
        // 玻璃瓶
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.PURE_GLASS)
        ), List.of(
                "# #",
                " # "
        )), PotionItems.BOTTLE.toStack(3));
        shapeless(writer, "", "_from_bottle",
                new ItemStack(Items.GLASS_BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );
        shapeless(writer, "", "_from_glass_bottle",
                new ItemStack(PotionItems.BOTTLE.get()),
                Ingredient.of(Items.GLASS_BOTTLE)
        );
        // 冰镇西瓜片 冰西瓜互换
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(FoodItems.ICE_MELON_SLICE)
        ), List.of(
                "III",
                "III",
                "III"
        )), DecorativeBlocks.ICE_MELON.toStack());
        shapeless(writer, new ItemStack(FoodItems.ICE_MELON_SLICE.get(), 9), Ingredient.of(DecorativeBlocks.ICE_MELON));
        // 金西瓜片 金西瓜互换
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(Items.GLISTERING_MELON_SLICE)
        ), List.of(
                "III",
                "III",
                "III"
        )), DecorativeBlocks.GOLDEN_MELON.toStack());
        shapeless(writer, new ItemStack(Items.GLISTERING_MELON_SLICE, 9), Ingredient.of(DecorativeBlocks.GOLDEN_MELON));
        shapeless(writer, new ItemStack(FoodItems.COLDBLOOD_PUMPKIN_PIE.get()), Ingredient.of(Items.EGG), Ingredient.of(Items.SUGAR), Ingredient.of(NatureBlocks.WHITE_PUMPKIN));
        shapeless(writer, new ItemStack(FoodItems.WHITE_PUMPKIN_SEED.get(), 4), Ingredient.of(NatureBlocks.WHITE_PUMPKIN));
        shapeless(writer, new ItemStack(DecorativeBlocks.JOHNNY_O_LANTERN.get(), 4), Ingredient.of(NatureBlocks.WHITE_PUMPKIN), Ingredient.of(Items.TORCH));
        // 各种片
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.SAND)
        ), LAYER_PATTERN), NatureBlocks.SAND_LAYER_BLOCK.toStack(6));

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.RED_SAND)
        ), LAYER_PATTERN), NatureBlocks.RED_SAND_LAYER_BLOCK.toStack(6));

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.EBONSAND)
        ), LAYER_PATTERN), NatureBlocks.EBONSAND_LAYER_BLOCK.toStack(6));

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.CRIMSAND)
        ), LAYER_PATTERN), NatureBlocks.CRIMSAND_LAYER_BLOCK.toStack(6));

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARLSAND)
        ), LAYER_PATTERN), NatureBlocks.PEARLSAND_LAYER_BLOCK.toStack(6));

        // 砂岩箱
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Blocks.SANDSTONE)
        ), CHEST_PATTERN), ChestBlocks.SANDSTONE_CHEST.toStack());
        // 大理石箱
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.MARBLE)
        ), CHEST_PATTERN), ChestBlocks.MARBLE_CHEST.toStack());
        // 花岗岩箱
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.GRANITE)
        ), CHEST_PATTERN), ChestBlocks.GRANITE_CHEST.toStack());

        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            registerWoodRecipes(writer, blockSet);
        }

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '|', Ingredient.of(Items.CHAIN),
                '#', Ingredient.of(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK)
        ), List.of(
                "| |",
                "###",
                "###"
        )), NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.HANGING_SIGN.toStack());

        // 木椅
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.OAK_PLANKS)
        ), List.of(
                "#  ",
                "#  ",
                "## "
        )), TFBlocks.OAK_SET.CHAIR.toStack());

        // 桌
        registeTableRecipes(writer, Ingredient.of(Items.OAK_PLANKS), TFBlocks.OAK_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.ACACIA_PLANKS), TFBlocks.ACACIA_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.BAMBOO_PLANKS), TFBlocks.BAMBOO_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.BIRCH_PLANKS), TFBlocks.BIRCH_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.CHERRY_PLANKS), TFBlocks.CHERRY_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.CRIMSON_PLANKS), TFBlocks.CRIMSON_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.DARK_OAK_PLANKS), TFBlocks.DARK_OAK_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.JUNGLE_PLANKS), TFBlocks.JUNGLE_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.MANGROVE_PLANKS), TFBlocks.MANGROVE_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.SPRUCE_PLANKS), TFBlocks.SPRUCE_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.WARPED_PLANKS), TFBlocks.WARPED_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.PLANKS), TFBlocks.PINE_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.BAOBAB_LOG_BLOCKS.PLANKS), TFBlocks.BAOBAB_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), TFBlocks.ASH_WOOD_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), TFBlocks.SHADEWOOD_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), TFBlocks.EBONWOOD_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), TFBlocks.PEARLWOOD_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.SPOOKY_LOG_BLOCKS.PLANKS), TFBlocks.SPOOKY_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.FEY_LOG_BLOCKS.PLANKS), TFBlocks.FEYWOOD_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.PLANKS), TFBlocks.DYNASTY_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.PLANKS), TFBlocks.MUSHROOM_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.POLISHED_BLACKSTONE), TFBlocks.POLISHED_BLACKSTONE_SET.TABLE, 1);
        registeTableRecipes(writer, Ingredient.of(Items.STONE), TFBlocks.STONE_SET.TABLE, 1);


        // 船
        registerBoatRecipes(writer, NatureBlocks.EBONY_LOG_BLOCKS, BoatItems.EBONY_BOAT, BoatItems.EBONY_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.PEARL_LOG_BLOCKS, BoatItems.PEARL_BOAT, BoatItems.PEARL_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.SHADOW_LOG_BLOCKS, BoatItems.SHADOW_BOAT, BoatItems.SHADOW_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.PALM_LOG_BLOCKS, BoatItems.PALM_BOAT, BoatItems.PALM_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.BAOBAB_LOG_BLOCKS, BoatItems.BAOBAB_BOAT, BoatItems.BAOBAB_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS, BoatItems.GLOWING_MUSHROOM_BOAT, BoatItems.GLOWING_MUSHROOM_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.YELLOW_WILLOW_LOG_BLOCKS, BoatItems.YELLOW_WILLOW_BOAT, BoatItems.YELLOW_WILLOW_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.LIVING_LOG_BLOCKS, BoatItems.LIVING_BOAT, BoatItems.LIVING_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS, BoatItems.LIVING_MAHOGANY_BOAT, BoatItems.LIVING_MAHOGANY_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.ASH_LOG_BLOCKS, BoatItems.ASH_BOAT, BoatItems.ASH_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.SPOOKY_LOG_BLOCKS, BoatItems.SPOOKY_BOAT, BoatItems.SPOOKY_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.DYNASTY_LOG_BLOCKS, BoatItems.DYNASTY_BOAT, BoatItems.DYNASTY_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.PINE_LOG_BLOCKS, BoatItems.PINE_BOAT, BoatItems.PINE_CHEST_BOAT);
        registerBoatRecipes(writer, NatureBlocks.FEY_LOG_BLOCKS, BoatItems.FEY_BOAT, BoatItems.FEY_CHEST_BOAT);

        // 基础盔甲
        registerArmorRecipes(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), ArmorItems.ASH_HELMET, ArmorItems.ASH_CHESTPLATE, ArmorItems.ASH_LEGGINGS, ArmorItems.ASH_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), ArmorItems.EBONY_HELMET, ArmorItems.EBONY_CHESTPLATE, ArmorItems.EBONY_LEGGINGS, ArmorItems.EBONY_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), ArmorItems.SHADOW_PLANK_HELMET, ArmorItems.SHADOW_PLANK_CHESTPLATE, ArmorItems.SHADOW_PLANK_LEGGINGS, ArmorItems.SHADOW_PLANK_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), ArmorItems.PEARL_HELMET, ArmorItems.PEARL_CHESTPLATE, ArmorItems.PEARL_LEGGINGS, ArmorItems.PEARL_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INITIAL_WOOD), ArmorItems.PLANK_HELMET, ArmorItems.PLANK_CHESTPLATE, ArmorItems.PLANK_LEGGINGS, ArmorItems.PLANK_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(Items.CACTUS), ArmorItems.CACTUS_HELMET, ArmorItems.CACTUS_CHESTPLATE, ArmorItems.CACTUS_LEGGINGS, ArmorItems.CACTUS_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(Tags.Items.INGOTS_COPPER), ArmorItems.COPPER_HELMET, ArmorItems.COPPER_CHESTPLATE, ArmorItems.COPPER_LEGGINGS, ArmorItems.COPPER_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TIN), ArmorItems.TIN_HELMET, ArmorItems.TIN_CHESTPLATE, ArmorItems.TIN_LEGGINGS, ArmorItems.TIN_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_LEAD), ArmorItems.LEAD_HELMET, ArmorItems.LEAD_CHESTPLATE, ArmorItems.LEAD_LEGGINGS, ArmorItems.LEAD_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_SILVER), ArmorItems.SILVER_HELMET, ArmorItems.SILVER_CHESTPLATE, ArmorItems.SILVER_LEGGINGS, ArmorItems.SILVER_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), ArmorItems.TUNGSTEN_HELMET, ArmorItems.TUNGSTEN_CHESTPLATE, ArmorItems.TUNGSTEN_LEGGINGS, ArmorItems.TUNGSTEN_BOOTS);
        registerArmorRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), ArmorItems.PLATINUM_HELMET, ArmorItems.PLATINUM_CHESTPLATE, ArmorItems.PLATINUM_LEGGINGS, ArmorItems.PLATINUM_BOOTS);
        // 基础弓
        registerBowRecipes(writer, Ingredient.of(Tags.Items.INGOTS_COPPER), BowItems.COPPER_BOW, BowItems.COPPER_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(Tags.Items.INGOTS_GOLD), BowItems.GOLDEN_BOW, BowItems.GOLDEN_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(Tags.Items.INGOTS_IRON), BowItems.IRON_BOW, BowItems.IRON_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TIN), BowItems.TIN_BOW, BowItems.TIN_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_LEAD), BowItems.LEAD_BOW, BowItems.LEAD_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_SILVER), BowItems.SILVER_BOW, BowItems.SILVER_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), BowItems.TUNGSTEN_BOW, BowItems.TUNGSTEN_SHORT_BOW);
        registerBowRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), BowItems.PLATINUM_BOW, BowItems.PLATINUM_SHORT_BOW);
        registerShortBowRecipes(writer, Ingredient.of(Items.STICK), Ingredient.of(Items.STRING), BowItems.WOODEN_SHORT_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), Ingredient.of(Items.BOW), BowItems.EBONWOOD_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), Ingredient.of(BowItems.WOODEN_SHORT_BOW), BowItems.EBONWOOD_SHORT_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), Ingredient.of(Items.BOW), BowItems.SHADEWOOD_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), Ingredient.of(BowItems.WOODEN_SHORT_BOW), BowItems.SHADEWOOD_SHORT_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), Ingredient.of(Items.BOW), BowItems.ASH_WOOD_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), Ingredient.of(BowItems.WOODEN_SHORT_BOW), BowItems.ASH_WOOD_SHORT_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), Ingredient.of(Items.BOW), BowItems.PEARLWOOD_BOW);
        registerShortBowRecipes(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), Ingredient.of(BowItems.WOODEN_SHORT_BOW), BowItems.PEARLWOOD_SHORT_BOW);

        // 基础工具，阔剑短剑
        registerToolRecipes(writer, Ingredient.of(Tags.Items.INGOTS_COPPER), AxeItems.COPPER_AXE, PickaxeItems.COPPER_PICKAXE, HammerItems.COPPER_HAMMER, SwordItems.COPPER_BROADSWORD, SwordItems.COPPER_SHORT_SWORD, ShovelItems.COPPER_SHOVEL, HoeItems.COPPER_HOE);
        registerToolRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TIN), AxeItems.TIN_AXE, PickaxeItems.TIN_PICKAXE, HammerItems.TIN_HAMMER, SwordItems.TIN_BROADSWORD, SwordItems.TIN_SHORT_SWORD, ShovelItems.TIN_SHOVEL, HoeItems.TIN_HOE);
        registerToolRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_LEAD), AxeItems.LEAD_AXE, PickaxeItems.LEAD_PICKAXE, HammerItems.LEAD_HAMMER, SwordItems.LEAD_BROADSWORD, SwordItems.LEAD_SHORT_SWORD, ShovelItems.LEAD_SHOVEL, HoeItems.LEAD_HOE);
        registerToolRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_SILVER), AxeItems.SILVER_AXE, PickaxeItems.SILVER_PICKAXE, HammerItems.SILVER_HAMMER, SwordItems.SILVER_BROADSWORD, SwordItems.SILVER_SHORT_SWORD, ShovelItems.SILVER_SHOVEL, HoeItems.SILVER_HOE);
        registerToolRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN), AxeItems.TUNGSTEN_AXE, PickaxeItems.TUNGSTEN_PICKAXE, HammerItems.TUNGSTEN_HAMMER, SwordItems.TUNGSTEN_BROADSWORD, SwordItems.TUNGSTEN_SHORT_SWORD, ShovelItems.TUNGSTEN_SHOVEL, HoeItems.TUNGSTEN_HOE);
        registerToolRecipes(writer, Ingredient.of(ModTags.Items.INGOTS_PLATINUM), AxeItems.PLATINUM_AXE, PickaxeItems.PLATINUM_PICKAXE, HammerItems.PLATINUM_HAMMER, SwordItems.PLATINUM_BROADSWORD, SwordItems.PLATINUM_SHORT_SWORD, ShovelItems.PLATINUM_SHOVEL, HoeItems.PLATINUM_HOE);
        registerShortSwordRecipe(writer, Ingredient.of(Tags.Items.INGOTS_IRON), SwordItems.IRON_SHORT_SWORD);
        registerShortSwordRecipe(writer, Ingredient.of(Tags.Items.INGOTS_GOLD), SwordItems.GOLDEN_SHORT_SWORD);
        registerHammerRecipe(writer, Ingredient.of(ModTags.Items.INITIAL_WOOD), HammerItems.WOODEN_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), HammerItems.EBONWOOD_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), HammerItems.SHADEWOOD_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), HammerItems.ASH_WOOD_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), HammerItems.PEARLWOOD_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(Tags.Items.INGOTS_GOLD), HammerItems.GOLDEN_HAMMER);
        registerHammerRecipe(writer, Ingredient.of(Tags.Items.INGOTS_IRON), HammerItems.IRON_HAMMER);
        registerBroadswordRecipe(writer, Ingredient.of(Items.CACTUS), SwordItems.CACTUS_SWORD);
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.CACTUS),
                '/', Ingredient.of(Items.STICK)
        ), PICKAXE_PATTERN), PickaxeItems.CACTUS_PICKAXE.toStack());
        registerBroadswordRecipe(writer, Ingredient.of(NatureBlocks.EBONY_LOG_BLOCKS.PLANKS), SwordItems.EBONWOOD_SWORD);
        registerBroadswordRecipe(writer, Ingredient.of(NatureBlocks.SHADOW_LOG_BLOCKS.PLANKS), SwordItems.SHADEWOOD_SWORD);
        registerBroadswordRecipe(writer, Ingredient.of(NatureBlocks.ASH_LOG_BLOCKS.PLANKS), SwordItems.ASH_WOOD_SWORD);
        registerBroadswordRecipe(writer, Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS), SwordItems.PEARLWOOD_SWORD);
        // 鱼竿
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "/ S",
                " /S",
                "  /"
        )), FishingPoleItems.WOOD_FISHING_POLE.toStack());
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), FISHING_POLE_PATTERN), FishingPoleItems.REINFORCED_FISHING_POLE.toStack());
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.INGOTS_DEMONITE)
        ), FISHING_POLE_PATTERN), FishingPoleItems.FISHER_OF_SOULS.toStack());
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(Items.STRING),
                '/', Ingredient.of(ModTags.Items.INGOTS_CRIMTANE)
        ), FISHING_POLE_PATTERN), FishingPoleItems.FLESHCATCHER.toStack());
        // 木悠悠球
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(ItemTags.LOGS),
                '/', Ingredient.of(Items.COBWEB)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), YoyoItems.WOODEN_YOYO.toStack());
        // 园艺剪
        registerShearsRecipe(writer, Ingredient.of(ModTags.Items.INGOTS_COBALT), GardenShearsItems.COBALT_GARDEN_SHEARS);
        registerShearsRecipe(writer, Ingredient.of(ModTags.Items.INGOTS_PALLADIUM), GardenShearsItems.PALLADIUM_GARDEN_SHEARS);
        // 箭
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS),
                'O', Ingredient.of(ItemTags.LOGS),
                'a', Ingredient.of(ItemTags.WOOL)
        ), List.of(
                "#",
                "O",
                "a"
        )), new ItemStack(Items.ARROW, 12));
        // 王朝木衍生系列
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.PLANKS),
                '/', Ingredient.of(Items.PAPER)
        ), List.of(
                " S ",
                "S/S",
                " S "
        )), DecorativeBlocks.WHITE_PAPER_PANE.toStack());
        shapeless(writer,
                DecorativeBlocks.WHITE_PAPER_PANE_LAMP.toStack(),
                Ingredient.of(DecorativeBlocks.WHITE_PAPER_PANE),
                Ingredient.of(Items.TORCH)
        );
        shapeless(writer,
                DecorativeBlocks.MALACHITE_PAPER_PANE.toStack(),
                Ingredient.of(DecorativeBlocks.WHITE_PAPER_PANE),
                Ingredient.of(Items.CYAN_DYE)
        );
        shapeless(writer,
                DecorativeBlocks.MALACHITE_PAPER_PANE_LAMP.toStack(),
                Ingredient.of(DecorativeBlocks.MALACHITE_PAPER_PANE),
                Ingredient.of(Items.TORCH)
        );
        shapeless(writer,
                DecorativeBlocks.TRADITIONAL_DYNASTY_DOOR.toStack(),
                Ingredient.of(NatureBlocks.DYNASTY_LOG_BLOCKS.DOOR),
                Ingredient.of(Items.RED_DYE),
                Ingredient.of(Items.BLACK_DYE)
        );
        // 松针手工绳套
        shapeless(writer,
                ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET.toStack(),
                Ingredient.of(NatureBlocks.PINE_DROOPING_VINE),
                Ingredient.of(NatureBlocks.PINE_DROOPING_VINE)
        );
        shapeless(writer,
                DecorativeBlocks.CHRISTMAS_PINE_TRAPDOOR.toStack(),
                Ingredient.of(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET),
                Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.TRAPDOOR)
        );
        shapeless(writer,
                DecorativeBlocks.CHRISTMAS_PINE_DOOR.toStack(),
                Ingredient.of(ModBlocks.PINE_NEEDLE_HANDMADE_ROPE_SET),
                Ingredient.of(NatureBlocks.PINE_LOG_BLOCKS.DOOR)
        );
        // 生命篝火
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                'S', Ingredient.of(NatureBlocks.LIVING_LOG_BLOCKS.WOOD, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.WOOD, MaterialItems.LIFE_MUSHROOM),
                '/', Ingredient.of(Items.CAMPFIRE)
        ), List.of(
                " S ",
                "S/S"
        )), FunctionalBlocks.LIFE_CAMPFIRE.toStack());
        // 香蒲造纸
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(ModItems.CATTAIL, ModItems.JUNGLE_CATTAIL, ModItems.GLOWING_MUSHROOM_CATTAIL, ModItems.EBONY_CATTAIL, ModItems.CRIMSON_CATTAIL, ModItems.HALLOW_CATTAIL)
        ), List.of(
                "///"
        )), new ItemStack(Items.PAPER, 3));

        // 地毯
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.RAINBOW_WOOL)
        ), List.of(
                "##"
        )), DecorativeBlocks.RAINBOW_CARPET.toStack(3));
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.FLINX_FUR_BLOCK)
        ), List.of(
                "##"
        )), DecorativeBlocks.FLINX_FUR_CARPET.toStack(3));
        // 便捷合成，无序合成
        shapeless(writer, "", "_from_raw_copper",
                new ItemStack(Items.COPPER_INGOT),
                Ingredient.of(Items.RAW_COPPER),
                Ingredient.of(Items.RAW_COPPER),
                Ingredient.of(Items.RAW_COPPER)
        );
        shapeless(writer, "", "_from_raw_tin",
                new ItemStack(MaterialItems.TIN_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_TIN),
                Ingredient.of(MaterialItems.RAW_TIN),
                Ingredient.of(MaterialItems.RAW_TIN)
        );
        shapeless(writer, "", "_from_raw_copper",
                new ItemStack(Items.IRON_INGOT),
                Ingredient.of(Items.RAW_IRON),
                Ingredient.of(Items.RAW_IRON),
                Ingredient.of(Items.RAW_IRON)
        );
        shapeless(writer, "", "_from_raw_lead",
                new ItemStack(MaterialItems.LEAD_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD),
                Ingredient.of(MaterialItems.RAW_LEAD)
        );
        shapeless(writer, "", "_from_raw_silver",
                new ItemStack(MaterialItems.SILVER_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER),
                Ingredient.of(MaterialItems.RAW_SILVER)
        );
        shapeless(writer, "", "_from_raw_tungsten",
                new ItemStack(MaterialItems.TUNGSTEN_INGOT.get()),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN),
                Ingredient.of(MaterialItems.RAW_TUNGSTEN)
        );

        shapeless(writer, ToolItems.VINE_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE), Ingredient.of(ModBlocks.VINE_ROPE));
        shapeless(writer, ToolItems.WEB_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE), Ingredient.of(ModBlocks.WEB_ROPE));
        shapeless(writer, ToolItems.SILK_ROPE_COIL.toStack(), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE), Ingredient.of(ModBlocks.SILK_ROPE));
        shapeless(writer, ToolItems.ROPE_COIL.toStack(), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE), Ingredient.of(ModBlocks.ROPE));

        shapeless(writer, ArmorItems.GOLDEN_HELMET.toStack(), Ingredient.of(Items.GOLDEN_HELMET), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, ArmorItems.GOLDEN_CHESTPLATE.toStack(), Ingredient.of(Items.GOLDEN_CHESTPLATE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, ArmorItems.GOLDEN_LEGGINGS.toStack(), Ingredient.of(Items.GOLDEN_LEGGINGS), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, ArmorItems.GOLDEN_BOOTS.toStack(), Ingredient.of(Items.GOLDEN_BOOTS), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, SwordItems.GOLDEN_BROADSWORD.toStack(), Ingredient.of(Items.GOLDEN_SWORD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, AxeItems.GOLDEN_AXE.toStack(), Ingredient.of(Items.GOLDEN_AXE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, PickaxeItems.GOLDEN_PICKAXE.toStack(), Ingredient.of(Items.GOLDEN_PICKAXE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, HoeItems.GOLDEN_HOE.toStack(), Ingredient.of(Items.GOLDEN_HOE), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));
        shapeless(writer, ShovelItems.GOLDEN_SHOVEL.toStack(), Ingredient.of(Items.GOLDEN_SHOVEL), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD), Ingredient.of(Tags.Items.INGOTS_GOLD));

        shapeless(writer, VanityArmorItems.GUY_FAWKES_MASK_SET.toStack(), Ingredient.of(VanityArmorItems.GUY_FAWKES_HAT), Ingredient.of(VanityArmorItems.GUY_FAWKES_MASK));


        shapeless(writer, new ItemStack(Items.STRING, 2), Ingredient.of(ItemTags.WOOL));
        shapeless(writer, "", "_from_stony_log", new ItemStack(Items.COBBLESTONE, 2), Ingredient.of(NatureBlocks.STONY_LOG));
        shapeless(writer, "", "_from_gel", new ItemStack(Items.TORCH, 3), Ingredient.of(MaterialItems.GEL), Ingredient.of(Items.STICK));
        shapeless(writer, "", "_from_slime_ball", new ItemStack(Items.TORCH, 3), Ingredient.of(Items.SLIME_BALL), Ingredient.of(Items.STICK));

        shapeless(writer, ConsumableItems.MANA_CRYSTAL.toStack(), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(writer, MaterialItems.FALLING_STAR.toStack(), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS), Ingredient.of(MaterialItems.STAR_PETALS));
        shapeless(writer, FoodItems.CLOUD_DOUGH.toStack(), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS), Ingredient.of(MaterialItems.FLOATING_WHEAT_HEADS));
        shapeless(writer, DecorativeBlocks.FLINX_FUR_BLOCK.toStack(20), Ingredient.of(MaterialItems.FLINX_FUR));
        shapeless(writer, new ItemStack(NatureBlocks.GLOWING_MUSHROOM_LOG_BLOCKS.PLANKS, 4), Ingredient.of(NatureBlocks.GLOWING_MUSHROOM_STEM_BLOCK));

        //生鱼片
        shapeless(writer, "", "_from_partial_mouth_fish",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.PARTIAL_MOUTH_FISH),
                Ingredient.of(FoodItems.PARTIAL_MOUTH_FISH)
        );
        shapeless(writer, "", "_from_red_snapper",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.RED_SNAPPER),
                Ingredient.of(FoodItems.RED_SNAPPER)
        );
        shapeless(writer,
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.SALMON),
                Ingredient.of(FoodItems.SALMON)
        );
        shapeless(writer, "", "_from_salmon",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(Items.SALMON),
                Ingredient.of(Items.SALMON)
        );
        shapeless(writer, "", "_from_tuna",
                FoodItems.SASHIMI.toStack(),
                Ingredient.of(FoodItems.TUNA),
                Ingredient.of(FoodItems.TUNA)
        );
        // 机关箱用陷阱箱合成方式
        shapeless(writer, ChestBlocks.DEATH_GOLDEN_CHEST.toStack(), Ingredient.of(ChestBlocks.GOLDEN_CHEST), Ingredient.of(Items.TRIPWIRE_HOOK));
        shapeless(writer, ChestBlocks.DEATH_WOODEN_CHEST.toStack(), Ingredient.of(Items.TRAPPED_CHEST), Ingredient.of(Items.TRIPWIRE_HOOK));

        shapeless(writer, ConsumableItems.VILE_POWDER.toStack(5), Ingredient.of(NatureBlocks.VILE_MUSHROOM));
        shapeless(writer, ConsumableItems.VICIOUS_POWDER.toStack(5), Ingredient.of(NatureBlocks.VICIOUS_MUSHROOM));
        shapeless(writer, DecorativeBlocks.WOOD_STONE_SLATTED_BLOCKS.toStack(4), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS), Ingredient.of(ItemTags.PLANKS));
        shapeless(writer, ConsumableItems.BOUNCY_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(writer, ConsumableItems.BOUNCY_DYNAMITE.toStack(), Ingredient.of(ConsumableItems.DYNAMITE), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(writer, ConsumableItems.BOUNCY_GRENADE.toStack(), Ingredient.of(ConsumableItems.GRENADE), Ingredient.of(MaterialItems.PINK_GEL));
        shapeless(writer, ConsumableItems.STICKY_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.GEL));
        shapeless(writer, ConsumableItems.STICKY_DIRT_BOMB.toStack(), Ingredient.of(ConsumableItems.DIRT_BOMB), Ingredient.of(MaterialItems.GEL));
        shapeless(writer, ConsumableItems.STICKY_DYNAMITE.toStack(), Ingredient.of(ConsumableItems.DYNAMITE), Ingredient.of(MaterialItems.GEL));
        shapeless(writer, ConsumableItems.STICKY_GRENADE.toStack(), Ingredient.of(ConsumableItems.GRENADE), Ingredient.of(MaterialItems.GEL));
        shapeless(writer, ConsumableItems.SCARAB_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        shapeless(writer, BoomerangItems.TRIMARANG.toStack(), Ingredient.of(BoomerangItems.ENCHANTED_BOOMERANG), Ingredient.of(BoomerangItems.ICE_BOOMERANG), Ingredient.of(BoomerangItems.SHROOMERANG));
        shapeless(writer, BoomerangItems.ENCHANTED_BOOMERANG.toStack(), Ingredient.of(BoomerangItems.WOOD_BOOMERANG), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(writer, BaitItems.ENCHANTED_NIGHTCRAWLER.toStack(), Ingredient.of(BaitItems.WORM), Ingredient.of(MaterialItems.FALLING_STAR));
        // 宝石树苗
        shapeless(writer, NatureBlocks.RUBY_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_RUBY));
        shapeless(writer, NatureBlocks.AMBER_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_AMBER));
        shapeless(writer, NatureBlocks.TOPAZ_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_TOPAZ));
        shapeless(writer, NatureBlocks.JADE_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_JADE));
        shapeless(writer, NatureBlocks.SAPPHIRE_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_SAPPHIRE));
        shapeless(writer, NatureBlocks.DIAMOND_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(Tags.Items.GEMS_DIAMOND));
        shapeless(writer, NatureBlocks.AMETHYST_SAPLING.toStack(), Ingredient.of(Items.OAK_SAPLING), Ingredient.of(ModTags.Items.GEMS_AMETHYST));

        // 基础石砖
        registerBricksRecipes(writer, Ingredient.of(NatureBlocks.EBONSTONE), DecorativeBlocks.EBONSTONE_BRICKS.FULL, 4);
        registerBricksRecipes(writer, Ingredient.of(NatureBlocks.CRIMSTONE), DecorativeBlocks.CRIMSTONE_BRICKS.FULL, 4);
        registerBricksRecipes(writer, Ingredient.of(NatureBlocks.PEARLSTONE), DecorativeBlocks.PEARLSTONE_BRICKS.FULL, 4);
        registerBricksRecipes(writer, Ingredient.of(NatureBlocks.MARBLE), DecorativeBlocks.POLISHED_MARBLE, 4);
        registerBricksRecipes(writer, Ingredient.of(NatureBlocks.GRANITE), DecorativeBlocks.POLISHED_GRANITE, 4);
        // 锁链
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_AMBER), DecorativeBlocks.AMBER_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(Tags.Items.GEMS_DIAMOND), DecorativeBlocks.DIAMOND_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_SAPPHIRE), DecorativeBlocks.SAPPHIRE_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_AMETHYST), DecorativeBlocks.AMETHYST_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_JADE), DecorativeBlocks.JADE_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_RUBY), DecorativeBlocks.RUBY_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ModTags.Items.GEMS_TOPAZ), DecorativeBlocks.TOPAZ_CHAIN);
        registerChainsRecipes(writer, Ingredient.of(ConsumableItems.DUNGEON_DEMON_BONE), DecorativeBlocks.BONE_CHAIN);
        // 纯净玻璃
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BLACK_DYE), DecorativeBlocks.BLACK_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BLUE_DYE), DecorativeBlocks.BLUE_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.BROWN_DYE), DecorativeBlocks.BROWN_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.CYAN_DYE), DecorativeBlocks.CYAN_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.GRAY_DYE), DecorativeBlocks.GRAY_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.GREEN_DYE), DecorativeBlocks.GREEN_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIGHT_BLUE_DYE), DecorativeBlocks.LIGHT_BLUE_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIGHT_GRAY_DYE), DecorativeBlocks.LIGHT_GRAY_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.LIME_DYE), DecorativeBlocks.LIME_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.MAGENTA_DYE), DecorativeBlocks.MAGENTA_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.ORANGE_DYE), DecorativeBlocks.ORANGE_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.PINK_DYE), DecorativeBlocks.PINK_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.PURPLE_DYE), DecorativeBlocks.PURPLE_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.RED_DYE), DecorativeBlocks.RED_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.WHITE_DYE), DecorativeBlocks.WHITE_PURE_GLASS, 8);
        registerPureGlassRecipes(writer, Ingredient.of(DecorativeBlocks.PURE_GLASS), Ingredient.of(Items.YELLOW_DYE), DecorativeBlocks.YELLOW_PURE_GLASS, 8);
        // 拐杖糖块
        registerPureGlassRecipes(writer, Ingredient.of(Items.SUGAR), Ingredient.of(Items.RED_DYE), DecorativeBlocks.RED_CANDY_BLOCK, 8);
        registerPureGlassRecipes(writer, Ingredient.of(Items.SUGAR), Ingredient.of(Items.GREEN_DYE), DecorativeBlocks.GREEN_CANDY_BLOCK, 8);


        // 镶金方解石
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shapeless(writer, FoodItems.HONEY_MOONCAKES_CHUNKS.toStack(3), Ingredient.of(FoodItems.HONEY_MOONCAKES));
        // 蛋黄月饼
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.SUGAR),
                        'W', Ingredient.of(Items.WHEAT),
                        'E', Ingredient.of(Items.EGG)
                ), List.of(
                        "#E#",
                        "WEW"
                )),
                FoodItems.EGG_YOLK_MOONCAKES.toStack(2)
        );
        shapeless(writer, FoodItems.EGG_YOLK_MOONCAKES_CHUNKS.toStack(2), Ingredient.of(FoodItems.EGG_YOLK_MOONCAKES));
        // 蛛丝绳
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STRING)
                ), List.of(
                        "#",
                        "#",
                        "#"
                )),
                ModBlocks.WEB_ROPE.toStack(3)
        );
        // 蛛丝锁链
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "###",
                        " # ",
                        " # "
                )),
                FunctionalBlocks.WEATHER_VANE.toStack()
        );
        // 凝灰岩展台
        shaped(writer, "", "",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.TUFF)
                ), List.of(
                        "###",
                        " # ",
                        "###"
                )),
                FunctionalBlocks.TUFF_BOOTH.toStack(7)
        );
        // 瞬爆tnt
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
                        'G', Ingredient.of(ModTags.Items.GEMS_RUBY),
                        'g', Ingredient.of(Tags.Items.INGOTS_GOLD)
                ), List.of(
                        "gGg",
                        "ggg"
                )),
                VanityArmorItems.GOLD_CROWN.toStack()
        );
        // 铂金冠
        shaped(writer,
                PortShapedRecipePattern.of(Map.of(
                        'G', Ingredient.of(ModTags.Items.GEMS_RUBY),
                        'g', Ingredient.of(ModTags.Items.INGOTS_PLATINUM)
                ), List.of(
                        "gGg",
                        "ggg"
                )),
                VanityArmorItems.PLATINUM_CROWN.toStack()
        );
        // 铅铁共用相关
        shaped(writer, "", "_from_nuggets_lead",
                PortShapedRecipePattern.of(Map.of(
                        'S', Ingredient.of(ModTags.Items.NUGGETS_LEAD)
                ), List.of(
                        "SSS",
                        "SSS",
                        "SSS"
                )),
                MaterialItems.LEAD_INGOT.toStack()
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        " # "
                )),
                new ItemStack(Items.BUCKET)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        "# #",
                        "###"
                )),
                new ItemStack(Items.CAULDRON)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        'C', Ingredient.of(Items.CHEST),
                        'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "I I",
                        "ICI",
                        " I "
                )),
                new ItemStack(Items.HOPPER)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "# #",
                        "###"
                )),
                new ItemStack(Items.MINECART)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
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
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        '#', Ingredient.of(Items.STICK),
                        'X', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "X X",
                        "X#X",
                        "X X"
                )),
                new ItemStack(Items.RAIL, 16)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        'i', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
                ), List.of(
                        "i ",
                        " i"
                )),
                new ItemStack(Items.SHEARS)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(ItemTags.PLANKS)
                ), List.of(
                        "#O#",
                        "###",
                        " # "
                )),
                new ItemStack(Items.SHIELD)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(ItemTags.PLANKS)
                ), List.of(
                        "OO",
                        "##",
                        "##"
                )),
                new ItemStack(Items.SMITHING_TABLE)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
                        'O', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                        '#', Ingredient.of(Items.STONE)
                ), List.of(
                        " O ",
                        "###"
                )),
                new ItemStack(Items.STONECUTTER)
        );
        shaped(writer, "", "_from_lead_and_iron",
                PortShapedRecipePattern.of(Map.of(
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
        shapeless(writer, new ItemStack(Items.FLINT_AND_STEEL), Ingredient.of(ModTags.Items.LEAD_AND_IRON), Ingredient.of(Items.FLINT));
        shapeless(writer, new ItemStack(NatureBlocks.FEY_LOG_BLOCKS.LOG, 8), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.LOGS), Ingredient.of(MaterialItems.LIFE_MUSHROOM));

        // 石头及深板岩压力板
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.STONE)), List.of("##")), new ItemStack(FunctionalBlocks.STONE_PRESSURE_PLATE));
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.DEEPSLATE)), List.of("##")), new ItemStack(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE));

        shapeless(writer, ToolItems.NPC_INVITATION.toStack(), Ingredient.of(Items.PAPER), Ingredient.of(Items.HONEYCOMB, MaterialItems.ROYAL_WAX));
        shapeless(writer, ToolItems.GUIDE_TO_PEACEFUL_COEXISTENCE.toStack(), Ingredient.of(ToolItems.GUIDE_TO_CRITTER_COMPANIONSHIP), Ingredient.of(ToolItems.GUIDE_TO_ENVIRONMENTAL_PRESERVATION));

        shapeless(writer, FunctionalBlocks.PEACE_CANDLE.toStack(), Ingredient.of(ItemTags.CANDLES), AmountIngredient.of(2, ModTags.Items.GOLD_AND_PLATINUM), Ingredient.of(MaterialItems.PINK_GEL));

        shapeless(writer, FunctionalBlocks.HEART_LANTERN.toStack(), Ingredient.of(Items.CHAIN), Ingredient.of(ConsumableItems.LIFE_CRYSTAL));
        shapeless(writer, FunctionalBlocks.STAR_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.FALLING_STAR));
        shapeless(writer, FunctionalBlocks.SOUL_OF_FLIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_FLIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_LIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_LIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_SIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_SIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_MIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_MIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_FRIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_FRIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_BRIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_BRIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_NIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_NIGHT));
        shapeless(writer, FunctionalBlocks.SOUL_OF_VOIGHT_IN_A_BOTTLE.toStack(), Ingredient.of(PotionItems.BOTTLE, Items.GLASS_BOTTLE), Ingredient.of(MaterialItems.SOUL_OF_VOIGHT));

        shapeless(writer, VanityArmorItems.SUNGLASSES.toStack(), Ingredient.of(MaterialItems.BLACK_LENS), Ingredient.of(MaterialItems.BLACK_LENS));

        shapeless(writer, ConsumableItems.DRY_BOMB.toStack(), Ingredient.of(ConsumableItems.WET_BOMB, ConsumableItems.HONEY_BOMB, ConsumableItems.LAVA_BOMB));
        shapeless(writer, ConsumableItems.WET_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(Items.WATER_BUCKET));
        shapeless(writer, ConsumableItems.HONEY_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(ToolItems.HONEY_BUCKET));
        shapeless(writer, ConsumableItems.LAVA_BOMB.toStack(), Ingredient.of(ConsumableItems.DRY_BOMB), Ingredient.of(Items.LAVA_BUCKET));

//        shapeless(writer, ConsumableItems.FALLEN_SOUL_CORE.toStack(), Ingredient.of(Items.BONE), Ingredient.of(Items.ROTTEN_FLESH), Ingredient.of(MaterialItems.FALLING_STAR));

        shapeless(writer, DecorativeBlocks.POO_BLOCK.toStack(4), Ingredient.of(ModBlocks.POO));

        // 暗影蜡烛 shapeless(writer, ToolItems.SHADOW_CANDLE.toStack(), Ingredient.of(ItemTags.CANDLES), AmountIngredient.of(3,ModTags.Items.EVIL_INGOT));
        // 钱币
        shapeless(writer, ModItems.COPPER_COIN.toStack(100), Ingredient.of(ModItems.SILVER_COIN));
        shapeless(writer, ModItems.SILVER_COIN.toStack(100), Ingredient.of(ModItems.GOLD_COIN));
        shapeless(writer, ModItems.GOLD_COIN.toStack(100), Ingredient.of(ModItems.PLATINUM_COIN));

        shapeless(writer, MaterialItems.RAW_ASPHALT.toStack(), AmountIngredient.of(2, ItemTags.STONE_CRAFTING_MATERIALS), Ingredient.of(MaterialItems.GEL));

        writer.accept(new UnitFinishedRecipe(BoomBunnyRecipe.getInstance()));
        writer.accept(new UnitFinishedRecipe(DragonPepperExtractingRecipe.getInstance()));
    }

    protected void shaped(Consumer<FinishedRecipe> writer, String prefix, String suffix, PortShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        writer.accept(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, true), createAdvancementBuilder(id, pattern.ingredients()));
    }

    protected void shaped(Consumer<FinishedRecipe> writer, PortShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource(getItemName(result.getItem()));
        writer.accept(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, true), createAdvancementBuilder(id, pattern.ingredients()));
    }

    protected void shapeless(Consumer<FinishedRecipe> writer, String prefix, String suffix, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(id, new ShapelessRecipe("", CraftingBookCategory.MISC, result, zingredients), createAdvancementBuilder(id, zingredients));
    }

    protected void shapeless(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource(getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(id, new ShapelessRecipe("", CraftingBookCategory.MISC, result, zingredients), createAdvancementBuilder(id, zingredients));
    }

    // 九原料合成一块的合成及分解配方
    protected void compressAndDecompressNine(Consumer<FinishedRecipe> writer, ItemLike decompressed, TagKey<Item> decompressedTag, ItemLike compressed, TagKey<Item> compressedTag) {
        ResourceLocation id1 = Confluence.asResource(getItemName(decompressed).concat("_from_decompacting"));
        NonNullList<Ingredient> ingredients = NonNullList.of(Ingredient.EMPTY, Ingredient.of(compressedTag));
        writer.accept(id1, new ShapelessRecipe("", CraftingBookCategory.BUILDING, new ItemStack(decompressed, 9), ingredients), createAdvancementBuilder(id1, ingredients));
        ResourceLocation id2 = Confluence.asResource(getItemName(compressed).concat("_from_compacting"));
        PortShapedRecipePattern pattern = PortShapedRecipePattern.of(Map.of('A', Ingredient.of(decompressedTag)), List.of("AAA", "AAA", "AAA"));
        writer.accept(id2, new ShapedRecipe("", CraftingBookCategory.BUILDING, pattern, compressed.asItem().getDefaultInstance()), createAdvancementBuilder(id2, pattern.ingredients()));
    }

    // 木头配方
    private void registerWoodRecipes(Consumer<FinishedRecipe> writer, LogBlockSet blockSet) {
        ItemLike[] logs = Stream.of(blockSet.LOG, blockSet.STRIPPED_LOG, blockSet.WOOD, blockSet.STRIPPED_WOOD).filter(PortDeferredBlock::isBound).toArray(ItemLike[]::new);
        if (logs.length > 0) shapeless(writer, blockSet.PLANKS.toStack(4), Ingredient.of(logs));
        shapeless(writer, blockSet.BUTTON.toStack(), Ingredient.of(blockSet.PLANKS));
        if (blockSet.LOG.isBound() && blockSet.WOOD.isBound())
            shaped(writer, PortShapedRecipePattern.of(Map.of(
                    '#', Ingredient.of(blockSet.LOG)
            ), List.of(
                    "##",
                    "##"
            )), blockSet.WOOD.toStack(3));
        if (blockSet.STRIPPED_LOG.isBound() && blockSet.STRIPPED_WOOD.isBound())
            shaped(writer, PortShapedRecipePattern.of(Map.of(
                    '#', Ingredient.of(blockSet.STRIPPED_LOG)
            ), List.of(
                    "##",
                    "##"
            )), blockSet.STRIPPED_WOOD.toStack(3));
        if (blockSet.STAIRS.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS)
        ), List.of(
                "#  ",
                "## ",
                "###"
        )), blockSet.STAIRS.toStack(4));
        if (blockSet.SLAB.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS)
        ), List.of(
                "###"
        )), blockSet.SLAB.toStack(6));
        if (blockSet.FENCE.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#/#",
                "#/#"
        )), blockSet.FENCE.toStack(3));
        if (blockSet.FENCE_GATE.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "/#/",
                "/#/"
        )), blockSet.FENCE_GATE.toStack());
        if (blockSet.DOOR.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS)
        ), List.of(
                "##",
                "##",
                "##"
        )), blockSet.DOOR.toStack(3));
        if (blockSet.TRAPDOOR.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS)
        ), List.of(
                "###",
                "###"
        )), blockSet.TRAPDOOR.toStack(2));
        if (blockSet.PRESSURE_PLATE.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS)
        ), List.of(
                "##"
        )), blockSet.PRESSURE_PLATE.toStack());
        if (blockSet.SIGN_ITEM.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                "###",
                " / "
        )), blockSet.SIGN_ITEM.toStack(3));
        if (blockSet.CHISELED_PLANKS.isBound()) shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(blockSet.SLAB)
        ), List.of(
                "#",
                "#"
        )), blockSet.CHISELED_PLANKS.toStack());
        if (blockSet.HANGING_SIGN.isBound() && blockSet.STRIPPED_LOG.isBound())
            shaped(writer, PortShapedRecipePattern.of(Map.of(
                    '|', Ingredient.of(Blocks.CHAIN),
                    '#', Ingredient.of(blockSet.STRIPPED_LOG)
            ), List.of(
                    "| |",
                    "###",
                    "###"
            )), blockSet.HANGING_SIGN.toStack(6));
    }

    private void registerBoatRecipes(Consumer<FinishedRecipe> writer, LogBlockSet woodSet, ItemLike boatItem, ItemLike chestBoatItem) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(woodSet.PLANKS)
        ), List.of(
                "# #",
                "###"
        )), boatItem.asItem().getDefaultInstance());

        shapeless(writer, chestBoatItem.asItem().getDefaultInstance(),
                Ingredient.of(boatItem),
                Ingredient.of(Tags.Items.CHESTS_WOODEN)
        );
    }

    private void registerArmorRecipes(Consumer<FinishedRecipe> writer, Ingredient materialIngredient, ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "###",
                "# #"
        )), helmet.asItem().getDefaultInstance());

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "# #",
                "# #"
        )), boots.asItem().getDefaultInstance());

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "###",
                "# #",
                "# #"
        )), leggings.asItem().getDefaultInstance());

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', materialIngredient
        ), List.of(
                "# #",
                "###",
                "###"
        )), chestplate.asItem().getDefaultInstance());
    }

    private void registerBowRecipes(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike normalBow, ItemLike shortBow) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STRING)
        ), BOW_PATTERN), normalBow.asItem().getDefaultInstance());

        registerShortBowRecipes(writer, material, Ingredient.of(Items.STRING), shortBow);
    }

    private void registerShortBowRecipes(Consumer<FinishedRecipe> writer, Ingredient material0, Ingredient material1, ItemLike shortBow) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material0,
                '/', material1
        ), SHORT_BOW_PATTERN), shortBow.asItem().getDefaultInstance());
    }

    private void registerToolRecipes(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike axe, ItemLike pickaxe, ItemLike hammer, ItemLike broadsword, ItemLike shortsword, ItemLike shovel, ItemLike hoe) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), AXE_PATTERN), axe.asItem().getDefaultInstance());

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), PICKAXE_PATTERN), pickaxe.asItem().getDefaultInstance());

        registerHammerRecipe(writer, material, hammer);
        registerBroadswordRecipe(writer, material, broadsword);
        registerShortSwordRecipe(writer, material, shortsword);

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), SHOVEL_PATTERN), shovel.asItem().getDefaultInstance());

        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), HOE_PATTERN), hoe.asItem().getDefaultInstance());
    }

    private void registerHammerRecipe(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike hammer) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), HAMMER_PATTERN), hammer.asItem().getDefaultInstance());
    }

    private void registerBroadswordRecipe(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike broadsword) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), BROADSWORD_PATTERN), broadsword.asItem().getDefaultInstance());
    }

    private void registerShortSwordRecipe(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike shortsword) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material,
                '/', Ingredient.of(Items.STICK)
        ), SHORT_SWORD_PATTERN), shortsword.asItem().getDefaultInstance());
    }

    private void registerShearsRecipe(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike shears) {
        shaped(writer, PortShapedRecipePattern.of(Map.of(
                '#', material
        ), SHEAR_PATTERN), shears.asItem().getDefaultInstance());
    }

    private void registerBricksRecipes(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike bricks, int count) {
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', material), BRICKS_PATTERN), bricks.asItem().getDefaultInstance());
    }

    private void registerChainsRecipes(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike chains) {
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', material), CHAINS_PATTERN), chains.asItem().getDefaultInstance());
    }

    private void registerPureGlassRecipes(Consumer<FinishedRecipe> writer, Ingredient material, Ingredient material2, ItemLike block, int count) {
        ItemStack result = new ItemStack(block.asItem(), count);
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', material, '/', material2), PURE_GLASS_PATTERN), result);
    }

    private void registeTableRecipes(Consumer<FinishedRecipe> writer, Ingredient material, ItemLike block, int count) {
        ItemStack result = new ItemStack(block.asItem(), count);
        shaped(writer, PortShapedRecipePattern.of(Map.of('#', material), TABLES_PATTERN), result);
    }
}
