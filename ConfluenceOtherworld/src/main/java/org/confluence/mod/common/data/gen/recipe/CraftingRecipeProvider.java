package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class CraftingRecipeProvider extends RecipeProvider {
    public CraftingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }
    // 注册矿物块的合成与分解配方
    @Override
    protected void buildRecipes(@NotNull RecipeOutput output) {
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
    }

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
