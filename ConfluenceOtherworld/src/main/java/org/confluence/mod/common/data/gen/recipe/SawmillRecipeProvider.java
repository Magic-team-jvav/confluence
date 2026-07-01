package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.SimpleFinishedRecipe;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.natural.LogBlockSet;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.recipe.SawmillRecipe;
import org.mesdag.portlib.registries.PortDeferredBlock;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SawmillRecipeProvider extends AbstractRecipeProvider {
    public SawmillRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        shaped(writer, FunctionalBlocks.KEG.toStack(), PortShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(Tags.Items.BARRELS_WOODEN),
                'B', Ingredient.of(Tags.Items.FENCES_WOODEN),
                'C', Ingredient.of(Tags.Items.INGOTS_COPPER)
        ), List.of(
                "AC",
                "B "
        )));

        shaped(writer, FunctionalBlocks.LOOM.toStack(), PortShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ItemTags.WOODEN_SLABS),
                'b', AmountIngredient.of(2, ItemTags.PLANKS)
        ), List.of(
                "  bb",
                "  bb",
                "bccb"
        )));

        shapeless(writer, new ItemStack(Items.OAK_PLANKS, 9), Ingredient.of(ItemTags.OAK_LOGS));
        shapeless(writer, new ItemStack(Items.ACACIA_PLANKS, 9), Ingredient.of(ItemTags.ACACIA_LOGS));
        shapeless(writer, new ItemStack(Items.BAMBOO_PLANKS), Ingredient.of(Items.BAMBOO));
        shapeless(writer, new ItemStack(Items.CHERRY_PLANKS, 9), Ingredient.of(ItemTags.CHERRY_LOGS));
        shapeless(writer, new ItemStack(Items.CRIMSON_PLANKS, 9), Ingredient.of(ItemTags.CRIMSON_STEMS));
        shapeless(writer, new ItemStack(Items.DARK_OAK_PLANKS, 9), Ingredient.of(ItemTags.DARK_OAK_LOGS));
        shapeless(writer, new ItemStack(Items.JUNGLE_PLANKS, 9), Ingredient.of(ItemTags.JUNGLE_LOGS));
        shapeless(writer, new ItemStack(Items.BIRCH_PLANKS, 9), Ingredient.of(ItemTags.BIRCH_LOGS));
        shapeless(writer, new ItemStack(Items.MANGROVE_PLANKS, 9), Ingredient.of(ItemTags.MANGROVE_LOGS));
        shapeless(writer, new ItemStack(Items.SPRUCE_PLANKS, 9), Ingredient.of(ItemTags.SPRUCE_LOGS));
        shapeless(writer, new ItemStack(Items.WARPED_PLANKS, 9), Ingredient.of(ItemTags.WARPED_STEMS));

        for (LogBlockSet blockSet : LogBlockSet.LOG_BLOCK_SETS) {
            ItemLike[] logs = Stream.of(blockSet.LOG, blockSet.STRIPPED_LOG, blockSet.WOOD, blockSet.STRIPPED_WOOD).filter(PortDeferredBlock::isBound).toArray(ItemLike[]::new);
            if (logs.length > 0)
                shapeless(writer, blockSet.PLANKS.toStack(9), Ingredient.of(logs));
            if (blockSet.SLAB.isBound())
                shapeless(writer, blockSet.SLAB.toStack(4), Ingredient.of(blockSet.PLANKS));
            if (blockSet.STAIRS.isBound())
                shapeless(writer, blockSet.STAIRS.toStack(2), Ingredient.of(blockSet.PLANKS));
        }

        shapeless(writer, new ItemStack(Items.OAK_SLAB, 4), Ingredient.of(Items.OAK_PLANKS));
        shapeless(writer, new ItemStack(Items.ACACIA_SLAB, 4), Ingredient.of(Items.ACACIA_PLANKS));
        shapeless(writer, new ItemStack(Items.BAMBOO_SLAB, 4), Ingredient.of(Items.BAMBOO_PLANKS));
        shapeless(writer, new ItemStack(Items.CHERRY_SLAB, 4), Ingredient.of(Items.CHERRY_PLANKS));
        shapeless(writer, new ItemStack(Items.CRIMSON_SLAB, 4), Ingredient.of(Items.CRIMSON_PLANKS));
        shapeless(writer, new ItemStack(Items.DARK_OAK_SLAB, 4), Ingredient.of(Items.DARK_OAK_PLANKS));
        shapeless(writer, new ItemStack(Items.JUNGLE_SLAB, 4), Ingredient.of(Items.JUNGLE_PLANKS));
        shapeless(writer, new ItemStack(Items.BIRCH_SLAB, 4), Ingredient.of(Items.BIRCH_PLANKS));
        shapeless(writer, new ItemStack(Items.MANGROVE_SLAB, 4), Ingredient.of(Items.MANGROVE_PLANKS));
        shapeless(writer, new ItemStack(Items.SPRUCE_SLAB, 4), Ingredient.of(Items.SPRUCE_PLANKS));
        shapeless(writer, new ItemStack(Items.WARPED_SLAB, 4), Ingredient.of(Items.WARPED_PLANKS));

        shapeless(writer, new ItemStack(Items.OAK_STAIRS, 2), Ingredient.of(Items.OAK_PLANKS));
        shapeless(writer, new ItemStack(Items.ACACIA_STAIRS, 2), Ingredient.of(Items.ACACIA_PLANKS));
        shapeless(writer, new ItemStack(Items.BAMBOO_STAIRS, 2), Ingredient.of(Items.BAMBOO_PLANKS));
        shapeless(writer, new ItemStack(Items.CHERRY_STAIRS, 2), Ingredient.of(Items.CHERRY_PLANKS));
        shapeless(writer, new ItemStack(Items.CRIMSON_STAIRS, 2), Ingredient.of(Items.CRIMSON_PLANKS));
        shapeless(writer, new ItemStack(Items.DARK_OAK_STAIRS, 2), Ingredient.of(Items.DARK_OAK_PLANKS));
        shapeless(writer, new ItemStack(Items.JUNGLE_STAIRS, 2), Ingredient.of(Items.JUNGLE_PLANKS));
        shapeless(writer, new ItemStack(Items.BIRCH_STAIRS, 2), Ingredient.of(Items.BIRCH_PLANKS));
        shapeless(writer, new ItemStack(Items.MANGROVE_STAIRS, 2), Ingredient.of(Items.MANGROVE_PLANKS));
        shapeless(writer, new ItemStack(Items.SPRUCE_STAIRS, 2), Ingredient.of(Items.SPRUCE_PLANKS));
        shapeless(writer, new ItemStack(Items.WARPED_STAIRS, 2), Ingredient.of(Items.WARPED_PLANKS));
    }

    protected void shaped(Consumer<FinishedRecipe> writer, ItemStack result, PortShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new SawmillRecipe(result, pattern)));
    }

    protected void shapeless(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sawmill/" + getItemName(result.getItem()));
        NonNullList<Ingredient> ingredientz = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new SawmillRecipe(result, ingredientz)));
    }
}
