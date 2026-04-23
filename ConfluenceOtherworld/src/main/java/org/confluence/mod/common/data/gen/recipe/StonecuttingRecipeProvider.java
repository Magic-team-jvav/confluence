package org.confluence.mod.common.data.gen.recipe;

import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.block.palettes.DecoBlockSet;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.block.OreBlocks;
import org.confluence.mod.common.init.item.MaterialItems;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.confluence.mod.common.data.gen.recipe.ModRecipeProvider.createAdvancementHolder;

public class StonecuttingRecipeProvider extends AbstractRecipeProvider {
    public StonecuttingRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {

        stonecutting(recipeOutput, DecorativeBlocks.BLUE_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.BLUE_BRICKS.FULL));
        stonecutting(recipeOutput, DecorativeBlocks.GREEN_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.GREEN_BRICKS.FULL));
        stonecutting(recipeOutput, DecorativeBlocks.PINK_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.PINK_BRICKS.FULL));

        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(recipeOutput, DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(recipeOutput, DecorativeBlocks.AETHERIUM_BRICKS.FULL.toStack(4), Ingredient.of(NatureBlocks.AETHERIUM_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.OBSIDIAN_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.OBSIDIAN));
        stonecutting(recipeOutput, DecorativeBlocks.GLOOM_OBSIDIAN_BRICKS.FULL.toStack(4), Ingredient.of(NatureBlocks.GLOOM_OBSIDIAN));
        stonecutting(recipeOutput, DecorativeBlocks.SNOW_BRICKS.FULL.toStack(1), Ingredient.of(Blocks.SNOW));
        stonecutting(recipeOutput, DecorativeBlocks.BLUE_ICE_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.BLUE_ICE));
        stonecutting(recipeOutput, DecorativeBlocks.PACKED_ICE_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.PACKED_ICE));

        stonecutting(recipeOutput, DecorativeBlocks.GOLDEN_BRICKS.FULL.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_GOLDEN_BRICKS.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.COPPER_BRICKS.FULL.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_COPPER_BRICKS.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.COPPER_TILES.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.IRON_BRICKS.FULL.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_IRON_BRICKS.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.TIN_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_TIN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.TIN_TILES.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.LEAD_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_LEAD_BRICKS.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.SILVER_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_SILVER_BRICKS.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.TUNGSTEN_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_TUNGSTEN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.PLATINUM_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CHISELED_PLATINUM_BRICKS.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.DEMONITE_ORE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.DEMONITE_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.CRIMTANE_ORE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.CRIMTANE_BLOCK));
        stonecutting(recipeOutput, DecorativeBlocks.METEORITE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.METEORITE_BLOCK));

        stonecutting(recipeOutput, MaterialItems.CHINA_BOWL.toStack(3), Ingredient.of(Items.WHITE_GLAZED_TERRACOTTA));
        stonecutting(recipeOutput, MaterialItems.CHINA_PLATE.toStack(5), Ingredient.of(Items.WHITE_GLAZED_TERRACOTTA));

        stonecutting(recipeOutput, FunctionalBlocks.TUFF_BOOTH.toStack(), Ingredient.of(Items.TUFF));

        stonecutting(recipeOutput, "_from_granite", DecorativeBlocks.GRANITE_COLUMN.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(recipeOutput, "_from_granite", DecorativeBlocks.GRANITE_BRICKS.FULL.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(recipeOutput, "_from_granite", DecorativeBlocks.POLISHED_GRANITE.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(recipeOutput, "_from_granite", DecorativeBlocks.CHISELED_GRANITE_BRICKS.toStack(), Ingredient.of(NatureBlocks.GRANITE));

        stonecutting(recipeOutput, "_from_polished_granite", DecorativeBlocks.GRANITE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));
        stonecutting(recipeOutput, "_from_polished_granite", DecorativeBlocks.GRANITE_BRICKS.FULL.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));
        stonecutting(recipeOutput, "_from_polished_granite", DecorativeBlocks.CHISELED_GRANITE_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));

        stonecutting(recipeOutput, "_from_calcite", NatureBlocks.MARBLE.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.POLISHED_MARBLE.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(recipeOutput, "_from_calcite", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));

        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.POLISHED_MARBLE.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(recipeOutput, "_from_marble", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));

        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(recipeOutput, "_from_polished_marble", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));

        stonecutting(recipeOutput, "_from_obsidian_bricks",DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(recipeOutput, "_from_obsidian_bricks",DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(recipeOutput, "_from_obsidian_bricks",DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));

        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            if (!blockSet.stonecutting) continue;
            Ingredient full = Ingredient.of(blockSet.FULL.get());
            stonecutting(recipeOutput, blockSet.STAIRS.toStack(), full);
            stonecutting(recipeOutput, blockSet.SLAB.toStack(2), full);
            stonecutting(recipeOutput, blockSet.WALL.toStack(), full);

            for (ObjectIntPair<Supplier<? extends ItemLike>> material : blockSet.materials) {
                Ingredient ingredient = Ingredient.of(material.left().get().asItem().getDefaultInstance());
                int amount = material.rightInt();
                stonecutting(recipeOutput, blockSet.FULL.toStack(amount), ingredient);
                stonecutting(recipeOutput, blockSet.STAIRS.toStack(amount), ingredient);
                stonecutting(recipeOutput, blockSet.SLAB.toStack(amount * 2), ingredient);
                stonecutting(recipeOutput, blockSet.WALL.toStack(amount), ingredient);
            }
        }
    }

    protected void stonecutting(RecipeOutput recipeOutput, String suffix, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new StonecutterRecipe("", ingredient, result), createAdvancementHolder(recipeOutput, id, ingredient));
    }

    protected void stonecutting(RecipeOutput recipeOutput, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new StonecutterRecipe("", ingredient, result), createAdvancementHolder(recipeOutput, id, ingredient));
    }
}
