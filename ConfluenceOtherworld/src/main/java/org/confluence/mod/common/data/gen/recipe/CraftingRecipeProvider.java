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
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;
import org.confluence.mod.common.init.item.PotionItems;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terraentity.init.TEItems;

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
        compressAndDecompressNine(output, MaterialItems.TIN_INGOT, ModTags.Items.INGOTS_TIN, OreBlocks.TIN_BLOCK, ModTags.Items.TIN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.LEAD_INGOT, ModTags.Items.INGOTS_LEAD, OreBlocks.LEAD_BLOCK, ModTags.Items.LEAD_BLOCK);
        compressAndDecompressNine(output, MaterialItems.SILVER_INGOT, ModTags.Items.INGOTS_SILVER, OreBlocks.SILVER_BLOCK, ModTags.Items.SILVER_BLOCK);
        compressAndDecompressNine(output, MaterialItems.TUNGSTEN_INGOT, ModTags.Items.INGOTS_TUNGSTEN, OreBlocks.TUNGSTEN_BLOCK, ModTags.Items.TUNGSTEN_BLOCK);
        compressAndDecompressNine(output, MaterialItems.PLATINUM_INGOT, ModTags.Items.INGOTS_PLATINUM, OreBlocks.PLATINUM_BLOCK, ModTags.Items.PLATINUM_BLOCK);
        compressAndDecompressNine(output, MaterialItems.METEORITE_INGOT, ModTags.Items.INGOTS_METEORITE, OreBlocks.METEORITE_BLOCK, ModTags.Items.METEORITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.DEMONITE_INGOT, ModTags.Items.INGOTS_DEMONITE, OreBlocks.DEMONITE_BLOCK, ModTags.Items.DEMONITE_BLOCK);
        compressAndDecompressNine(output, MaterialItems.CRIMTANE_INGOT, ModTags.Items.INGOTS_CRIMSON, OreBlocks.CRIMTANE_BLOCK, ModTags.Items.CRIMSON_BLOCK);
        compressAndDecompressNine(output, MaterialItems.HELLSTONE_INGOT, ModTags.Items.INGOTS_HELLSTONE, OreBlocks.HELLSTONE_BLOCK, ModTags.Items.HELLSTONE_BLOCK);
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
        )), TEItems.HOUSE_DETECTOR.toStack());
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
        // 玻璃瓶互换
        shapeless(output, "", "_from_bottle",
                new ItemStack(Items.GLASS_BOTTLE),
                Ingredient.of(PotionItems.BOTTLE)
        );
        shapeless(output, "", "_from_glass_bottle",
                new ItemStack(PotionItems.BOTTLE.get()),
                Ingredient.of(Items.GLASS_BOTTLE)
        );

        // 石头及深板岩压力板
        shaped(output, "", "", ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.STONE)), List.of("##")), new ItemStack(FunctionalBlocks.STONE_PRESSURE_PLATE));
        shaped(output, "", "", ShapedRecipePattern.of(Map.of('#', Ingredient.of(Blocks.DEEPSLATE)), List.of("##")), new ItemStack(FunctionalBlocks.DEEPSLATE_PRESSURE_PLATE));

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
    protected void compressAndDecompressNine(RecipeOutput output, ItemLike decompressed, TagKey<Item> decompressedTag, ItemLike compressed, TagKey<Item> compressedTag) {
        output.accept(Confluence.asResource(getItemName(decompressed)), new ShapelessRecipe("", CraftingBookCategory.BUILDING, new ItemStack(decompressed, 9), NonNullList.of(Ingredient.EMPTY, Ingredient.of(compressedTag))), null);
        output.accept(Confluence.asResource(getItemName(compressed)), new ShapedRecipe("", CraftingBookCategory.BUILDING, ShapedRecipePattern.of(Map.of('A', Ingredient.of(decompressedTag)), List.of("AAA", "AAA", "AAA")), compressed.asItem().getDefaultInstance()), null);
    }
}
