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
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.DecorativeBlocks;
import org.confluence.mod.common.init.block.StatueBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEWhipItems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeavyWorkBenchProvider extends AbstractRecipeProvider {
    public HeavyWorkBenchProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }
    private static class HookRecipeData {
        Ingredient material1;
        Ingredient material2;
        ItemLike resultItem;

        public HookRecipeData(Ingredient material1, Ingredient material2, ItemLike resultItem) {
            this.material1 = material1;
            this.material2 = material2;
            this.resultItem = resultItem;
        }
    }
    private static class WhipRecipeData {
        Ingredient material1;
        Ingredient material2;
        ItemLike resultItem;

        public WhipRecipeData(Ingredient material1, Ingredient material2, ItemLike resultItem) {
            this.material1 = material1;
            this.material2 = material2;
            this.resultItem = resultItem;
        }
    }
    private static class StaffRecipeData {
        Ingredient material1;
        Ingredient material2;
        ItemLike resultItem;

        public StaffRecipeData(Ingredient material1, Ingredient material2, ItemLike resultItem) {
            this.material1 = material1;
            this.material2 = material2;
            this.resultItem = resultItem;
        }
    }
    private static class LightSaberRecipeData {
        Ingredient material1;
        Ingredient material2;
        ItemLike resultItem;

        public LightSaberRecipeData(Ingredient material1, Ingredient material2, ItemLike resultItem) {
            this.material1 = material1;
            this.material2 = material2;
            this.resultItem = resultItem;
        }
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        List<String> statuePattern = List.of(
                " ## ",
                " ## ",
                " ## ",
                "####"
        );
        Map<Character, Ingredient> statueIngredientMap = Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS)
        );
        ItemLike[] statueItems = {
                StatueBlocks.A_STATUE.get(),
                StatueBlocks.B_STATUE.get(),
                StatueBlocks.C_STATUE.get(),
                StatueBlocks.D_STATUE.get(),
                StatueBlocks.E_STATUE.get(),
                StatueBlocks.F_STATUE.get(),
                StatueBlocks.G_STATUE.get(),
                StatueBlocks.H_STATUE.get(),
                StatueBlocks.I_STATUE.get(),
                StatueBlocks.J_STATUE.get(),
                StatueBlocks.K_STATUE.get(),
                StatueBlocks.L_STATUE.get(),
                StatueBlocks.M_STATUE.get(),
                StatueBlocks.N_STATUE.get(),
                StatueBlocks.O_STATUE.get(),
                StatueBlocks.P_STATUE.get(),
                StatueBlocks.Q_STATUE.get(),
                StatueBlocks.R_STATUE.get(),
                StatueBlocks.S_STATUE.get(),
                StatueBlocks.T_STATUE.get(),
                StatueBlocks.U_STATUE.get(),
                StatueBlocks.V_STATUE.get(),
                StatueBlocks.W_STATUE.get(),
                StatueBlocks.X_STATUE.get(),
                StatueBlocks.Y_STATUE.get(),
                StatueBlocks.Z_STATUE.get(),
                StatueBlocks.N0_STATUE.get(),
                StatueBlocks.N1_STATUE.get(),
                StatueBlocks.N2_STATUE.get(),
                StatueBlocks.N3_STATUE.get(),
                StatueBlocks.N4_STATUE.get(),
                StatueBlocks.N5_STATUE.get(),
                StatueBlocks.N6_STATUE.get(),
                StatueBlocks.N7_STATUE.get(),
                StatueBlocks.N8_STATUE.get(),
                StatueBlocks.N9_STATUE.get(),
                StatueBlocks.PERIOD_STATUE.get(),
                StatueBlocks.EXCLAMATION_MARK_STATUE.get(),
                StatueBlocks.QUESTION_MARK_STATUE.get()
        };
        // 雕像
        for (ItemLike statueItem : statueItems) {
            heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(statueIngredientMap, statuePattern), new ItemStack(statueItem.asItem()));
        }

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
        List<String> hookPattern = List.of(
                "  #",
                " a ",
                "a  "
        );
        generateRecipes(recipeOutput, hookPattern,
                new RecipeData<>(Ingredient.of(MaterialItems.HOOK.get()), Ingredient.of(Items.CHAIN), HookItems.GRAPPLING_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.RUBY_BLOCK.get()), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), HookItems.RUBY_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.AMBER_BLOCK.get()), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), HookItems.AMBER_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.TOPAZ_BLOCK.get()), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), HookItems.TOPAZ_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.TR_EMERALD_BLOCK.get()), Ingredient.of(DecorativeBlocks.EMERALD_CHAIN), HookItems.EMERALD_HOOK.get()),
                new RecipeData<>(Ingredient.of(Items.DIAMOND_BLOCK), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), HookItems.DIAMOND_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK.get()), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), HookItems.SAPPHIRE_HOOK.get()),
                new RecipeData<>(Ingredient.of(DecorativeBlocks.TR_AMETHYST_BLOCK.get()), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), HookItems.AMETHYST_HOOK.get())
        );

        // 基础鞭子
        List<String> whipPattern = List.of(
                "  a ",
                " a a",
                " # a",
                "#   "
        );
        generateRecipes(recipeOutput, whipPattern,
                new RecipeData<>(AmountIngredient.of(4, Items.BAMBOO), Ingredient.of(Items.BAMBOO_FENCE), TEWhipItems.SLUB_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, Tags.Items.INGOTS_GOLD), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), TEWhipItems.RUBY_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, MaterialItems.STURDY_FOSSIL), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), TEWhipItems.AMBER_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, ModTags.Items.INGOTS_TIN), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), TEWhipItems.TOPAZ_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, ModTags.Items.INGOTS_TUNGSTEN), Ingredient.of(DecorativeBlocks.EMERALD_CHAIN), TEWhipItems.EMERALD_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, ModTags.Items.INGOTS_PLATINUM), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), TEWhipItems.DIAMOND_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, ModTags.Items.INGOTS_SILVER), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), TEWhipItems.SAPPHIRE_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, Tags.Items.INGOTS_COPPER), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), TEWhipItems.AMETHYST_WHIP.get()),
                new RecipeData<>(AmountIngredient.of(4, MaterialItems.SPORE_ROOT), Ingredient.of(MaterialItems.GELSTONE), TEWhipItems.SWAMP_WHIP.get())
        );
        // 基础法杖
        List<String> staffPattern = List.of(
                "  a#",
                "  aa",
                " a  ",
                "a   "
        );
        generateRecipes(recipeOutput, staffPattern,
                new RecipeData<>(AmountIngredient.of(5, Items.DIAMOND), AmountIngredient.of(2, ModTags.Items.INGOTS_PLATINUM), ManaWeaponItems.DIAMOND_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.RUBY), AmountIngredient.of(2, Tags.Items.INGOTS_GOLD), ManaWeaponItems.RUBY_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.AMBER), AmountIngredient.of(2, MaterialItems.STURDY_FOSSIL), ManaWeaponItems.AMBER_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TOPAZ), AmountIngredient.of(2, ModTags.Items.INGOTS_TIN), ManaWeaponItems.TOPAZ_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TR_EMERALD), AmountIngredient.of(2, ModTags.Items.INGOTS_TUNGSTEN), ManaWeaponItems.EMERALD_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.SAPPHIRE), AmountIngredient.of(2, ModTags.Items.INGOTS_SILVER), ManaWeaponItems.SAPPHIRE_STAFF.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TR_AMETHYST), AmountIngredient.of(2, Tags.Items.INGOTS_COPPER), ManaWeaponItems.AMETHYST_STAFF.get())
        );
        // 基础光剑
        List<String> lightSaberPattern = List.of(
                "   #",
                "  # ",
                "aa  ",
                "a   "
        );
        generateRecipes(recipeOutput, lightSaberPattern,
                new RecipeData<>(AmountIngredient.of(5, Items.DIAMOND), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.WHITE_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.RUBY), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.RED_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.AMBER), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.ORANGE_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TOPAZ), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.YELLOW_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TR_EMERALD), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.GREEN_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.SAPPHIRE), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.BLUE_LIGHT_SABER.get()),
                new RecipeData<>(AmountIngredient.of(5, MaterialItems.TR_AMETHYST), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.PURPLE_LIGHT_SABER.get())
        );
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
        // 星星炮
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(4,MaterialItems.METEORITE_INGOT),
                'A', Ingredient.of(TGItems.MINISHARK),
                '/', AmountIngredient.of(5,MaterialItems.FALLING_STAR)
        ), List.of(
                " ## ",
                "# A/",
                " ## "
        )), GunItems.STAR_CANNON.toStack());
    }

    protected void heavyWorkBench(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }

    private static class RecipeData<T extends ItemLike> {
        Ingredient material1;
        Ingredient material2;
        T resultItem;

        public RecipeData(Ingredient material1, Ingredient material2, T resultItem) {
            this.material1 = material1;
            this.material2 = material2;
            this.resultItem = resultItem;
        }
    }

    private <T extends ItemLike> void generateRecipes(RecipeOutput recipeOutput, List<String> pattern, RecipeData<T>... recipeDataList) {
        for (RecipeData<T> data : recipeDataList) {
            Map<Character, Ingredient> ingredientMap = Map.of(
                    '#', data.material1,
                    'a', data.material2
            );
            heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(ingredientMap, pattern), new ItemStack(data.resultItem.asItem()));
        }
    }
}
