package org.confluence.mod.common.data.gen.recipe;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.SimpleFinishedRecipe;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.block.ModBlocks;
import org.confluence.mod.common.init.block.NatureBlocks;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HardmodeAnvilRecipe;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HardmodeAnvilRecipeProvider extends AbstractRecipeProvider {
    public HardmodeAnvilRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer) {

        hardmodeAnvil(writer, DrillItems.DRAX.toStack(), PortShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(3, MaterialItems.HALLOWED_INGOT),
                'F', Ingredient.of(MaterialItems.SOUL_OF_FRIGHT),
                'M', Ingredient.of(MaterialItems.SOUL_OF_MIGHT),
                'S', Ingredient.of(MaterialItems.SOUL_OF_SIGHT)
        ), List.of(
                "HHF ",
                "H HM",
                "HHS "
        )));
        hardmodeAnvil(writer, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.toStack(), AmountIngredient.of(18, MaterialItems.CHLOROPHYTE_INGOT), Ingredient.of(FunctionalBlocks.EXTRACTINATOR));
        hardmodeAnvil(writer, FunctionalBlocks.TITANIUM_FORGE.toStack(), PortShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(5, MaterialItems.RAW_TITANIUM),
                'F', Ingredient.of(FunctionalBlocks.HELLFORGE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                " H ",
                "HFH",
                "HHH",
                "ppp"
        )));
        hardmodeAnvil(writer, FunctionalBlocks.ADAMANTITE_FORGE.toStack(), PortShapedRecipePattern.of(Map.of(
                'H', AmountIngredient.of(5, MaterialItems.RAW_ADAMANTITE),
                'F', Ingredient.of(FunctionalBlocks.HELLFORGE),
                'p', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                " H ",
                "HFH",
                "HHH",
                "ppp"
        )));
        hardmodeAnvil(writer, GunItems.CHLOROPHYTE_BULLET.toStack(60), AmountIngredient.of(60, GunItems.MUSKET_BULLET), Ingredient.of(MaterialItems.CHLOROPHYTE_INGOT));
        hardmodeAnvil(writer, GunItems.CRYSTAL_BULLET.toStack(100), AmountIngredient.of(100, GunItems.MUSKET_BULLET), Ingredient.of(MaterialItems.CRYSTAL_SHARDS));
        hardmodeAnvil(writer, GunItems.ICHOR_BULLET.toStack(150), AmountIngredient.of(150, GunItems.MUSKET_BULLET), Ingredient.of(MaterialItems.ICHOR));
        hardmodeAnvil(writer, GunItems.CURSED_BULLET.toStack(150), AmountIngredient.of(150, GunItems.MUSKET_BULLET), Ingredient.of(ModBlocks.CURSED_FLAME));
        // 秘银套
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_HAT.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "bab",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_HELMET.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "aaa",
                "a a"
        )));
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_HOOD.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                " a ",
                "aaa",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_CHESTPLATE.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "a a",
                "bab",
                "bab"
        )));
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_LEGGINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "a#a",
                "# #",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.MYTHRIL_BOOTS.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "a a",
                "# #"
        )));

        // 秘银武器工具
        hardmodeAnvil(writer, SwordItems.MYTHRIL_SWORD.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   a",
                "  a ",
                "a/a ",
                "/   "
        )));
        hardmodeAnvil(writer, SpearItems.MYTHRIL_HALBERD.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  ab",
                " /  ",
                "a   "
        )));
        hardmodeAnvil(writer, PickaxeItems.MYTHRIL_PICKAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                "  /b",
                " / b",
                "/   "
        )));
        hardmodeAnvil(writer, AxeItems.MYTHRIL_WARAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(writer, DrillItems.MYTHRIL_DRILL.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "##a ",
                "# #a",
                "##a "
        )));
        hardmodeAnvil(writer, ChainsawItems.MYTHRIL_CHAINSAW.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "##a ",
                "##aa"
        )));
        hardmodeAnvil(writer, HoeShovelItems.MYTHRIL_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));

        // 山铜套
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_HEADGEAR.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "bab",
                "a a"
        )));
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_HELMET.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "aaa",
                "b b"
        )));
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_MASK.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "aaa",
                "#a#",
                " a "
        )));
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_CHESTPLATE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "b b",
                "bbb",
                "bbb"
        )));
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_LEGGINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "aaa",
                "# #",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.ORICHALCUM_BOOTS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "a a",
                "a a"
        )));

        // 山铜武器工具
        hardmodeAnvil(writer, SwordItems.ORICHALCUM_SWORD.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   a",
                "  aa",
                "a/a ",
                "/   "
        )));
        hardmodeAnvil(writer, SpearItems.ORICHALCUM_HALBERD.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  /a",
                " a a",
                "b   "
        )));
        hardmodeAnvil(writer, PickaxeItems.ORICHALCUM_PICKAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bcc",
                "  /c",
                " / b",
                "/   "
        )));
        hardmodeAnvil(writer, AxeItems.ORICHALCUM_WARAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(writer, DrillItems.ORICHALCUM_DRILL.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "##a ",
                "# #a",
                "##a "
        )));
        hardmodeAnvil(writer, ChainsawItems.ORICHALCUM_CHAINSAW.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "#aa ",
                "#aaa"
        )));
        hardmodeAnvil(writer, HoeShovelItems.ORICHALCUM_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));

        // 钛金套
        hardmodeAnvil(writer, ArmorItems.TITANIUM_HEADGEAR.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "aba",
                "b b"
        )));
        hardmodeAnvil(writer, ArmorItems.TITANIUM_HELMET.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "bbb",
                "a a"
        )));
        hardmodeAnvil(writer, ArmorItems.TITANIUM_MASK.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "b#b",
                "#a#",
                " a "
        )));
        hardmodeAnvil(writer, ArmorItems.TITANIUM_CHESTPLATE.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "c c",
                "bcb",
                "bab"
        )));
        hardmodeAnvil(writer, ArmorItems.TITANIUM_LEGGINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "aaa",
                "a a",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.TITANIUM_BOOTS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "a a",
                "a a"
        )));

        // 钛金武器工具
        hardmodeAnvil(writer, SwordItems.TITANIUM_SWORD.toStack(), PortShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  cc",
                " cac",
                "a/c ",
                "/a  "
        )));
        hardmodeAnvil(writer, SpearItems.TITANIUM_TRIDENT.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  #b",
                " #b#",
                " /# ",
                "b   "
        )));
        hardmodeAnvil(writer, PickaxeItems.TITANIUM_PICKAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(4, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                "  /b",
                " / b",
                "/   "
        )));
        hardmodeAnvil(writer, AxeItems.TITANIUM_WARAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'c', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbc",
                " b/b",
                " /b ",
                "/   "
        )));
        hardmodeAnvil(writer, DrillItems.TITANIUM_DRILL.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "aab ",
                "# ab",
                "aab "
        )));
        hardmodeAnvil(writer, ChainsawItems.TITANIUM_CHAINSAW.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_TITANIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM)
        ), List.of(
                "aaa ",
                "#aaa"
        )));
        hardmodeAnvil(writer, HoeShovelItems.TITANIUM_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_TITANIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));
        // 精金套
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_HEADGEAR.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "bab",
                "a a"
        )));
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_HELMET.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "aaa",
                "b b"
        )));
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_MASK.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "aaa",
                "#a#",
                " a "
        )));
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_CHESTPLATE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "b b",
                "bbb",
                "bbb"
        )));
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_LEGGINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "aaa",
                "# #",
                "# #"
        )));
        hardmodeAnvil(writer, ArmorItems.ADAMANTITE_BOOTS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "a a",
                "a a"
        )));

        // 精金武器工具
        hardmodeAnvil(writer, SwordItems.ADAMANTITE_SWORD.toStack(), PortShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  c ",
                " cac",
                "aac ",
                "/a  "
        )));
        hardmodeAnvil(writer, SpearItems.ADAMANTITE_GLAIVE.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  bb",
                " aa ",
                " /  ",
                "a   "
        )));
        hardmodeAnvil(writer, PickaxeItems.ADAMANTITE_PICKAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bcc",
                "  /c",
                " / b",
                "/   "
        )));
        hardmodeAnvil(writer, AxeItems.ADAMANTITE_WARAXE.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )));
        hardmodeAnvil(writer, DrillItems.ADAMANTITE_DRILL.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "##a ",
                "# #a",
                "##a "
        )));
        hardmodeAnvil(writer, ChainsawItems.ADAMANTITE_CHAINSAW.toStack(), PortShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_ADAMANTITE),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE)
        ), List.of(
                "#aa ",
                "#aaa"
        )));
        hardmodeAnvil(writer, HoeShovelItems.ADAMANTITE_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_ADAMANTITE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));
        // 矿车升级包
        hardmodeAnvil(writer, ConsumableItems.MINECART_UPGRADE_KIT.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(MaterialItems.MECHANICAL_WHEEL_PIECE),
                'b', Ingredient.of(MaterialItems.MECHANICAL_WAGON_PIECE),
                'c', Ingredient.of(MaterialItems.MECHANICAL_BATTERY_PIECE)
        ), List.of(
                "abc"
        )));
        // 翅膀
        hardmodeAnvil(writer, AccessoryItems.FAIRY_WINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(14, MaterialItems.PIXIE_DUST),
                'c', AmountIngredient.of(8, MaterialItems.PIXIE_DUST)
        ), List.of(
                "b  b",
                "baab",
                "b  b",
                "c  c"
        )));
        hardmodeAnvil(writer, AccessoryItems.HARPY_WINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', Ingredient.of(MaterialItems.GIANT_HARPY_FEATHER)
        ), List.of(
                "aba"
        )));
        hardmodeAnvil(writer, AccessoryItems.ANGEL_WINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(4, MaterialItems.SOUL_OF_LIGHT),
                'c', AmountIngredient.of(5, MaterialItems.HARPY_FEATHER)
        ), List.of(
                "b  b",
                "caac",
                "b  b"
        )));
        hardmodeAnvil(writer, AccessoryItems.DEMON_WINGS.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.SOUL_OF_FLIGHT),
                'b', AmountIngredient.of(4, MaterialItems.SOUL_OF_NIGHT),
                'c', AmountIngredient.of(5, MaterialItems.HARPY_FEATHER)
        ), List.of(
                "b  b",
                "caac",
                "b  b"
        )));
        // 连弩
        hardmodeAnvil(writer, CrossbowItems.MYTHRIL_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_MYTHRIL),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        hardmodeAnvil(writer, CrossbowItems.ORICHALCUM_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_ORICHALCUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ORICHALCUM),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        hardmodeAnvil(writer, CrossbowItems.ADAMANTITE_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_ADAMANTITE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_ADAMANTITE),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        hardmodeAnvil(writer, CrossbowItems.TITANIUM_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(7, ModTags.Items.INGOTS_TITANIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_TITANIUM),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        hardmodeAnvil(writer, CrossbowItems.HALLOWED_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_HALLOWED),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_HALLOWED),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        hardmodeAnvil(writer, CrossbowItems.CHLOROPHYTE_REPEATER.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_CHLOROPHYTE),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_CHLOROPHYTE),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )));
        // 神圣
        hardmodeAnvil(writer, HoeShovelItems.HALLOWED_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_HALLOWED),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));
        // 叶绿
        hardmodeAnvil(writer, HoeShovelItems.CHLOROPHYTE_HOE_SHOVEL.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_CHLOROPHYTE),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )));
        // 神圣王冠
        hardmodeAnvil(writer, VanityArmorItems.HALLOWED_CROWN.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_HALLOWED)
        ), List.of(
                " a ",
                "aaa",
                "a a"
        )));
        // 裂天剑
        hardmodeAnvil(writer, ManaWeaponItems.SKY_FRACTURE.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(MaterialItems.LIGHT_SHARD),
                'b', AmountIngredient.of(8, MaterialItems.SOUL_OF_LIGHT),
                'c', Ingredient.of(ManaWeaponItems.MAGIC_MISSILE)
        ), List.of(
                " b ",
                "aca",
                " b "
        )));
        // 彩虹魔杖
        hardmodeAnvil(writer, ManaWeaponItems.RAINBOW_ROD.toStack(), PortShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(10, MaterialItems.PIXIE_DUST),
                'd', AmountIngredient.of(15, MaterialItems.SOUL_OF_SIGHT),
                'c', AmountIngredient.of(10, MaterialItems.CRYSTAL_SHARDS),
                'b', AmountIngredient.of(4, MaterialItems.SOUL_OF_LIGHT),
                'e', AmountIngredient.of(2, MaterialItems.UNICORN_HORN)
        ), List.of(
                " a ",
                "bdb",
                " c ",
                " e "
        )));
    }

    protected void hardmodeAnvil(Consumer<FinishedRecipe> writer, ItemStack result, PortShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new HardmodeAnvilRecipe(result, Either.left(pattern))));
    }

    protected void hardmodeAnvil(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hardmode_anvil/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new HardmodeAnvilRecipe(result, Either.right(zingredients))));
    }
}
