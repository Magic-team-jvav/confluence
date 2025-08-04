package org.confluence.mod.common.data.gen.recipe;

import com.xiaohunao.terra_moment.common.init.TMItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
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
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TESummonItems;
import org.confluence.terraentity.init.item.TEWhipItems;
import org.confluence.terraentity.init.item.TEYoyosItems;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HeavyWorkBenchProvider extends AbstractRecipeProvider {
    private final List<String> baseHookPattern = List.of(
            "aa#"
    );
    private final List<String> baseWhipPattern = List.of(
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
    private final List<String> basePhasebladePattern = List.of(
            "   #",
            "  # ",
            "aa  ",
            "a   "
    );
    private final List<String> baseRobePattern = List.of(
            "bbb",
            "a#a",
            "a a"
    );


    public HeavyWorkBenchProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {
        buildBaseStatues(recipeOutput);

        // 玻璃窑
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
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
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.JADE_BLOCK), Ingredient.of(DecorativeBlocks.JADE_CHAIN), HookItems.JADE_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), HookItems.DIAMOND_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), HookItems.SAPPHIRE_HOOK.toStack());
        baseHook(recipeOutput, Ingredient.of(DecorativeBlocks.AMETHYST_BLOCK), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), HookItems.AMETHYST_HOOK.toStack());

        // 基础鞭子
        baseWhip(recipeOutput, AmountIngredient.of(4, Items.BAMBOO), Ingredient.of(Items.BAMBOO_FENCE), TEWhipItems.SLUB_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_GOLD), Ingredient.of(DecorativeBlocks.RUBY_CHAIN), TEWhipItems.RUBY_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.STURDY_FOSSIL), Ingredient.of(DecorativeBlocks.AMBER_CHAIN), TEWhipItems.AMBER_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TIN), Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN), TEWhipItems.TOPAZ_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_TUNGSTEN), Ingredient.of(DecorativeBlocks.JADE_CHAIN), TEWhipItems.JADE_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_PLATINUM), Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN), TEWhipItems.DIAMOND_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, ModTags.Items.INGOTS_SILVER), Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN), TEWhipItems.SAPPHIRE_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, Tags.Items.INGOTS_COPPER), Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN), TEWhipItems.AMETHYST_WHIP.toStack());
        baseWhip(recipeOutput, AmountIngredient.of(4, MaterialItems.SPORE_ROOT), Ingredient.of(ModTags.Items.RAW_MATERIALS_GELSTONE), TEWhipItems.SWAMP_WHIP.toStack());

        // 基础法杖
        baseStaff(recipeOutput, AmountIngredient.of(5, Tags.Items.GEMS_DIAMOND), AmountIngredient.of(2, ModTags.Items.INGOTS_PLATINUM), ManaWeaponItems.DIAMOND_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_RUBY), AmountIngredient.of(2, Tags.Items.INGOTS_GOLD), ManaWeaponItems.RUBY_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_AMBER), AmountIngredient.of(2, MaterialItems.STURDY_FOSSIL), ManaWeaponItems.AMBER_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_TOPAZ), AmountIngredient.of(2, ModTags.Items.INGOTS_TIN), ManaWeaponItems.TOPAZ_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_JADE), AmountIngredient.of(2, ModTags.Items.INGOTS_TUNGSTEN), ManaWeaponItems.JADE_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_SAPPHIRE), AmountIngredient.of(2, ModTags.Items.INGOTS_SILVER), ManaWeaponItems.SAPPHIRE_STAFF.toStack());
        baseStaff(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_AMETHYST), AmountIngredient.of(2, Tags.Items.INGOTS_COPPER), ManaWeaponItems.AMETHYST_STAFF.toStack());

        // 基础光剑
        basePhaseblade(recipeOutput, AmountIngredient.of(5, Tags.Items.GEMS_DIAMOND), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.WHITE_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_RUBY), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.RED_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_AMBER), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.ORANGE_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_TOPAZ), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.YELLOW_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_JADE), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.GREEN_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_SAPPHIRE), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.BLUE_PHASEBLADE.toStack());
        basePhaseblade(recipeOutput, AmountIngredient.of(5, ModTags.Items.GEMS_AMETHYST), AmountIngredient.of(5, MaterialItems.METEORITE_INGOT), SwordItems.PURPLE_PHASEBLADE.toStack());

        // 基础长袍
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, Tags.Items.GEMS_DIAMOND),Ingredient.of(Tags.Items.GEMS_DIAMOND),ArmorItems.DIAMOND_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_RUBY),Ingredient.of(ModTags.Items.GEMS_RUBY), ArmorItems.RUBY_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMBER),Ingredient.of(ModTags.Items.GEMS_AMBER), ArmorItems.AMBER_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_TOPAZ),Ingredient.of(ModTags.Items.GEMS_TOPAZ), ArmorItems.TOPAZ_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_JADE),Ingredient.of(ModTags.Items.GEMS_JADE), ArmorItems.JADE_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_SAPPHIRE),Ingredient.of(ModTags.Items.GEMS_SAPPHIRE), ArmorItems.SAPPHIRE_ROBE.toStack());
        baseRobe(recipeOutput, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMETHYST),Ingredient.of(ModTags.Items.GEMS_AMETHYST), ArmorItems.AMETHYST_ROBE.toStack());

        // 金系列
        Map<Character, Ingredient> goldWithGems = Map.of(
                '#', Ingredient.of(Tags.Items.GEMS),
                'a', Ingredient.of(Items.GOLD_INGOT)
        );
        shaped(recipeOutput, "golden_helmet_from_reinforce", ShapedRecipePattern.of(goldWithGems, List.of(
                "a#a",
                "a a"
        )), ArmorItems.GOLDEN_HELMET.toStack());
        shaped(recipeOutput, "golden_leggings_from_reinforce", ShapedRecipePattern.of(goldWithGems, List.of(
                "a#a",
                "a a",
                "a a"
        )), ArmorItems.GOLDEN_LEGGINGS.toStack());
        shaped(recipeOutput, "golden_boots_from_reinforce", ShapedRecipePattern.of(goldWithGems, List.of(
                "a a",
                "a#a"
        )), ArmorItems.GOLDEN_BOOTS.toStack());
        shaped(recipeOutput, "golden_chestplate_from_reinforce", ShapedRecipePattern.of(goldWithGems, List.of(
                "a a",
                "a#a",
                "aaa"
        )), ArmorItems.GOLDEN_CHESTPLATE.toStack());
        Map<Character, Ingredient> goldWithGemsAndStick = Map.of(
                '#', Ingredient.of(Tags.Items.GEMS),
                'a', Ingredient.of(Items.GOLD_INGOT),
                '/', Ingredient.of(Items.STICK)
        );
        shaped(recipeOutput, ShapedRecipePattern.of(goldWithGemsAndStick, List.of(
                "a",
                "#",
                "/"
        )), SwordItems.GOLDEN_BROADSWORD.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(goldWithGemsAndStick, List.of(
                "a#a",
                " / ",
                " / "
        )), PickaxeItems.GOLDEN_PICKAXE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(goldWithGemsAndStick, List.of(
                "a#",
                "a/",
                " /"
        )), AxeItems.GOLDEN_AXE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(goldWithGemsAndStick, List.of(
                "a",
                "#",
                "/"
        )), ShovelItems.GOLDEN_SHOVEL.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(goldWithGemsAndStick, List.of(
                "a#",
                " /",
                " /"
        )), HoeItems.GOLDEN_HOE.toStack());
        // 星星炮
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(4, MaterialItems.METEORITE_INGOT),
                'A', Ingredient.of(TGItems.MINISHARK),
                '/', AmountIngredient.of(5, MaterialItems.FALLING_STAR)
        ), List.of(
                " ## ",
                "# A/",
                " ## "
        )), GunItems.STAR_CANNON.toStack());
        // 死灵套装
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(10, Items.COBWEB)
        ), List.of(
                "/A/",
                "A A"
        )), ArmorItems.NECRO_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/ /",
                "/A/",
                "AAA"
        )), ArmorItems.NECRO_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/A/",
                "/ /",
                "A A"
        )), ArmorItems.NECRO_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, MaterialItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(10, Items.COBWEB)
        ), List.of(
                "/ /",
                "A A"
        )), ArmorItems.NECRO_BOOTS.toStack());
        // 丛林套装
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE)
        ), List.of(
                "AAA",
                "A A"
        )), ArmorItems.JUNGLE_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE),
                '/', AmountIngredient.of(2, MaterialItems.STINGER)
        ), List.of(
                "A A",
                "/A/",
                "/A/"
        )), ArmorItems.JUNGLE_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(2, MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.STINGER),
                'b', Ingredient.of(MaterialItems.MAN_EATER_VINE)
        ), List.of(
                "bAb",
                "/ /",
                "A A"
        )), ArmorItems.JUNGLE_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', Ingredient.of(MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.STINGER)
        ), List.of(
                "/ /",
                "A A"
        )), ArmorItems.JUNGLE_BOOTS.toStack());
        // 草剑
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
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
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4, MaterialItems.DEMONITE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.ROTTEN_BONE)
        ), List.of(
                "a",
                "/",
                "/"
        )), ShovelItems.SHADOW_SHOVEL.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4, MaterialItems.CRIMTANE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.VERTEBRA)
        ), List.of(
                "a",
                "/",
                "/"
        )), ShovelItems.MINER.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.DEMONITE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.ROTTEN_BONE)
        ), List.of(
                "aa",
                " /",
                " /"
        )), HoeItems.SHADOW_HOE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.CRIMTANE_INGOT),
                '/', AmountIngredient.of(2, MaterialItems.VERTEBRA)
        ), List.of(
                "aa",
                " /",
                " /"
        )), HoeItems.CULTIVATOR.toStack());
        // 锄锹
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.METEORITE_INGOT),
                '/', Ingredient.of(MaterialItems.METEORITE_INGOT)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )), HoeShovelItems.METEOR_HOE_SHOVEL.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT),
                '/', Ingredient.of(Items.BLAZE_ROD)
        ), List.of(
                " aaa",
                "  /a",
                " /  ",
                "/   "
        )), HoeShovelItems.MOLTEN_HOE_SHOVEL.toStack());
        // 熔岩套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT)
        ), List.of(
                "###",
                "# #"
        )), ArmorItems.MOLTEN_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT),
                'a', Ingredient.of(MaterialItems.HELLSTONE_INGOT)
        ), List.of(
                "###",
                "# #",
                "a a"
        )), ArmorItems.MOLTEN_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.MOLTEN_BOOTS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT),
                'a', Ingredient.of(MaterialItems.HELLSTONE_INGOT)
        ), List.of(
                "# #",
                "#a#",
                "###"
        )), ArmorItems.MOLTEN_CHESTPLATE.toStack());
        // 巨石
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.COBBLESTONE)
        ), List.of(
                " // ",
                "////",
                "////",
                " // "
        )), FunctionalBlocks.NORMAL_BOULDER.toStack());
        // 巨石面包
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.BREAD)
        ), List.of(
                " // ",
                "////",
                "////",
                " // "
        )), FoodItems.BOULDER_BREAD.toStack());
        // 蜜蜂手榴弹
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(ConsumableItems.GRENADE),
                '/', Ingredient.of(MaterialItems.ROYAL_WAX)
        ), List.of(
                "/",
                "a"
        )), ConsumableItems.BEENADE.toStack());
        // 铁傀儡法杖
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(ConsumableItems.LIFE_CRYSTAL),
                'b', Ingredient.of(Items.CARVED_PUMPKIN),
                'c', Ingredient.of(Items.REDSTONE_BLOCK),
                'e', Ingredient.of(ConsumableItems.MANA_CRYSTAL),
                '#', Ingredient.of(Items.IRON_BLOCK)
        ), List.of(
                "ebe",
                "#a#",
                " # ",
                "ccc"
        )), TESummonItems.IRON_GOLEM_STAFF.toStack());
        // 小雪怪法杖
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2,MaterialItems.FLINX_FUR),
                '#', AmountIngredient.of(2,ModTags.Items.GOLD_AND_PLATINUM)
        ), List.of(
                "  aa",
                " a##",
                " # #",
                "#   "
        )), TESummonItems.SNOW_FLINX_STAFF.toStack());

        // 小雪怪皮毛外套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4,MaterialItems.FLINX_FUR),
                'b', AmountIngredient.of(2,MaterialItems.SILK),
                '#', AmountIngredient.of(8,ModTags.Items.GOLD_AND_PLATINUM)
        ), List.of(
                "a#a",
                "bbb",
                "b b"
        )), ArmorItems.FLINX_FUR_COAT.toStack());

        // 计时器
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.GOLD_WATCH,TCItems.PLATINUM_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_1_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.SILVER_WATCH,TCItems.TUNGSTEN_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_3_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.COPPER_WATCH,TCItems.TIN_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_5_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(MaterialItems.TIN_INGOT,Items.COPPER_INGOT),
                'b', Ingredient.of(Items.REDSTONE_BLOCK),
                'c', Ingredient.of(Tags.Items.STONES)
        ), List.of(
                "bbb",
                "cac",
                "ccc"
        )), FunctionalBlocks.SIGNAL_ADAPTER.toStack());
        // 小鬼法杖
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HELLSTONE_INGOT),
                'a', Ingredient.of(OreBlocks.HELLSTONE_BLOCK)
        ), List.of(
                "  # ",
                "  a#",
                " #  ",
                "#   "
        )), TESummonItems.IMP_STAFF.toStack());

        // 长袍
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(3, MaterialItems.SILK),
                'a', AmountIngredient.of(2, MaterialItems.SILK)
        ), List.of(
                "#a#",
                "# #",
                "# #"
        )), VanityArmorItems.ROBE.toStack());

        // 抑郁球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3,ModTags.Items.INGOTS_DEMONITE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.MALAISE.toStack());

        // 血脉球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3,ModTags.Items.INGOTS_CRIMTANE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.ARTERY.toStack());

        // 蜂巢球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3,MaterialItems.ROYAL_WAX),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.HIVE_FIVE.toStack());

        // 亚马逊球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(4,NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.getPlanks()),
                'A', AmountIngredient.of(12,MaterialItems.STINGER),
                'B', AmountIngredient.of(9,MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.MAN_EATER_VINE)
        ), List.of(
                " AS",
                " /S",
                "B  "
        )), TEYoyosItems.AMAZON.toStack());

        // 哥布林战旗
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(3, MaterialItems.TATTERED_CLOTH),
                'b', Ingredient.of(MaterialItems.TATTERED_CLOTH),
                'a', AmountIngredient.of(2, ItemTags.PLANKS)
        ), List.of(
                "###",
                " b ",
                "aaa"
        )), TMItems.GOBLIN_BATTLE_STANDARD.get().getDefaultInstance());

        // 秘银砧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'a', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL)
        ), List.of(
                "###",
                " a ",
                "aaa"
        )), FunctionalBlocks.MYTHRIL_ANVIL.toStack());

        // 山铜砧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'a', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM)
        ), List.of(
                "###",
                " a ",
                "#a#"
        )), FunctionalBlocks.ORICHALCUM_ANVIL.toStack());

        shapeless(recipeOutput, NatureBlocks.THIN_ICE_BLOCK.toStack(), Ingredient.of(Items.ICE));
        shapeless(recipeOutput, ConsumableItems.BONE_THROWING_KNIFE.toStack(), Ingredient.of(ConsumableItems.THROWING_KNIVE), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        shapeless(recipeOutput, ConsumableItems.ROTTEN_BONE_DUST.toStack(2), AmountIngredient.of(2, MaterialItems.ROTTEN_BONE), AmountIngredient.of(2, MaterialItems.WORM_TOOTH), AmountIngredient.of(4, MaterialItems.ROTTEN_CHUNK));
        shapeless(recipeOutput, ConsumableItems.BLOODSTAINED_POWDER.toStack(2), AmountIngredient.of(6, MaterialItems.VERTEBRA), AmountIngredient.of(4, MaterialItems.BLOOD_CLOT_POWDER));
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
            shaped(recipeOutput, baseStatuePattern, statueItem.asItem().getDefaultInstance());
        }
    }

    protected void shaped(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }

    protected void shaped(RecipeOutput recipeOutput, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern), null);
    }

    protected void shapeless(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        NonNullList<Ingredient> ingredientz = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ingredientz), null);
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
    protected void basePhaseblade(RecipeOutput recipeOutput, Ingredient gem, Ingredient handle, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', gem,
                'a', handle
        ), basePhasebladePattern)), null);
    }
    protected void baseRobe(RecipeOutput recipeOutput, Ingredient robe, Ingredient gem, Ingredient handle,ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', robe,
                'b', gem,
                'a', handle
        ), baseRobePattern)), null);
    }
}
