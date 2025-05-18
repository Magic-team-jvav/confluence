package org.confluence.mod.common.data.gen.recipe;

import com.xiaohunao.enemybanner.blocks.BannerBoxBlock;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terraentity.init.TEItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class CraftingRecipeProvider extends AbstractRecipeProvider {
    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output, HolderLookup.Provider holderLookup) {
        // 注册矿物块的合成与分解配方
        compressAndDecompressNine(MaterialItems.TIN_INGOT.get(), ModTags.Items.INGOTS_TIN,
                OreBlocks.TIN_BLOCK.asItem(), ModTags.Items.TIN_BLOCK, output);
        compressAndDecompressNine(MaterialItems.LEAD_INGOT.get(), ModTags.Items.INGOTS_LEAD,
                OreBlocks.LEAD_BLOCK.asItem(), ModTags.Items.LEAD_BLOCK, output);
        compressAndDecompressNine(MaterialItems.SILVER_INGOT.get(), ModTags.Items.INGOTS_SILVER,
                OreBlocks.SILVER_BLOCK.asItem(), ModTags.Items.SILVER_BLOCK, output);
        compressAndDecompressNine(MaterialItems.TUNGSTEN_INGOT.get(), ModTags.Items.INGOTS_TUNGSTEN,
                OreBlocks.TUNGSTEN_BLOCK.asItem(), ModTags.Items.TUNGSTEN_BLOCK, output);
        compressAndDecompressNine(MaterialItems.PLATINUM_INGOT.get(), ModTags.Items.INGOTS_PLATINUM,
                OreBlocks.PLATINUM_BLOCK.asItem(), ModTags.Items.PLATINUM_BLOCK, output);
        compressAndDecompressNine(MaterialItems.METEORITE_INGOT.get(), ModTags.Items.INGOTS_METEORITE,
                OreBlocks.METEORITE_BLOCK.asItem(), ModTags.Items.METEORITE_BLOCK, output);
        compressAndDecompressNine(MaterialItems.DEMONITE_INGOT.get(), ModTags.Items.INGOTS_DEMONITE,
                OreBlocks.DEMONITE_BLOCK.asItem(), ModTags.Items.DEMONITE_BLOCK, output);
        compressAndDecompressNine(MaterialItems.TR_CRIMSON_INGOT.get(), ModTags.Items.INGOTS_CRIMSON,
                OreBlocks.TR_CRIMSON_BLOCK.asItem(), ModTags.Items.CRIMSON_BLOCK, output);
        compressAndDecompressNine(MaterialItems.HELLSTONE_INGOT.get(), ModTags.Items.INGOTS_HELLSTONE,
                OreBlocks.HELLSTONE_BLOCK.asItem(), ModTags.Items.HELLSTONE_BLOCK, output);
        // 粗矿
        compressAndDecompressNine(MaterialItems.RAW_TIN.get(), ModTags.Items.RAW_MATERIALS_TIN,
                OreBlocks.RAW_TIN_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_TIN_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_LEAD.get(), ModTags.Items.RAW_MATERIALS_LEAD,
                OreBlocks.RAW_LEAD_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_LEAD_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_SILVER.get(), ModTags.Items.RAW_MATERIALS_SILVER,
                OreBlocks.RAW_SILVER_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_SILVER_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_TUNGSTEN.get(), ModTags.Items.RAW_MATERIALS_TUNGSTEN,
                OreBlocks.RAW_TUNGSTEN_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_TUNGSTEN_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_PLATINUM.get(), ModTags.Items.RAW_MATERIALS_PLATINUM,
                OreBlocks.RAW_PLATINUM_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_PLATINUM_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_METEORITE.get(), ModTags.Items.RAW_MATERIALS_METEORITE,
                OreBlocks.RAW_METEORITE_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_METEORITE_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_DEMONITE.get(), ModTags.Items.RAW_MATERIALS_DEMONITE,
                OreBlocks.RAW_DEMONITE_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_DEMONITE_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_TR_CRIMSON.get(), ModTags.Items.RAW_MATERIALS_CRIMSON,
                OreBlocks.RAW_TR_CRIMSON_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_CRIMSON_BLOCK, output);
        compressAndDecompressNine(MaterialItems.RAW_HELLSTONE.get(), ModTags.Items.RAW_MATERIALS_HELLSTONE,
                OreBlocks.RAW_HELLSTONE_BLOCK.asItem(), ModTags.Items.RAW_MATERIALS_HELLSTONE_BLOCK, output);

        // 铅砧
        shaped(output, "", "", ShapedRecipePattern.of(Map.of(
                'I', Ingredient.of(ModTags.Items.LEAD_BLOCK),
                'i', Ingredient.of(ModTags.Items.INGOTS_LEAD)
        ), List.of(
                "III",
                " i ",
                "iii"
        )), FunctionalBlocks.LEAD_ANVIL.toStack());
        // 房屋探测器
        shaped(output, "", "", ShapedRecipePattern.of(Map.of(
                'B', Ingredient.of(ItemTags.PLANKS),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                " B ",
                "B/B",
                "/ /"
        )), TEItems.HOUSE_DETECTOR.get().getDefaultInstance());
        // 蛛网
        shaped(output, "", "", ShapedRecipePattern.of(Map.of(
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
        shaped(output, "", "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.SIGNS),
                'I', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                'R', Ingredient.of(Items.REDSTONE)
        ), List.of(
                "RIR",
                "I#I",
                "RIR"
        )), new ItemStack(FunctionalBlocks.ANNOUNCEMENT_BOX_ITEM.asItem()));
        // 蜂蜜瓶
        shapeless(output, "", "_from_glass_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET.get()),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE),
                Ingredient.of(Items.GLASS_BOTTLE)
        );
        shapeless(output, "", "_from_bottle",
                new ItemStack(Items.HONEY_BOTTLE, 3),
                Ingredient.of(ToolItems.HONEY_BUCKET.get()),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );

        // 石头及深板岩压力板
        shaped(output, "", "", ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.STONE.asItem())), List.of("##")), new ItemStack(FunctionalBlocks.STONE_PRESSURE_PLATE.asItem()));
        shaped(output, "", "", ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.DEEPSLATE.asItem())), List.of("##")), new ItemStack(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE.asItem()));

        shapeless(output, "", "", ToolItems.NPC_INVITATION.toStack(), Ingredient.of(Items.PAPER), Ingredient.of(Items.HONEYCOMB, MaterialItems.ROYAL_WAX));
    }

    protected void shaped(RecipeOutput recipeOutput, String prefix, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new ShapedRecipe("", CraftingBookCategory.MISC, pattern, result, true), null);
    }

    protected void shapeless(RecipeOutput recipeOutput, String prefix, String suffix, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new ShapelessRecipe("", CraftingBookCategory.MISC, result, zingredients), null);
    }

    // 九原料合成一块的合成及分解配方
    protected void compressAndDecompressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result, TagKey<Item> resultTag, @NotNull RecipeOutput output) {
        compressNine(input, inputTag, result).save(output);
        decompressNine(result, resultTag, input).save(output, BuiltInRegistries.ITEM.getKey(input.asItem()) + "_from_block");
    }

    protected ShapedRecipeBuilder compressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('A', inputTag)
                .pattern("AAA").pattern("AAA").pattern("AAA")
                .unlockedBy("hasitem", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(input)));
    }

    protected ShapelessRecipeBuilder decompressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result) {
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 9)
                .requires(inputTag)
                .unlockedBy("hasitem", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(input)));
    }
}
