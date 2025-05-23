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
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeavyWorkBenchProvider extends AbstractRecipeProvider {
    private final List<String> baseHookPattern = List.of(
            "  #",
            " a ",
            "a  "
    );
    private final List<@NotNull String> baseWhipPattern = List.of(
            "  a ",
            " a a",
            " # a",
            "#   "
    );
    private final List<String> baseStaffPattern = List.of(
            "  a#",
            "  aa",
            " a  ",
            "a   "
    );
    private final List<String> baseLightSaberPattern = List.of(
            "   #",
            "  # ",
            "aa  ",
            "a   "
    );

    public HeavyWorkBenchProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        buildBaseStatues(recipeOutput);

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
        baseHook(recipeOutput, Ingredient.of(MaterialItems.HOOK), Ingredient.of(Items.CHAIN), HookItems.GRAPPLING_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.RUBY_BLOCK), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), HookItems.RUBY_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.AMBER_BLOCK), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), HookItems.AMBER_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.TOPAZ_BLOCK), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), HookItems.TOPAZ_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.TR_EMERALD_BLOCK), Ingredient.of(DecorativeBlocks.EMERALD_CHAIN), HookItems.EMERALD_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(Items.DIAMOND_BLOCK), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), HookItems.DIAMOND_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), HookItems.SAPPHIRE_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.TR_AMETHYST_BLOCK), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), HookItems.AMETHYST_HOOK.toStack());

        // 基础鞭子
        baseWhip(recipeOutput, AmountIngredient.of(4, Items.BAMBOO), Ingredient.of(Items.BAMBOO_FENCE), TEWhipItems.SLUB_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_GOLD), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), TEWhipItems.RUBY_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.STURDY_FOSSIL), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), TEWhipItems.AMBER_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TIN), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), TEWhipItems.TOPAZ_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TUNGSTEN), Ingredient.of(DecorativeBlocks.EMERALD_CHAIN), TEWhipItems.EMERALD_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_PLATINUM), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), TEWhipItems.DIAMOND_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_SILVER), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), TEWhipItems.SAPPHIRE_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_COPPER), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), TEWhipItems.AMETHYST_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.SPORE_ROOT), Ingredient.of(MaterialItems.GELSTONE), TEWhipItems.SWAMP_WHIP.toStack());

        // 基础法杖
        baseStaff(recipeOutput, AmountIngredient.of(5, Items.DIAMOND), AmountIngredient.of(2, ModTags.Items.INGOTS_PLATINUM), ManaWeaponItems.DIAMOND_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.RUBY), AmountIngredient.of(2, Tags.Items.INGOTS_GOLD), ManaWeaponItems.RUBY_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.AMBER), AmountIngredient.of(2, MaterialItems.STURDY_FOSSIL), ManaWeaponItems.AMBER_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.TOPAZ), AmountIngredient.of(2, ModTags.Items.INGOTS_TIN), ManaWeaponItems.TOPAZ_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.TR_EMERALD), AmountIngredient.of(2, ModTags.Items.INGOTS_TUNGSTEN), ManaWeaponItems.EMERALD_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.SAPPHIRE), AmountIngredient.of(2, ModTags.Items.INGOTS_SILVER), ManaWeaponItems.SAPPHIRE_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, MaterialItems.TR_AMETHYST), AmountIngredient.of(2, Tags.Items.INGOTS_COPPER), ManaWeaponItems.AMETHYST_STAFF.toStack());

        // 基础光剑
        baseLightSaber(recipeOutput, AmountIngredient.of(5, Items.DIAMOND), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.WHITE_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.RUBY), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.RED_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.AMBER), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.ORANGE_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.TOPAZ), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.YELLOW_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.TR_EMERALD), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.GREEN_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.SAPPHIRE), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.BLUE_LIGHT_SABER.toStack());
        baseLightSaber(recipeOutput, AmountIngredient.of(5, MaterialItems.TR_AMETHYST), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.PURPLE_LIGHT_SABER.toStack());

        // 金系列
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "#a#",
                "a a"
        )), ArmorItems.GOLDEN_HELMET.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "#a#",
                "# #",
                "a a"
        )), ArmorItems.GOLDEN_LEGGINGS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.GOLDEN_BOOTS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                'a', Ingredient.of(Items.GOLD_INGOT)
        ), List.of(
                "a a",
                "#a#",
                "###"
        )), ArmorItems.GOLDEN_CHESTPLATE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "#",
                "/"
        )), SwordItems.GOLDEN_BOARD_SWORD.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "###",
                " / ",
                " / "
        )), PickaxeItems.GOLDEN_PICKAXE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "##",
                "#/",
                " /"
        )), AxeItems.GOLDEN_AXE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "#",
                "/",
                "/"
        )), ShovelItems.GOLDEN_SHOVEL.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        ), List.of(
                "##",
                " /",
                " /"
        )), HoeItems.GOLDEN_HOE.toStack());
        // 星星炮
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(4, MaterialItems.METEORITE_INGOT),
                'A', Ingredient.of(TGItems.MINISHARK),
                '/', AmountIngredient.of(5, MaterialItems.FALLING_STAR)
        ), List.of(
                " ## ",
                "# A/",
                " ## "
        )), GunItems.STAR_CANNON.toStack());
        // 死灵套装
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(10, Items.COBWEB)
        ), List.of(
                "/A/",
                "A A"
        )), ArmorItems.NECRO_HELMET.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/ /",
                "/A/",
                "AAA"
        )), ArmorItems.NECRO_CHESTPLATE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/A/",
                "/ /",
                "A A"
        )), ArmorItems.NECRO_LEGGINGS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(10, Items.COBWEB)
        ), List.of(
                "/ /",
                "A A"
        )), ArmorItems.NECRO_BOOTS.toStack());
        // 丛林套装
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE)
        ), List.of(
                "AAA",
                "A A"
        )), ArmorItems.JUNGLE_HELMET.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE),
                '/', AmountIngredient.of(2, MaterialItems.STINGER)
        ), List.of(
                "A A",
                "/A/",
                "/A/"
        )), ArmorItems.JUNGLE_CHESTPLATE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.STINGER),
                'b', Ingredient.of(MaterialItems.MAN_EATER_VINE)
        ), List.of(
                "bAb",
                "/ /",
                "A A"
        )), ArmorItems.JUNGLE_LEGGINGS.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.STINGER)
        ), List.of(
                "/ /",
                "A A"
        )), ArmorItems.JUNGLE_BOOTS.toStack());
        // 草剑
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(7, MaterialItems.JUNGLE_SPORE),
                'b', Ingredient.of(MaterialItems.MAN_EATER_VINE),
                'c', AmountIngredient.of(4, MaterialItems.STINGER)
        ), List.of(
                "   c",
                " cb ",
                "abc ",
                "ba  "
        )), SwordItems.BLADE_OF_GRASS.toStack());
        // 邪恶工具
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4, MaterialItems.DEMONITE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.ROTTEN_BONE)
        ), List.of(
                "a",
                "/",
                "/"
        )), ShovelItems.SHADOW_SHOVEL.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4, MaterialItems.TR_CRIMSON_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.VERTEBRA)
        ), List.of(
                "a",
                "/",
                "/"
        )), ShovelItems.MINER.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.DEMONITE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.ROTTEN_BONE)
        ), List.of(
                "aa",
                " /",
                " /"
        )), HoeItems.SHADOW_HOE.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.TR_CRIMSON_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.VERTEBRA)
        ), List.of(
                "aa",
                " /",
                " /"
        )), HoeItems.CULTIVATOR.toStack());
        // 锄锹
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.METEORITE_INGOT),
                '/', Ingredient.of(MaterialItems.METEORITE_INGOT)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )), HoeShovelItems.METEOR_HOE_SHOVEL.toStack());
        heavyWorkBench(recipeOutput, "", ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT),
                '/', Ingredient.of(Items.BLAZE_ROD)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )), HoeShovelItems.MOLTEN_HOE_SHOVEL.toStack());
    }

    private void buildBaseStatues(RecipeOutput recipeOutput) {
        ShapedRecipePattern baseStatuePattern = ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS)
        ), List.of(
                " ## ",
                " ## ",
                " ## ",
                "####"
        ));
        ItemLike[] statueItems = {
                StatueBlocks.A_STATUE, StatueBlocks.B_STATUE, StatueBlocks.C_STATUE, StatueBlocks.D_STATUE, StatueBlocks.E_STATUE, StatueBlocks.F_STATUE, StatueBlocks.G_STATUE, StatueBlocks.H_STATUE, StatueBlocks.I_STATUE, StatueBlocks.J_STATUE, StatueBlocks.K_STATUE, StatueBlocks.L_STATUE, StatueBlocks.M_STATUE, StatueBlocks.N_STATUE, StatueBlocks.O_STATUE, StatueBlocks.P_STATUE, StatueBlocks.Q_STATUE, StatueBlocks.R_STATUE, StatueBlocks.S_STATUE, StatueBlocks.T_STATUE, StatueBlocks.U_STATUE, StatueBlocks.V_STATUE, StatueBlocks.W_STATUE, StatueBlocks.X_STATUE, StatueBlocks.Y_STATUE, StatueBlocks.Z_STATUE,
                StatueBlocks.N0_STATUE, StatueBlocks.N1_STATUE, StatueBlocks.N2_STATUE, StatueBlocks.N3_STATUE, StatueBlocks.N4_STATUE, StatueBlocks.N5_STATUE, StatueBlocks.N6_STATUE, StatueBlocks.N7_STATUE, StatueBlocks.N8_STATUE, StatueBlocks.N9_STATUE,
                StatueBlocks.PERIOD_STATUE,
                StatueBlocks.EXCLAMATION_MARK_STATUE,
                StatueBlocks.QUESTION_MARK_STATUE
        };
        // 雕像
        for (ItemLike statueItem : statueItems) {
            heavyWorkBench(recipeOutput, "", baseStatuePattern, statueItem.asItem().getDefaultInstance());
        }
    }

    protected void heavyWorkBench(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }

    protected void baseHook(RecipeOutput recipeOutput, Ingredient hook, Ingredient chain, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', hook,
                'a', chain
        ), baseHookPattern)), null);
    }

    protected void baseWhip(RecipeOutput recipeOutput, Ingredient handle, Ingredient strip, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', handle,
                'a', strip
        ), baseWhipPattern)), null);
    }

    protected void baseStaff(RecipeOutput recipeOutput, Ingredient gem, Ingredient handle, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', gem,
                'a', handle
        ), baseStaffPattern)), null);
    }

    protected void baseLightSaber(RecipeOutput recipeOutput, Ingredient gem, Ingredient handle, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', gem,
                'a', handle
        ), baseLightSaberPattern)), null);
    }
}
