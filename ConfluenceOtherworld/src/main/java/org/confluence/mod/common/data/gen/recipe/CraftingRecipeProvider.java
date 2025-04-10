package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CraftingRecipeProvider extends AbstractRecipeProvider {
    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
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
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, FunctionalBlocks.LEAD_ANVIL)
                .define('I', ModTags.Items.LEAD_BLOCK).define('i', ModTags.Items.INGOTS_LEAD)
                .pattern("III").pattern(" i ").pattern("iii")
                .unlockedBy("hasitem", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(MaterialItems.LEAD_INGOT)))
                .save(output);
    }
    // 九原料合成一块的合成及分解配方
    protected void compressAndDecompressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result, TagKey<Item> resultTag, @NotNull RecipeOutput output){
        compressNine(input, inputTag, result).save(output);
        decompressNine(result, resultTag, input).save(output, BuiltInRegistries.ITEM.getKey(input.asItem()) + "_from_block");
    }
    protected ShapedRecipeBuilder compressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result){
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, result)
                .define('A', inputTag)
                .pattern("AAA").pattern("AAA").pattern("AAA")
                .unlockedBy("hasitem", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(input)));
    }
    protected ShapelessRecipeBuilder decompressNine(ItemLike input, TagKey<Item> inputTag, ItemLike result){
        return ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, result, 9)
                .requires(inputTag)
                .unlockedBy("hasitem", inventoryTrigger(net.minecraft.advancements.critereon.ItemPredicate.Builder.item().of(input)));
    }
}
