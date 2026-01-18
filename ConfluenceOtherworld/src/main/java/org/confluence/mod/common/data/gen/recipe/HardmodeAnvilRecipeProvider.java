package org.confluence.mod.common.data.gen.recipe;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HardmodeAnvilRecipe;
import org.confluence.terra_guns.common.init.TGItems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HardmodeAnvilRecipeProvider extends AbstractRecipeProvider {
    public HardmodeAnvilRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {

        hardmodeAnvil(recipeOutput, DrillItems.DRAX.toStack(), ShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(3, MaterialItems.HALLOWED_INGOT),
                'F', Ingredient.of(MaterialItems.SOUL_OF_FRIGHT),
                'M', Ingredient.of(MaterialItems.SOUL_OF_MIGHT),
                'S', Ingredient.of(MaterialItems.SOUL_OF_SIGHT)
        ), List.of(
                "HHF ",
                "H HM",
                "HHS "
        )));
        hardmodeAnvil(recipeOutput, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.toStack(), AmountIngredient.of(18, MaterialItems.CHLOROPHYTE_INGOT), Ingredient.of(FunctionalBlocks.EXTRACTINATOR));
        hardmodeAnvil(recipeOutput, FunctionalBlocks.TITANIUM_FORGE.toStack(), ShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(5, MaterialItems.RAW_TITANIUM),
                'F', Ingredient.of(FunctionalBlocks.HELLFORGE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                " H ",
                "HFH",
                "HHH",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, FunctionalBlocks.ADAMANTITE_FORGE.toStack(), ShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(5, MaterialItems.RAW_ADAMANTITE),
                'F', Ingredient.of(FunctionalBlocks.HELLFORGE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                " H ",
                "HFH",
                "HHH",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, TGItems.CHLOROPHYTE_BULLET.toStack(60), AmountIngredient.of(60, TGItems.MUSKET_BULLET), Ingredient.of(MaterialItems.CHLOROPHYTE_INGOT));
        hardmodeAnvil(recipeOutput, TGItems.CRYSTAL_BULLET.toStack(100), AmountIngredient.of(100, TGItems.MUSKET_BULLET), Ingredient.of(MaterialItems.CRYSTAL_SHARDS));
        hardmodeAnvil(recipeOutput, TGItems.ICHOR_BULLET.toStack(150), AmountIngredient.of(150, TGItems.MUSKET_BULLET), Ingredient.of(MaterialItems.ICHOR));
        hardmodeAnvil(recipeOutput, TGItems.CURSED_BULLET.toStack(150), AmountIngredient.of(150, TGItems.MUSKET_BULLET), Ingredient.of(MaterialItems.CURSED_FLAME));
        // 秘银套
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_HAT.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "bab",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_HELMET.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "a a",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_HOOD.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                " a ",
                "aaa",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_CHESTPLATE.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a a",
                "bab",
                "bab",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_LEGGINGS.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a#a",
                "# #",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.MYTHRIL_BOOTS.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a a",
                "# #",
                "ppp"
        )));

        // 秘银武器工具
        hardmodeAnvil(recipeOutput, SwordItems.MYTHRIL_SWORD.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   a",
                "  a ",
                "a/a ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, SpearItems.MYTHRIL_HALBERD.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  ab",
                " /  ",
                "a   "
        )));
        hardmodeAnvil(recipeOutput, PickaxeItems.MYTHRIL_PICKAXE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                "  /b",
                " / b",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, AxeItems.MYTHRIL_WARAXE.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, DrillItems.MYTHRIL_DRILL.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "##a ",
                "# #a",
                "##a ",
                "pppp"
        )));
        hardmodeAnvil(recipeOutput, ChainsawItems.MYTHRIL_CHAINSAW.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "##a ",
                "##aa",
                "pppp"
        )));

        // 山铜套
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_HEADGEAR.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "bab",
                "a a",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_HELMET.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "b b",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_MASK.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "#a#",
                " a ",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_CHESTPLATE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "b b",
                "bbb",
                "bbb",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_LEGGINGS.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "# #",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ORICHALCUM_BOOTS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a a",
                "a a",
                "ppp"
        )));

        // 山铜武器工具
        hardmodeAnvil(recipeOutput, SwordItems.ORICHALCUM_SWORD.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   a",
                "  aa",
                "a/a ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, SpearItems.ORICHALCUM_HALBERD.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  /a",
                " a a",
                "b   "
        )));
        hardmodeAnvil(recipeOutput, PickaxeItems.ORICHALCUM_PICKAXE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bcc",
                "  /c",
                " / b",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, AxeItems.ORICHALCUM_WARAXE.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, DrillItems.ORICHALCUM_DRILL.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "##a ",
                "# #a",
                "##a ",
                "pppp"
        )));
        hardmodeAnvil(recipeOutput, ChainsawItems.ORICHALCUM_CHAINSAW.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())

        ), List.of(
                "#aa ",
                "#aaa",
                "pppp"
        )));

        // 钛金套
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_HEADGEAR.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aba",
                "b b",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_HELMET.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "bbb",
                "a a",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_MASK.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "b#b",
                "#a#",
                " a ",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_CHESTPLATE.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "c c",
                "bcb",
                "bab",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_LEGGINGS.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "a a",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.TITANIUM_BOOTS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a a",
                "a a",
                "ppp"
        )));

        // 钛金武器工具
        hardmodeAnvil(recipeOutput, SwordItems.TITANIUM_SWORD.toStack(), ShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  cc",
                " cac",
                "a/c ",
                "/a  "
        )));
        hardmodeAnvil(recipeOutput, SpearItems.TITANIUM_TRIDENT.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  #b",
                " #b#",
                " /# ",
                "b   "
        )));
        hardmodeAnvil(recipeOutput, PickaxeItems.TITANIUM_PICKAXE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(4, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                "  /b",
                " / b",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, AxeItems.TITANIUM_WARAXE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'c', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbc",
                " b/b",
                " /b ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, DrillItems.TITANIUM_DRILL.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aab ",
                "# ab",
                "aab ",
                "pppp"
        )));
        hardmodeAnvil(recipeOutput, ChainsawItems.TITANIUM_CHAINSAW.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa ",
                "#aaa",
                "pppp"
        )));

        // 精金套
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_HEADGEAR.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "bab",
                "a a",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_HELMET.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "b b",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_MASK.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "#a#",
                " a ",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_CHESTPLATE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "b b",
                "bbb",
                "bbb",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_LEGGINGS.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "aaa",
                "# #",
                "# #",
                "ppp"
        )));
        hardmodeAnvil(recipeOutput, ArmorItems.ADAMANTITE_BOOTS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "a a",
                "a a",
                "ppp"
        )));

        // 精金武器工具
        hardmodeAnvil(recipeOutput, SwordItems.ADAMANTITE_SWORD.toStack(), ShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  c ",
                " cac",
                "aac ",
                "/a  "
        )));
        hardmodeAnvil(recipeOutput, SpearItems.ADAMANTITE_GLAIVE.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  bb",
                " aa ",
                " /  ",
                "a   "
        )));
        hardmodeAnvil(recipeOutput, PickaxeItems.ADAMANTITE_PICKAXE.toStack(), ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bcc",
                "  /c",
                " / b",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, AxeItems.ADAMANTITE_WARAXE.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(recipeOutput, DrillItems.ADAMANTITE_DRILL.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "##a ",
                "# #a",
                "##a ",
                "pppp"
        )));
        hardmodeAnvil(recipeOutput, ChainsawItems.ADAMANTITE_CHAINSAW.toStack(), ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "#aa ",
                "#aaa",
                "pppp"
        )));
        // 矿车升级包
        hardmodeAnvil(recipeOutput, ConsumableItems.MINECART_UPGRADE_KIT.toStack(), ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(MaterialItems.MECHANICAL_WHEEL_PIECE),
                'b', Ingredient.of(MaterialItems.MECHANICAL_WAGON_PIECE),
                'c', Ingredient.of(MaterialItems.MECHANICAL_BATTERY_PIECE)
        ), List.of(
                "abc"
        )));
        // 翅膀
        hardmodeAnvil(recipeOutput, AccessoryItems.FAIRY_WINGS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(14, MaterialItems.PIXIE_DUST),
                'c', AmountIngredient.of(8, MaterialItems.PIXIE_DUST)
        ), List.of(
                "b  b",
                "baab",
                "b  b",
                "c  c"
        )));
        hardmodeAnvil(recipeOutput, AccessoryItems.HARPY_WINGS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', Ingredient.of(MaterialItems.GIANT_HARPY_FEATHER)
        ), List.of(
                "aba"
        )));
        hardmodeAnvil(recipeOutput, AccessoryItems.ANGEL_WINGS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(4, MaterialItems.SOUL_OF_LIGHT),
                'c', AmountIngredient.of(5, MaterialItems.HARPY_FEATHER)
        ), List.of(
                "b  b",
                "caac",
                "b  b"
        )));
        hardmodeAnvil(recipeOutput, AccessoryItems.DEMON_WINGS.toStack(), ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(4, MaterialItems.SOUL_OF_NIGHT),
                'c', AmountIngredient.of(5, MaterialItems.HARPY_FEATHER)
        ), List.of(
                "b  b",
                "caac",
                "b  b"
        )));
    }

    protected void hardmodeAnvil(RecipeOutput recipeOutput, ItemStack result, ShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HardmodeAnvilRecipe(result, Either.left(pattern)), null);
    }

    protected void hardmodeAnvil(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HardmodeAnvilRecipe(result, Either.right(zingredients)), null);
    }
}
