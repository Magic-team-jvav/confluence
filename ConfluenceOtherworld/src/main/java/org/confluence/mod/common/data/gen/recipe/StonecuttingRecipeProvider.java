package org.confluence.mod.common.data.gen.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectIntPair;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
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

import java.util.function.Consumer;
import java.util.function.Supplier;

public class StonecuttingRecipeProvider extends AbstractRecipeProvider {
    public StonecuttingRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {
        stonecutting(writer, DecorativeBlocks.BLUE_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.BLUE_BRICKS.FULL));
        stonecutting(writer, DecorativeBlocks.GREEN_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.GREEN_BRICKS.FULL));
        stonecutting(writer, DecorativeBlocks.PINK_BRICK_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.PINK_BRICKS.FULL));

        stonecutting(writer, DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(writer, DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(writer, DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(writer, DecorativeBlocks.AETHERIUM_BRICKS.FULL.toStack(4), Ingredient.of(NatureBlocks.AETHERIUM_BLOCK));
        stonecutting(writer, DecorativeBlocks.OBSIDIAN_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.OBSIDIAN));
        stonecutting(writer, DecorativeBlocks.GLOOM_OBSIDIAN_BRICKS.FULL.toStack(4), Ingredient.of(NatureBlocks.GLOOM_OBSIDIAN));
        stonecutting(writer, DecorativeBlocks.CRYING_OBSIDIAN_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.CRYING_OBSIDIAN));
        stonecutting(writer, DecorativeBlocks.SNOW_BRICKS.FULL.toStack(1), Ingredient.of(Blocks.SNOW));
        stonecutting(writer, DecorativeBlocks.BLUE_ICE_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.BLUE_ICE));
        stonecutting(writer, DecorativeBlocks.PACKED_ICE_BRICKS.FULL.toStack(4), Ingredient.of(Blocks.PACKED_ICE));

        stonecutting(writer, DecorativeBlocks.GOLDEN_BRICKS.FULL.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_GOLDEN_BRICKS.toStack(9), Ingredient.of(Items.GOLD_BLOCK));
        stonecutting(writer, DecorativeBlocks.COPPER_BRICKS.FULL.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_COPPER_BRICKS.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(writer, DecorativeBlocks.COPPER_TILES.toStack(9), Ingredient.of(Items.COPPER_BLOCK));
        stonecutting(writer, DecorativeBlocks.IRON_BRICKS.FULL.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_IRON_BRICKS.toStack(9), Ingredient.of(Items.IRON_BLOCK));
        stonecutting(writer, DecorativeBlocks.TIN_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_TIN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(writer, DecorativeBlocks.TIN_TILES.toStack(9), Ingredient.of(OreBlocks.TIN_BLOCK));
        stonecutting(writer, DecorativeBlocks.LEAD_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_LEAD_BRICKS.toStack(9), Ingredient.of(OreBlocks.LEAD_BLOCK));
        stonecutting(writer, DecorativeBlocks.SILVER_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_SILVER_BRICKS.toStack(9), Ingredient.of(OreBlocks.SILVER_BLOCK));
        stonecutting(writer, DecorativeBlocks.TUNGSTEN_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_TUNGSTEN_BRICKS.toStack(9), Ingredient.of(OreBlocks.TUNGSTEN_BLOCK));
        stonecutting(writer, DecorativeBlocks.PLATINUM_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(writer, DecorativeBlocks.CHISELED_PLATINUM_BRICKS.toStack(9), Ingredient.of(OreBlocks.PLATINUM_BLOCK));
        stonecutting(writer, DecorativeBlocks.DEMONITE_ORE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.DEMONITE_BLOCK));
        stonecutting(writer, DecorativeBlocks.CRIMTANE_ORE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.CRIMTANE_BLOCK));
        stonecutting(writer, DecorativeBlocks.METEORITE_BRICKS.FULL.toStack(9), Ingredient.of(OreBlocks.METEORITE_BLOCK));

        stonecutting(writer, MaterialItems.CHINA_BOWL.toStack(3), Ingredient.of(Items.WHITE_GLAZED_TERRACOTTA));
        stonecutting(writer, MaterialItems.CHINA_PLATE.toStack(5), Ingredient.of(Items.WHITE_GLAZED_TERRACOTTA));

        stonecutting(writer, FunctionalBlocks.TUFF_BOOTH.toStack(), Ingredient.of(Items.TUFF));

        stonecutting(writer, "_from_granite", DecorativeBlocks.GRANITE_COLUMN.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(writer, "_from_granite", DecorativeBlocks.GRANITE_BRICKS.FULL.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(writer, "_from_granite", DecorativeBlocks.POLISHED_GRANITE.toStack(), Ingredient.of(NatureBlocks.GRANITE));
        stonecutting(writer, "_from_granite", DecorativeBlocks.CHISELED_GRANITE_BRICKS.toStack(), Ingredient.of(NatureBlocks.GRANITE));

        stonecutting(writer, "_from_polished_granite", DecorativeBlocks.GRANITE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));
        stonecutting(writer, "_from_polished_granite", DecorativeBlocks.GRANITE_BRICKS.FULL.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));
        stonecutting(writer, "_from_polished_granite", DecorativeBlocks.CHISELED_GRANITE_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_GRANITE));

        stonecutting(writer, "_from_calcite", NatureBlocks.MARBLE.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.POLISHED_MARBLE.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));
        stonecutting(writer, "_from_calcite", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(Blocks.CALCITE));

        stonecutting(writer, "_from_marble", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.POLISHED_MARBLE.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));
        stonecutting(writer, "_from_marble", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(NatureBlocks.MARBLE));

        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.MARBLE_COLUMN.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.MARBLE_BRICKS.FULL.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.MARBLE_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.CHISELED_MARBLE_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.MARBLE_CHESSBOARD_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));
        stonecutting(writer, "_from_polished_marble", DecorativeBlocks.MARBLE_ETERNAL_CHESSBOARD_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.POLISHED_MARBLE));

        stonecutting(writer, "_from_obsidian_bricks", DecorativeBlocks.CHISELED_OBSIDIAN_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(writer, "_from_obsidian_bricks", DecorativeBlocks.OBSIDIAN_SMALL_BRICKS.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));
        stonecutting(writer, "_from_obsidian_bricks", DecorativeBlocks.SMOOTH_OBSIDIAN.toStack(), Ingredient.of(DecorativeBlocks.OBSIDIAN_BRICKS.FULL));

        for (DecoBlockSet blockSet : DecoBlockSet.DECO_BLOCK_SETS) {
            if (!blockSet.stonecutting) continue;
            Ingredient full = Ingredient.of(blockSet.FULL.get());
            stonecutting(writer, blockSet.STAIRS.toStack(), full);
            stonecutting(writer, blockSet.SLAB.toStack(2), full);
            stonecutting(writer, blockSet.WALL.toStack(), full);

            for (ObjectIntPair<Supplier<? extends ItemLike>> material : blockSet.materials) {
                Ingredient ingredient = Ingredient.of(material.left().get().asItem().getDefaultInstance());
                int amount = material.rightInt();
                stonecutting(writer, blockSet.FULL.toStack(amount), ingredient);
                stonecutting(writer, blockSet.STAIRS.toStack(amount), ingredient);
                stonecutting(writer, blockSet.SLAB.toStack(amount * 2), ingredient);
                stonecutting(writer, blockSet.WALL.toStack(amount), ingredient);
            }
        }
    }

    protected void stonecutting(Consumer<FinishedRecipe> writer, String suffix, ItemStack result, Ingredient ingredient) {
        ResourceLocation id = Confluence.asResource("stonecutting/" + getItemName(result.getItem()) + suffix);
        Advancement.Builder advancement = ModRecipeProvider.createAdvancementBuilder(id, ingredient);
        writer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.add("ingredient", ingredient.toJson());
                json.addProperty("result", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
                json.addProperty("count", result.getCount());
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return RecipeSerializer.STONECUTTER;
            }

            @Override
            public JsonObject serializeAdvancement() {
                return advancement.serializeToJson();
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return id.withPrefix("recipes/confluence/");
            }
        });
    }

    protected void stonecutting(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient ingredient) {
        stonecutting(writer, "", result, ingredient);
    }
}
