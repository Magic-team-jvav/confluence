package org.confluence.mod.common.data.gen.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_furniture.common.init.TFBlocks;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeavyWorkBenchProvider extends AbstractRecipeProvider {
    public HeavyWorkBenchProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        List<String> statuePattern = List.of(
                " ## ",
                " ## ",
                " ## ",
                "####"
        );
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS)
        ), statuePattern), StatueBlocks.A_STATUE.toStack());
        // 玻璃窑
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.LEAD_AND_IRON),
                'a', Ingredient.of(Items.TORCH)
        ), List.of(
                " ## ",
                "#  #",
                "#  #",
                "#aa#"
        )), TFBlocks.GLASS_KILN.toStack());
        // 基础钩爪
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(MaterialItems.HOOK.get()),
                'a', Ingredient.of(Items.CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.GRAPPLING_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.RUBY_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.RUBY_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.RUBY_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.AMBER_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.AMBER_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.AMBER_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.TOPAZ_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.TOPAZ_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.TR_EMERALD_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.EMERALD_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.EMERALD_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.DIAMOND_BLOCK),
                'a', Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.DIAMOND_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.SAPPHIRE_HOOK.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(DecorativeBlocks.TR_AMETHYST_BLOCK.get()),
                'a', Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN)
        ), List.of(
                "  #",
                " a ",
                "a  "
        )), HookItems.AMETHYST_HOOK.toStack());
        // 金系列
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "#a#",
                "a a"
        )), ArmorItems.GOLDEN_HELMET.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "#a#",
                "# #",
                "a a"
        )), ArmorItems.GOLDEN_LEGGINGS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.GOLDEN_BOOTS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "a a",
                "#a#",
                "###"
        )), ArmorItems.GOLDEN_CHESTPLATE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.GOLDEN_BOARD_SWORD.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                " / ",
                " / "
        )), PickaxeItems.GOLDEN_PICKAXE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "##",
                "#/",
                " /"
        )), AxeItems.GOLDEN_AXE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "/",
                "/"
        )), ShovelItems.GOLDEN_SHOVEL.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2,Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "##",
                " /",
                " /"
        )), HoeItems.GOLDEN_HOE.toStack());
    }

    protected void heavyWorkBench(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }
}
