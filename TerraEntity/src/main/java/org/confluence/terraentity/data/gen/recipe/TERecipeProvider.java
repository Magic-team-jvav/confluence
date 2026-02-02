package org.confluence.terraentity.data.gen.recipe;


import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.terraentity.init.TEItems;
import org.confluence.terraentity.init.item.*;

import java.util.concurrent.CompletableFuture;


public class TERecipeProvider extends AbstractRecipeProvider {

    public TERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput, HolderLookup.Provider holderLookup) {

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, TESummonItems.HORNET_STAFF.get())
                .pattern("BAB")
                .pattern(" C ")
                .pattern(" C ")
                .define('A', Items.BEE_SPAWN_EGG)
                .define('B', Items.HONEY_BLOCK)
                .define('C', ItemTags.FLOWERS)
                .unlockedBy("has_bee_spawn_egg",has(Items.BEE_SPAWN_EGG))
                .save(recipeOutput);

        // 鞭子

        registerWhip(recipeOutput, TEWhipItems.LEATHER_WHIP.get(), Items.LEATHER, "has_leather");
        registerWhip(recipeOutput, TEWhipItems.SLUB_WHIP.get(), Items.BAMBOO, "has_bamboo");
        registerWhip(recipeOutput, TEWhipItems.AMBER_WHIP.get(), Items.COPPER_INGOT, "has_copper_ingot");
        registerWhip(recipeOutput, TEWhipItems.AMETHYST_WHIP.get(), Items.AMETHYST_CLUSTER, "has_amethyst_cluster");
        registerWhip(recipeOutput, TEWhipItems.DIAMOND_WHIP.get(), Items.DIAMOND, "has_diamond");
        registerWhip(recipeOutput, TEWhipItems.JADE_WHIP.get(), Items.EMERALD, "has_emerald");
        registerWhip(recipeOutput, TEWhipItems.RUBY_WHIP.get(), Items.REDSTONE_BLOCK, "has_redstone_block");
        registerWhip(recipeOutput, TEWhipItems.SAPPHIRE_WHIP.get(), Items.LAPIS_BLOCK, "has_lapis_block");
        registerWhip(recipeOutput, TEWhipItems.TOPAZ_WHIP.get(), Items.GOLD_INGOT, "has_gold_ingot");

        // 回旋镖
        registerBoomerang(recipeOutput, TEBoomerangItems.WOOD_BOOMERANG.get(), ItemTags.PLANKS, "has_wood_planks");
        registerBoomerangEnchanted(recipeOutput, TEBoomerangItems.ENCHANTED_BOOMERANG.get(),TEBoomerangItems.WOOD_BOOMERANG.asItem(), Items.IRON_INGOT, "has_iron_ingot");
        registerBoomerangEnchanted(recipeOutput, TEBoomerangItems.ICE_BOOMERANG.get(),TEBoomerangItems.ENCHANTED_BOOMERANG.asItem(), Items.BLUE_ICE,  "has_blue_ice");
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, TEBoomerangItems.SHROOMERANG.get())
                .requires(TEBoomerangItems.ENCHANTED_BOOMERANG.get())
                .requires(Items.BROWN_MUSHROOM)
                .requires(Items.RED_MUSHROOM)
                .requires(Items.WARPED_FUNGUS)
                .unlockedBy("has_ice_boomerang", has(TEBoomerangItems.ICE_BOOMERANG.get()))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, TEBoomerangItems.TRIMARANG.get())
                .requires(TEBoomerangItems.ENCHANTED_BOOMERANG.get())
                .requires(TEBoomerangItems.ICE_BOOMERANG.get())
                .requires(TEBoomerangItems.SHROOMERANG.get())
                .unlockedBy("has_shroomerang", has(TEBoomerangItems.SHROOMERANG.get()))
                .save(recipeOutput);

        netheriteSmithing(recipeOutput, TEBoomerangItems.TRIMARANG.get(), RecipeCategory.COMBAT, TEBoomerangItems.FLAMARANG.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, TEItems.HOUSE_DETECTOR.get())
                .pattern(" A ")
                .pattern("ABA")
                .pattern("C C")
                .define('A', ItemTags.PLANKS)
                .define('B', Items.REDSTONE)
                .define('C', Items.STICK)
                .unlockedBy("has_red_stone",has(Items.REDSTONE))
                .save(recipeOutput);

        // 棱镜暂用配方
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, TESummonItems.TERRAPRISMA.get())
                .requires(TESummonItems.SUMMON_WOODEN_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_STONE_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_IRON_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_DIAMOND_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF)
                .unlockedBy("has_iron_sword_staff", has(TESummonItems.SUMMON_IRON_SWORD_STAFF))
                .save(recipeOutput);

        registerYoyo(recipeOutput, TEYoyosItems.WOODEN_YOYO.get(), ItemTags.PLANKS, "has_planks");
        registerYoyo(recipeOutput, TEYoyosItems.RALLY.get(), Items.IRON_INGOT, "has_iron_ingot");
        registerYoyo(recipeOutput, TEYoyosItems.MALAISE.get(), Items.SHULKER_SHELL, "has_shulker_shell");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TEBossSummonsItems.HILL_OF_FLESH_SUMMONS)
                .requires(TEBossSummonsItems.WALL_OF_FLESH_SUMMONS)
                .unlockedBy("has_voodoo_doll", has(TEBossSummonsItems.WALL_OF_FLESH_SUMMONS))
                .save(recipeOutput);
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, TEBossSummonsItems.WALL_OF_FLESH_SUMMONS)
                .requires(TEBossSummonsItems.HILL_OF_FLESH_SUMMONS)
                .unlockedBy("has_voodoo_doll", has(TEBossSummonsItems.HILL_OF_FLESH_SUMMONS))
                .save(recipeOutput);

        /*
        ShapelessRecipeBuilder.shapeless(RecipeCategory.COMBAT, TESummonItems.TERRAPRISMA.get())
                .requires(TESummonItems.SUMMON_WOODEN_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_STONE_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_GOLDEN_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_DIAMOND_SWORD_STAFF)
                .requires(TESummonItems.SUMMON_NETHERITE_SWORD_STAFF)
                .unlockedBy("has_golden_sword_staff", has(TESummonItems.SUMMON_GOLDEN_SWORD_STAFF))
                .save(recipeOutput);
         */
    }

    private static void registerYoyo(RecipeOutput recipeOutput, ItemLike yoyo, ItemLike material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, yoyo)
                .pattern(" BB")
                .pattern("ABB")
                .pattern("A  ")
                .define('A', Items.STRING)
                .define('B', material)
                .unlockedBy(name,has(yoyo))
                .save(recipeOutput);
    }

    private static void registerYoyo(RecipeOutput recipeOutput, ItemLike yoyo, TagKey<Item> material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, yoyo)
                .pattern(" BB")
                .pattern("ABB")
                .pattern("A  ")
                .define('A', Items.STRING)
                .define('B', material)
                .unlockedBy(name,has(yoyo))
                .save(recipeOutput);
    }

    protected static void netheriteSmithing(RecipeOutput recipeOutput, Item ingredientItem, RecipeCategory category, Item resultItem) {
        SmithingTransformRecipeBuilder.smithing(
                        Ingredient.of(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE), Ingredient.of(ingredientItem), Ingredient.of(Items.NETHERITE_INGOT), category, resultItem
                )
                .unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                .save(recipeOutput, getItemName(resultItem) + "_smithing");
    }

    public void registerWhip(RecipeOutput recipeOutput, Item whip, ItemLike material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, whip)
                .pattern("  A")
                .pattern("AAA")
                .pattern("A  ")
                .define('A', material)
                .unlockedBy(name, has(material))
                .save(recipeOutput);
    }

    public void registerBoomerang(RecipeOutput recipeOutput, Item boomerang, ItemLike material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boomerang)
                .pattern(" AA")
                .pattern("A  ")
                .pattern("A  ")
                .define('A', material)
                .unlockedBy(name, has(material))
                .save(recipeOutput);
    }
    public void registerBoomerangEnchanted(RecipeOutput recipeOutput, Item boomerang,Item template, ItemLike material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boomerang)
                .pattern("BAA")
                .pattern("A  ")
                .pattern("A  ")
                .define('A', material)
                .define('B', template)
                .unlockedBy(name, has(material))
                .save(recipeOutput);
    }
    public void registerBoomerang(RecipeOutput recipeOutput, Item boomerang, TagKey<Item> material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boomerang)
                .pattern(" AA")
                .pattern("A  ")
                .pattern("A  ")
                .define('A', material)
                .unlockedBy(name, has(material))
                .save(recipeOutput);
    }
    public void registerBoomerangEnchanted(RecipeOutput recipeOutput, Item boomerang, Item template, TagKey<Item> material, String name){
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boomerang)
                .pattern("BAA")
                .pattern("A  ")
                .pattern("A  ")
                .define('A', material)
                .define('B', template)
                .unlockedBy(name, has(material))
                .save(recipeOutput);
    }
}
