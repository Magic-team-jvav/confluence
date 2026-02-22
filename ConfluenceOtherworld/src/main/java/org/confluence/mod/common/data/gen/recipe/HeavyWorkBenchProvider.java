package org.confluence.mod.common.data.gen.recipe;

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
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.HeavyWorkBenchRecipe;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_guns.common.init.TGItems;
import org.confluence.terraentity.init.item.TEBoomerangItems;
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
                'A', AmountIngredient.of(10, ConsumableItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(10, Items.COBWEB)
        ), List.of(
                "/A/",
                "A A"
        )), ArmorItems.NECRO_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, ConsumableItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/ /",
                "/A/",
                "AAA"
        )), ArmorItems.NECRO_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, ConsumableItems.DUNGEON_DEMON_BONE),
                '/', AmountIngredient.of(5, Items.COBWEB)
        ), List.of(
                "/A/",
                "/ /",
                "A A"
        )), ArmorItems.NECRO_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'A', AmountIngredient.of(10, ConsumableItems.DUNGEON_DEMON_BONE),
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
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.OAK_LOG)
        ), List.of(
                " // ",
                "////",
                "////",
                " // "
        )), FunctionalBlocks.OAK_LOG_BOULDER.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.CACTUS)
        ), List.of(
                " // ",
                "////",
                "////",
                " // "
        )), FunctionalBlocks.ROLLING_CACTUS_BOULDER.toStack());
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
                'a', AmountIngredient.of(2, MaterialItems.FLINX_FUR),
                '#', AmountIngredient.of(2, ModTags.Items.GOLD_AND_PLATINUM)
        ), List.of(
                "  aa",
                " a##",
                " # #",
                "#   "
        )), TESummonItems.SNOW_FLINX_STAFF.toStack());

        // 计时器
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.GOLD_WATCH, TCItems.PLATINUM_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_1_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.SILVER_WATCH, TCItems.TUNGSTEN_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_3_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(TCItems.COPPER_WATCH, TCItems.TIN_WATCH),
                'b', Ingredient.of(Items.REDSTONE),
                'c', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "cbc",
                "cac",
                "cbc"
        )), FunctionalBlocks.TIMERS_BLOCK_5_1.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(MaterialItems.TIN_INGOT, Items.COPPER_INGOT),
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



        // 抑郁球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3, ModTags.Items.INGOTS_DEMONITE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.MALAISE.toStack());

        // 血脉球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3, ModTags.Items.INGOTS_CRIMTANE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.ARTERY.toStack());

        // 蜂巢球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(3, MaterialItems.ROYAL_WAX),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " SS",
                "/SS",
                "/  "
        )), TEYoyosItems.HIVE_FIVE.toStack());

        // 亚马逊球
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'S', AmountIngredient.of(4, NatureBlocks.LIVING_MAHOGANY_LOG_BLOCKS.PLANKS),
                'A', AmountIngredient.of(12, MaterialItems.STINGER),
                'B', AmountIngredient.of(9, MaterialItems.JUNGLE_SPORE),
                '/', Ingredient.of(MaterialItems.MAN_EATER_VINE)
        ), List.of(
                " AS",
                " /S",
                "B  "
        )), TEYoyosItems.AMAZON.toStack());

        // 秘银砧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_MYTHRIL),
                'a', Ingredient.of(ModTags.Items.INGOTS_MYTHRIL),
                'b', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS)
        ), List.of(
                "###",
                " a ",
                "aaa",
                "bbb"
        )), FunctionalBlocks.MYTHRIL_ANVIL.toStack());

        // 山铜砧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_ORICHALCUM),
                'a', Ingredient.of(ModTags.Items.INGOTS_ORICHALCUM),
                'b', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS)
        ), List.of(
                "###",
                " a ",
                "#a#",
                "bbb"
        )), FunctionalBlocks.ORICHALCUM_ANVIL.toStack());

        // 穿流戟
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(3, MaterialItems.OPAL),
                'a', AmountIngredient.of(2, MaterialItems.HEIM),
                'b', AmountIngredient.of(3, ModTags.Items.GOLD_AND_PLATINUM),
                'c', Ingredient.of(MaterialItems.HEIM)
        ), List.of(
                "  ca",
                " c#c",
                " bc ",
                "b   "
        )), SpearItems.STREAMSTRIKE_HALBERD.toStack());

        // 再生之斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(Items.WOODEN_AXE),
                'a', AmountIngredient.of(3, MaterialItems.JUNGLE_SPORE),
                'b', Ingredient.of(MaterialItems.MAN_EATER_VINE),
                'c', Ingredient.of(ToolItems.STAFF_OF_REGROWTH)
        ), List.of(
                " aaa",
                " c#b",
                " b a",
                "b   "
        )), AxeItems.AXE_OF_REGROWTH.toStack());
        // 血腥屠刀
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.VERTEBRA),
                'c', AmountIngredient.of(2, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "  cc",
                " cc ",
                "c/  ",
                "/   "
        )), SwordItems.BLOOD_BUTCHERER.toStack());
        // 嗜血狂斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.VERTEBRA),
                'c', AmountIngredient.of(2, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                " ccc",
                " c/c",
                " /c ",
                "/   "
        )), AxeItems.BLOOD_LUST_CLUSTER.toStack());
        // 死亡使者镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.VERTEBRA),
                'a', AmountIngredient.of(6, MaterialItems.TISSUE_SAMPLE),
                'c', AmountIngredient.of(3, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                " cca",
                "  /c",
                " / c",
                "/   "
        )), PickaxeItems.DEATHBRINGER_PICKAXE.toStack());
        // 血肉锤
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.VERTEBRA),
                'a', AmountIngredient.of(4, MaterialItems.TISSUE_SAMPLE),
                'c', AmountIngredient.of(3, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "cac",
                "c/c",
                " / ",
                " / "
        )), HammerItems.FLESH_GRINDER.toStack());
        // 烹饪锅
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                'b', AmountIngredient.of(2, ModTags.Items.LEAD_AND_IRON),
                '#', Ingredient.of(ItemTags.PLANKS)
        ), List.of(
                "a  a",
                "a  a",
                "baab",
                "#  #"
        )), FunctionalBlocks.COOKING_POT.toStack());
        // 锯木机
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(ModTags.Items.LEAD_AND_IRON),
                'b', AmountIngredient.of(3, ItemTags.WOODEN_SLABS),
                'c', Ingredient.of(Items.STICK),
                'd', Ingredient.of(Items.CHAIN)
        ), List.of(
                "aa  ",
                "bbbb",
                "cd c",
                "cbbc"
        )), FunctionalBlocks.SAWMILL.toStack());
        // 肌腱弓
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_CRIMTANE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " ccc",
                "cc /",
                "c / ",
                "c/  "
        )), BowItems.TENDON_BOW.toStack());
        // 恶魔弓
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.INGOTS_DEMONITE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " ccc",
                "cc /",
                "c / ",
                "c/  "
        )), BowItems.DEMON_BOW.toStack());
        // 魔光剑
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.ROTTEN_BONE),
                'c', Ingredient.of(ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "  cc",
                " ccc",
                "c/c ",
                "/c  "
        )), SwordItems.LIGHTS_BANE.toStack());
        // 暗夜战斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.ROTTEN_BONE),
                'c', AmountIngredient.of(2, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                " ccc",
                " c/c",
                " /c ",
                "/   "
        )), AxeItems.WAR_AXE_OF_THE_NIGHT.toStack());
        // 恶魔镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.ROTTEN_BONE),
                'a', AmountIngredient.of(6, MaterialItems.SHADOW_SCALE),
                'c', AmountIngredient.of(3, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                " cca",
                "  /c",
                " / c",
                "/   "
        )), PickaxeItems.NIGHTMARE_PICKAXE.toStack());
        // 魔锤
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(MaterialItems.ROTTEN_BONE),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_DEMONITE),
                'a', AmountIngredient.of(2, MaterialItems.SHADOW_SCALE)
        ), List.of(
                "aca",
                "c/c",
                " / ",
                " / "
        )), HammerItems.THE_BREAKER.toStack());
        // 暗影套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(5, MaterialItems.SHADOW_SCALE),
                '#', AmountIngredient.of(4, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "a#a",
                "# #"
        )), ArmorItems.SHADOW_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(5, MaterialItems.SHADOW_SCALE),
                '#', AmountIngredient.of(4, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "# #",
                "a#a",
                "###"
        )), ArmorItems.SHADOW_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(5, MaterialItems.SHADOW_SCALE),
                '#', AmountIngredient.of(3, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "a#a",
                "# #",
                "# #"
        )), ArmorItems.SHADOW_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(5, MaterialItems.SHADOW_SCALE),
                '#', AmountIngredient.of(3, ModTags.Items.INGOTS_DEMONITE)
        ), List.of(
                "a a",
                "# #"
        )), ArmorItems.SHADOW_BOOTS.toStack());
        // 猩红套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, MaterialItems.TISSUE_SAMPLE),
                '#', AmountIngredient.of(6, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "#a#",
                "a a"
        )), ArmorItems.CRIMSON_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, MaterialItems.TISSUE_SAMPLE),
                '#', AmountIngredient.of(5, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "a a",
                "#a#",
                "###"
        )), ArmorItems.CRIMSON_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, MaterialItems.TISSUE_SAMPLE),
                '#', AmountIngredient.of(4, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "a#a",
                "# #",
                "a a"
        )), ArmorItems.CRIMSON_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(5, MaterialItems.TISSUE_SAMPLE),
                '#', AmountIngredient.of(4, ModTags.Items.INGOTS_CRIMTANE)
        ), List.of(
                "a a",
                "# #"
        )), ArmorItems.CRIMSON_BOOTS.toStack());
        // 石骸弓
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'c', Ingredient.of(ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " ccc",
                "cc /",
                "c / ",
                "c/  "
        )), BowItems.FOSSIL_BOW.toStack());
        // 化石镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STICK),
                '#', Ingredient.of(ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL)
        ), List.of(
                " ###",
                "  /#",
                " / #",
                "/   "
        )), PickaxeItems.FOSSIL_PICKAXE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STICK),
                '#', Ingredient.of(ModTags.Items.RAW_MATERIALS_OPAL),
                'a', Ingredient.of(MaterialItems.HEIM)
        ), List.of(
                " aa#",
                "  /a",
                " / a",
                "/   "
        )), PickaxeItems.ABYSSAL_PICKAXE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STICK),
                '#', Ingredient.of(ModTags.Items.RAW_MATERIALS_GELSTONE),
                'a', Ingredient.of(MaterialItems.SPORE_ROOT)
        ), List.of(
                " aa#",
                "  /a",
                " / a",
                "/   "
        )), PickaxeItems.MIASMA_PICKAXE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '/', Ingredient.of(Items.STICK),
                '#', Ingredient.of(ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL),
                'a', Ingredient.of(MaterialItems.WINTER_MARROW)
        ), List.of(
                " aa#",
                "  /a",
                " / a",
                "/   "
        )), PickaxeItems.COLD_CRYSTAL_PICKAXE.toStack());
        // 化石套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL),
                'a', AmountIngredient.of(2, ModTags.Items.GEMS_AMBER)
        ), List.of(
                "#a#",
                "# #"
        )), ArmorItems.FOSSIL_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL),
                'a', AmountIngredient.of(2, ModTags.Items.GEMS_AMBER)
        ), List.of(
                "# #",
                "#a#",
                "###"
        )), ArmorItems.FOSSIL_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.FOSSIL_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_STURDY_FOSSIL)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.FOSSIL_BOOTS.toStack());
        // 熔火之怒
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'c', AmountIngredient.of(2, ModTags.Items.INGOTS_HELLSTONE),
                '/', Ingredient.of(Items.STRING)
        ), List.of(
                " ccc",
                "cc /",
                "c / ",
                "c/  "
        )), BowItems.MOLTEN_FURY.toStack());
        // 泥土炸弹
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(3, Items.DIRT),
                'a', Ingredient.of(ConsumableItems.BOMB)
        ), List.of(
                "###",
                "#a#",
                "###"
        )), ConsumableItems.DIRT_BOMB.toStack());
        // 烈焰回旋镖
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, ModTags.Items.INGOTS_HELLSTONE),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_HELLSTONE),
                'c', Ingredient.of(TEBoomerangItems.ENCHANTED_BOOMERANG)
        ), List.of(
                "  ab",
                " c  ",
                "a   ",
                "b   "
        )), TEBoomerangItems.FLAMARANG.toStack());
        // 火山
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_HELLSTONE),
                'b', Ingredient.of(Items.SOUL_SAND),
                'c', AmountIngredient.of(2, Items.BLAZE_ROD)
        ), List.of(
                "   a",
                " aa ",
                "baa ",
                "cb  "
        )), SwordItems.VOLCANO.toStack());
        // 熔岩锤斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(3, ModTags.Items.INGOTS_HELLSTONE),
                'a', Ingredient.of(Items.BLAZE_ROD)
        ), List.of(
                "####",
                " #a#",
                " a  ",
                "a   "
        )), HamaxeItems.MOLTEN_HAMAXE.toStack());
        // 熔岩镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_HELLSTONE),
                '/', Ingredient.of(Items.BLAZE_ROD)
        ), List.of(
                " ccc",
                "  /c",
                " / c",
                "/   "
        )), PickaxeItems.MOLTEN_PICKAXE.toStack());
        // 流星锤斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE),
                'a', AmountIngredient.of(3, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "####",
                " #a#",
                " a  ",
                "a   "
        )), HamaxeItems.METEOR_HAMAXE.toStack());
        // 陨石套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "###",
                "# #"
        )), ArmorItems.METEOR_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "# #",
                "###",
                "###"
        )), ArmorItems.METEOR_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.METEOR_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.METEOR_BOOTS.toStack());
        // 太空枪
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, ModTags.Items.INGOTS_METEORITE),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_METEORITE)
        ), List.of(
                "  ##",
                " ## ",
                "aa  ",
                "  a "
        )), ManaWeaponItems.SPACE_GUN.toStack());
        // 南瓜套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.PUMPKIN)
        ), List.of(
                "###",
                "# #"
        )), ArmorItems.PUMPKIN_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.PUMPKIN)
        ), List.of(
                "# #",
                "###",
                "###"
        )), ArmorItems.PUMPKIN_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.PUMPKIN)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.PUMPKIN_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, Items.PUMPKIN)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.PUMPKIN_BOOTS.toStack());
        // 寒晶套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.WINTER_MARROW),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL)
        ), List.of(
                "#a#",
                "# #"
        )), ArmorItems.COLD_CRYSTAL_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.WINTER_MARROW),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_COLD_CRYSTAL)
        ), List.of(
                "#a#",
                "# #",
                "# #"
        )), ArmorItems.COLD_CRYSTAL_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.WINTER_MARROW)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.COLD_CRYSTAL_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.WINTER_MARROW)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.COLD_CRYSTAL_BOOTS.toStack());
        // 孢根套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.SPORE_ROOT),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_GELSTONE)
        ), List.of(
                "#a#",
                "# #"
        )), ArmorItems.SPORE_ROOT_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.SPORE_ROOT),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_GELSTONE)
        ), List.of(
                "# #",
                "#a#",
                "###"
        )), ArmorItems.SPORE_ROOT_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.SPORE_ROOT)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.SPORE_ROOT_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.SPORE_ROOT)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.SPORE_ROOT_BOOTS.toStack());
        // 渊潾套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HEIM),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_OPAL),
                'b', AmountIngredient.of(2, Items.PRISMARINE_SHARD)
        ), List.of(
                "bab",
                "# #"
        )), ArmorItems.HEIM_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HEIM),
                'a', AmountIngredient.of(2, ModTags.Items.RAW_MATERIALS_OPAL),
                'b', AmountIngredient.of(2, Items.PRISMARINE_SHARD)
        ), List.of(
                "# #",
                "bab",
                "###"
        )), ArmorItems.HEIM_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HEIM),
                'b', AmountIngredient.of(2, Items.PRISMARINE_SHARD)
        ), List.of(
                "b#b",
                "b b",
                "# #"
        )), ArmorItems.HEIM_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.HEIM),
                'b', AmountIngredient.of(2, Items.PRISMARINE_SHARD)
        ), List.of(
                "b b",
                "# #"
        )), ArmorItems.HEIM_BOOTS.toStack());
        // 魔光护符
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', Ingredient.of(ModTags.Items.EVIL_INGOT),
                'c', AmountIngredient.of(2, ModTags.Items.EVIL_INGOT),
                '/', AmountIngredient.of(2, ModTags.Items.GEMS_TOPAZ)
        ), List.of(
                "a  a",
                " cc ",
                "c//c",
                "cccc"
        )), TCItems.MAGILUMINESCENCE.toStack());
        // 黄蜂法杖
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(2, MaterialItems.ROYAL_WAX)
        ), List.of(
                "###",
                "# #",
                " # ",
                " # "
        )), TESummonItems.HORNET_STAFF.toStack());
        // 蜜蜂套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(MaterialItems.ROYAL_WAX)
        ), List.of(
                "###",
                "# #"
        )), ArmorItems.BEE_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(MaterialItems.ROYAL_WAX)
        ), List.of(
                "# #",
                "###",
                "###"
        )), ArmorItems.BEE_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(MaterialItems.ROYAL_WAX)
        ), List.of(
                "###",
                "# #",
                "# #"
        )), ArmorItems.BEE_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(MaterialItems.ROYAL_WAX)
        ), List.of(
                "# #",
                "# #"
        )), ArmorItems.BEE_BOOTS.toStack());

        // 钴套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_COBALT),
                'b', AmountIngredient.of(3,ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "bab",
                "# #"
        )), ArmorItems.COBALT_HAT.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "aaa",
                "a a"
        )), ArmorItems.COBALT_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "a#a",
                "###",
                " a "
        )), ArmorItems.COBALT_MASK.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_COBALT),
                'b', AmountIngredient.of(3,ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "a a",
                "bab",
                "bab"
        )), ArmorItems.COBALT_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "a#a",
                "# #",
                "# #"
        )), ArmorItems.COBALT_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT)
                ), List.of(
                "a a",
                "# #"
        )), ArmorItems.COBALT_BOOTS.toStack());
        // 钴薙刀
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_COBALT),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  ab",
                " /  ",
                "a   "
        )), SpearItems.COBALT_NAGINATA.toStack());
        // 钴剑
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   a",
                "  aa",
                " a/ ",
                "/   "
        )), SwordItems.COBALT_SWORD.toStack());
        // 钴镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_COBALT),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                "  /b",
                " / b",
                "/   "
        )), PickaxeItems.COBALT_PICKAXE.toStack());
        // 钴战斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )), AxeItems.COBALT_WARAXE.toStack());
        // 钴钻头
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "##a ",
                "##aa"
        )), ChainsawItems.COBALT_CHAINSAW.toStack());
        // 钴链锯
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_COBALT),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT)
        ), List.of(
                "##a ",
                "# #a",
                "##a "
        )), DrillItems.COBALT_DRILL.toStack());

        // 钯金套
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_PALLADIUM),
                'b', AmountIngredient.of(3,ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "bab",
                "a a"
        )), ArmorItems.PALLADIUM_HEADGEAR.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "aaa",
                "b b"
        )), ArmorItems.PALLADIUM_HELMET.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_PALLADIUM),
                'a', AmountIngredient.of(2,ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "aaa",
                "#a#",
                " a "
        )), ArmorItems.PALLADIUM_MASK.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3,ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "b b",
                "bbb",
                "bbb"
        )), ArmorItems.PALLADIUM_CHESTPLATE.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_PALLADIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "aaa",
                "# #",
                "# #"
        )), ArmorItems.PALLADIUM_LEGGINGS.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "a a",
                "a a"
        )), ArmorItems.PALLADIUM_BOOTS.toStack());
        // 钯金剑
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "  aa",
                "  a ",
                "a/a ",
                "/   "
        )), SwordItems.PALLADIUM_SWORD.toStack());
        // 钯金刺矛
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_PALLADIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "   b",
                "  ab",
                " / a",
                "a   "
        )), SpearItems.PALLADIUM_PIKE.toStack());
        // 钯金镐
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_PALLADIUM),
                'c', AmountIngredient.of(4, ModTags.Items.INGOTS_PALLADIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bcc",
                "  /c",
                " / b",
                "/   "
        )), PickaxeItems.PALLADIUM_PICKAXE.toStack());
        // 钯金战斧
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_PALLADIUM),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM),
                '/', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                " bbb",
                " #/b",
                " /# ",
                "/   "
        )), AxeItems.PALLADIUM_WARAXE.toStack());
        // 钯金钻头
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_PALLADIUM),
                'a', AmountIngredient.of(4, ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "##a ",
                "# #a",
                "##a "
        )), DrillItems.PALLADIUM_DRILL.toStack());
        // 钯金链锯
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_PALLADIUM),
                'a', AmountIngredient.of(2, ModTags.Items.INGOTS_PALLADIUM)
        ), List.of(
                "#aa ",
                "#aaa"
        )), ChainsawItems.PALLADIUM_CHAINSAW.toStack());

        // 荆鞭
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(6,MaterialItems.JUNGLE_SPORE),
                'a', Ingredient.of(MaterialItems.MAN_EATER_VINE),
                'b', AmountIngredient.of(15, MaterialItems.STINGER)
        ), List.of(
                "  a ",
                " a a",
                " # b",
                "#   "
        )), TEWhipItems.SNAPTHORN.toStack());
        // 脊柱骨鞭
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', AmountIngredient.of(45,ConsumableItems.DUNGEON_DEMON_BONE),
                'a', AmountIngredient.of(13,Items.COBWEB)
        ), List.of(
                "  a ",
                " a a",
                " # a",
                "#   "
        )), TEWhipItems.SPINAL_TAP.toStack());

        // 珍珠木棍
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(NatureBlocks.PEARL_LOG_BLOCKS.PLANKS.get())
        ), List.of(
                "#",
                "#",
                "#"
        )), MaterialItems.PEARLWOOD_STICK.toStack(6));

        // 垃圾桶
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.LEAD_AND_IRON)
        ), List.of(
                "####",
                " ## ",
                " ## "
        )), TFBlocks.TRASH_CAN.toStack());

        // 凤凰爆破枪
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                '#', Ingredient.of(ModTags.Items.INGOTS_HELLSTONE),
                'a', Ingredient.of(TGItems.HAND_GUN)
        ), List.of(
                "####",
                "a## ",
                " ## "
        )), TGItems.PHOENIX_BLASTER.toStack());
        // 魂钥匙
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, MaterialItems.SOUL_OF_LIGHT),
                'b', Ingredient.of(MaterialItems.SOUL_OF_LIGHT)
        ), List.of(
                "aaa",
                " a ",
                "bb ",
                " b "
        )), ToolItems.KEY_OF_LIGHT.toStack());

        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(3, MaterialItems.SOUL_OF_NIGHT),
                'b', Ingredient.of(MaterialItems.SOUL_OF_NIGHT)
        ), List.of(
                "aaa",
                " a ",
                "bb ",
                " b "
        )), ToolItems.KEY_OF_NIGHT.toStack());

        // 连弩
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_COBALT),
                'b', AmountIngredient.of(2, ModTags.Items.INGOTS_COBALT),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )), CrossbowItems.COBALT_REPEATER.toStack());
        shaped(recipeOutput, ShapedRecipePattern.of(Map.of(
                'a', AmountIngredient.of(6, ModTags.Items.INGOTS_PALLADIUM),
                'b', AmountIngredient.of(3, ModTags.Items.INGOTS_PALLADIUM),
                'c', Ingredient.of(Items.TRIPWIRE_HOOK),
                'd', Ingredient.of(MaterialItems.PEARLWOOD_STICK)
        ), List.of(
                "dad",
                "bcb",
                " d "
        )), CrossbowItems.PALLADIUM_REPEATER.toStack());

        shapeless(recipeOutput, NatureBlocks.THIN_ICE_BLOCK.toStack(), EnvironmentLevelAccess.matcher(null, null, true), Ingredient.of(Items.ICE));
        shapeless(recipeOutput, ConsumableItems.BONE_THROWING_KNIFE.toStack(30),Ingredient.of(MaterialItems.STURDY_FOSSIL));
        shapeless(recipeOutput, ConsumableItems.ROTTEN_BONE_DUST.toStack(2), AmountIngredient.of(2, MaterialItems.ROTTEN_BONE), AmountIngredient.of(2, MaterialItems.WORM_TOOTH), AmountIngredient.of(4, MaterialItems.ROTTEN_CHUNK));
        shapeless(recipeOutput, ConsumableItems.BLOODSTAINED_POWDER.toStack(2), AmountIngredient.of(6, MaterialItems.VERTEBRA), AmountIngredient.of(4, MaterialItems.BLOOD_CLOT_POWDER));
        shapeless(recipeOutput, TGItems.SILVER_BULLET.toStack(70), AmountIngredient.of(70, TGItems.MUSKET_BULLET), Ingredient.of(ModTags.Items.INGOTS_SILVER));
        shapeless(recipeOutput, TGItems.TUNGSTEN_BULLET.toStack(70), AmountIngredient.of(70, TGItems.MUSKET_BULLET), Ingredient.of(ModTags.Items.INGOTS_TUNGSTEN));
        shapeless(recipeOutput, TGItems.METEOR_SHOT.toStack(70), AmountIngredient.of(70, TGItems.MUSKET_BULLET), Ingredient.of(ModTags.Items.INGOTS_METEORITE));

        shapeless(recipeOutput, TGItems.HIGH_VELOCITY_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.COG));
        shapeless(recipeOutput, TGItems.PARTY_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.CONFETTI));
        shapeless(recipeOutput, TGItems.GOLDEN_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.GOLD_DUST));
        shapeless(recipeOutput, TGItems.NANO_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.NANITES));
        shapeless(recipeOutput, TGItems.EXPLODING_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.EXPLOSIVE_POWDER));
        shapeless(recipeOutput, TGItems.VENOM_BULLET.toStack(50), AmountIngredient.of(50, MaterialItems.EMPTY_BULLET), Ingredient.of(MaterialItems.VIAL_OF_VENOM));

        shapeless(recipeOutput, FunctionalBlocks.EXPLODE_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER), Ingredient.of(FunctionalBlocks.INSTANTANEOUS_EXPLOSION_TNT));
        shapeless(recipeOutput, FunctionalBlocks.FOLLOWER_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER), Ingredient.of(Items.OBSERVER));
        shapeless(recipeOutput, FunctionalBlocks.EVER_POWERED_RAIL.toStack(16), AmountIngredient.of(16, Items.POWERED_RAIL), Ingredient.of(Items.REDSTONE_TORCH));

        shapeless(recipeOutput, ConsumableItems.SCARAB_BOMB.toStack(), Ingredient.of(ConsumableItems.BOMB), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        shapeless(recipeOutput, NatureBlocks.LIFE_CRYSTAL_BLOCK.toStack(), Ingredient.of(ConsumableItems.LIFE_CRYSTAL));

        shapeless(recipeOutput, ModItems.SCRYING_ORB.toStack(), Ingredient.of(TCItems.MAGIC_MIRROR,ToolItems.ICE_MIRROR), AmountIngredient.of(2, MaterialItems.LENS), AmountIngredient.of(4, PotionItems.WORMHOLE_POTION));


        shapeless(recipeOutput, FunctionalBlocks.BOUNCY_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER), AmountIngredient.of(5, MaterialItems.PINK_GEL));
        shapeless(recipeOutput, FunctionalBlocks.LAVA_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER),  Ingredient.of(ConsumableItems.LAVA_BOMB));
        shapeless(recipeOutput, FunctionalBlocks.SPIDER_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER), AmountIngredient.of(200, Items.COBWEB));


        shapeless(recipeOutput, ConsumableItems.ABEEMINATION.toStack(), AmountIngredient.of(5, NatureBlocks.JUNGLE_HIVE_BLOCK), AmountIngredient.of(5, Items.HONEY_BLOCK), Ingredient.of(Items.HONEY_BOTTLE), Ingredient.of(MaterialItems.STINGER));
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
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern, EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void shaped(RecipeOutput recipeOutput, ShapedRecipePattern pattern, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern, EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void shapeless(RecipeOutput recipeOutput, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        NonNullList<Ingredient> ingredientz = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ingredientz, EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void shaped(RecipeOutput recipeOutput, String suffix, ShapedRecipePattern pattern, ItemStack result, EnvironmentLevelAccess.Matcher environment) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()) + suffix);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern, environment), null);
    }

    protected void shaped(RecipeOutput recipeOutput, ShapedRecipePattern pattern, ItemStack result, EnvironmentLevelAccess.Matcher environment) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, pattern, environment), null);
    }

    protected void shapeless(RecipeOutput recipeOutput, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        NonNullList<Ingredient> ingredientz = NonNullList.of(Ingredient.EMPTY, ingredients);
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ingredientz, environment), null);
    }

    protected void baseHook(RecipeOutput recipeOutput, Ingredient hook, Ingredient chain, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', hook,
                'a', chain
        ), baseHookPattern), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void baseWhip(RecipeOutput recipeOutput, Ingredient handle, Ingredient strip, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', handle,
                'a', strip
        ), baseWhipPattern), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void baseStaff(RecipeOutput recipeOutput, Ingredient gem, Ingredient handle, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', gem,
                'a', handle
        ), baseStaffPattern), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }

    protected void basePhaseblade(RecipeOutput recipeOutput, Ingredient gem, Ingredient handle, ItemStack result) {
        ResourceLocation id = Confluence.asResource("heavy_work_bench/" + getItemName(result.getItem()));
        recipeOutput.accept(id, new HeavyWorkBenchRecipe(result, ShapedRecipePattern.of(Map.of(
                '#', gem,
                'a', handle
        ), basePhasebladePattern), EnvironmentLevelAccess.Matcher.EMPTY), null);
    }


}
