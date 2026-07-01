package org.confluence.mod.common.data.gen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SmithingTransformRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.holdersets.OrHolderSet;
import org.confluence.lib.common.data.gen.AbstractRecipeProvider;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.lib.common.recipe.EnvironmentLevelAccess;
import org.confluence.lib.common.recipe.SimpleFinishedRecipe;
import org.confluence.mod.Confluence;
import org.confluence.mod.common.init.ModTags;
import org.confluence.mod.common.init.block.*;
import org.confluence.mod.common.init.item.*;
import org.confluence.mod.common.recipe.*;
import org.confluence.terra_curio.common.init.TCItems;
import org.confluence.terra_curio.common.recipe.WorkshopRecipe;
import org.confluence.terra_furniture.common.init.TFBlocks;
import org.confluence.terra_furniture.common.init.TFTags;
import org.mesdag.portlib.wrapper.common.PortTags;
import org.mesdag.portlib.wrapper.world.item.crafting.PortShapedRecipePattern;

import java.util.*;
import java.util.function.Consumer;

public class ModRecipeProvider extends AbstractRecipeProvider {
    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> writer, HolderLookup.Provider provider) {
        EnvironmentLevelAccess.SearchContext searchWater = searchWater(provider);

        // 高炉
        RecipeSerializer<?> blasting = RecipeSerializer.BLASTING_RECIPE;
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_AMBER), MaterialItems.AMBER.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_RUBY), MaterialItems.RUBY.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_TOPAZ), MaterialItems.TOPAZ.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_JADE), MaterialItems.JADE.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_SAPPHIRE), MaterialItems.SAPPHIRE.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.ORES_AMETHYST), MaterialItems.AMETHYST.toStack(), 1.0F, 100);

        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.COAL_ORE_SMELTING), Items.COAL.getDefaultInstance(), 0.1F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.COPPER_ORE_SMELTING), Items.COPPER_INGOT.getDefaultInstance(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.IRON_ORE_SMELTING), Items.IRON_INGOT.getDefaultInstance(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.GOLD_ORE_SMELTING), Items.GOLD_INGOT.getDefaultInstance(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.DIAMOND_ORE_SMELTING), Items.DIAMOND.getDefaultInstance(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.EMERALD_ORE_SMELTING), Items.EMERALD.getDefaultInstance(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.REDSTONE_ORE_SMELTING), Items.REDSTONE.getDefaultInstance(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.LAPIS_ORE_SMELTING), Items.LAPIS_ORE.getDefaultInstance(), 0.7F, 100);

        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.TIN_ORE_SMELTING), MaterialItems.TIN_INGOT.toStack(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.LEAD_ORE_SMELTING), MaterialItems.LEAD_INGOT.toStack(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.SILVER_ORE_SMELTING), MaterialItems.SILVER_INGOT.toStack(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.TUNGSTEN_ORE_SMELTING), MaterialItems.TUNGSTEN_INGOT.toStack(), 0.7F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.PLATINUM_ORE_SMELTING), MaterialItems.PLATINUM_INGOT.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.CRIMTANE_ORE_SMELTING), MaterialItems.CRIMTANE_INGOT.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.DEMONITE_ORE_SMELTING), MaterialItems.DEMONITE_INGOT.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(ModTags.Items.METEORITE_ORE_SMELTING), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.COLD_CRYSTAL_ORE), MaterialItems.COLD_CRYSTAL.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.GELSTONE_ORE), MaterialItems.GELSTONE.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.SPORE_ROOT_BLOCK), MaterialItems.SPORE_ROOT.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.OPAL_ORE), MaterialItems.OPAL.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.WINTER_MARROW_BLOCK), MaterialItems.WINTER_MARROW.toStack(), 1.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.LUNARTEAR_ORE), MaterialItems.LUNARTEAR.toStack(), 2.0F, 100);
        cooking(writer, blasting, "blasting/", "", Ingredient.of(OreBlocks.DRAGONSAL_ORE), MaterialItems.DRAGONSAL.toStack(), 2.0F, 100);
        // 熔炉
        RecipeSerializer<?> smelting = RecipeSerializer.SMELTING_RECIPE;
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_AMBER), MaterialItems.AMBER.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_RUBY), MaterialItems.RUBY.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_TOPAZ), MaterialItems.TOPAZ.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_JADE), MaterialItems.JADE.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_SAPPHIRE), MaterialItems.SAPPHIRE.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.ORES_AMETHYST), MaterialItems.AMETHYST.toStack(), 1.0F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.COAL_ORE_SMELTING), Items.COAL.getDefaultInstance(), 0.1F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.COPPER_ORE_SMELTING), Items.COPPER_INGOT.getDefaultInstance(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.IRON_ORE_SMELTING), Items.IRON_INGOT.getDefaultInstance(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.GOLD_ORE_SMELTING), Items.GOLD_INGOT.getDefaultInstance(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.DIAMOND_ORE_SMELTING), Items.DIAMOND.getDefaultInstance(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.EMERALD_ORE_SMELTING), Items.EMERALD.getDefaultInstance(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.REDSTONE_ORE_SMELTING), Items.REDSTONE.getDefaultInstance(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.LAPIS_ORE_SMELTING), Items.LAPIS_ORE.getDefaultInstance(), 0.7F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.TIN_ORE_SMELTING), MaterialItems.TIN_INGOT.toStack(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.LEAD_ORE_SMELTING), MaterialItems.LEAD_INGOT.toStack(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.SILVER_ORE_SMELTING), MaterialItems.SILVER_INGOT.toStack(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.TUNGSTEN_ORE_SMELTING), MaterialItems.TUNGSTEN_INGOT.toStack(), 0.7F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.PLATINUM_ORE_SMELTING), MaterialItems.PLATINUM_INGOT.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.CRIMTANE_ORE_SMELTING), MaterialItems.CRIMTANE_INGOT.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.DEMONITE_ORE_SMELTING), MaterialItems.DEMONITE_INGOT.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.METEORITE_ORE_SMELTING), MaterialItems.METEORITE_INGOT.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.COLD_CRYSTAL_ORE), MaterialItems.COLD_CRYSTAL.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.GELSTONE_ORE), MaterialItems.GELSTONE.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.SPORE_ROOT_BLOCK), MaterialItems.SPORE_ROOT.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.OPAL_ORE), MaterialItems.OPAL.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.WINTER_MARROW_BLOCK), MaterialItems.WINTER_MARROW.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.LUNARTEAR_ORE), MaterialItems.LUNARTEAR.toStack(), 2.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(OreBlocks.DRAGONSAL_ORE), MaterialItems.DRAGONSAL.toStack(), 2.0F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_EBONSAND_BLOCK), NatureBlocks.EBONSAND.toStack(), 0.15F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_PEARLSAND_BLOCK), NatureBlocks.PEARLSAND.toStack(), 0.15F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_RED_SAND_BLOCK), Items.RED_SAND.getDefaultInstance(), 0.15F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(NatureBlocks.MOISTENED_SAND_BLOCK), Items.SAND.getDefaultInstance(), 0.15F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(PortTags.Items.GLASS_BLOCKS_COLORLESS), PotionItems.MUG.toStack(), 1.0F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(NatureBlocks.DIATOMACEOUS), DecorativeBlocks.PURE_GLASS.toStack(), 0.3F, 200);
        cooking(writer, smelting, "smelting/", "gold_nugget_from_gold_cooking", Ingredient.of(ModTags.Items.GOLD_COOKING), Items.GOLD_NUGGET.getDefaultInstance(), 0.1F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(DecorativeBlocks.MARBLE_BRICKS.FULL), DecorativeBlocks.CRACKED_MARBLE_BRICKS.toStack(), 0.1F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(DecorativeBlocks.GRANITE_BRICKS.FULL), DecorativeBlocks.CRACKED_GRANITE_BRICKS.toStack(), 0.1F, 200);


        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.2F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 200);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.PINE_NUT), FoodItems.ROASTED_PINE_NUT.toStack(), 0.02F, 20);
        cooking(writer, smelting, "smelting/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);

        cooking(writer, smelting, "smelting/", "glass_from_crimsand", Ingredient.of(NatureBlocks.CRIMSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);
        cooking(writer, smelting, "smelting/", "glass_from_ebonsand", Ingredient.of(NatureBlocks.EBONSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);
        cooking(writer, smelting, "smelting/", "glass_from_pearlsand", Ingredient.of(NatureBlocks.PEARLSAND), Items.GLASS.getDefaultInstance(), 0.1F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(ModTags.Items.GOLD_COOKING), Items.GOLD_NUGGET.getDefaultInstance(), 0.1F, 200);

        cooking(writer, smelting, "smelting/", "", Ingredient.of(Items.SOUL_SAND), DecorativeBlocks.SOUL_GLASS.toStack(), 0.1F, 200);

        RecipeSerializer<?> smoking = RecipeSerializer.SMELTING_RECIPE;
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 100);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.PINE_NUT), FoodItems.ROASTED_PINE_NUT.toStack(), 0.02F, 10);
        cooking(writer, smoking, "smoking/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);

        RecipeSerializer<?> campfireCooking = RecipeSerializer.CAMPFIRE_COOKING_RECIPE;
        cooking(writer, campfireCooking, "campfire_cooking/", "_from_atlantic_cod", Ingredient.of(FoodItems.ATLANTIC_COD), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.BAOBAB_FRUIT), FoodItems.COOKED_BAOBAB_FRUIT.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.FLUTTERING_LAMB_CHOPS), FoodItems.COOKED_FLUTTERING_LAMB_CHOPS.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_DUCK), FoodItems.COOKED_DUCK.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_BIRD), FoodItems.COOKED_BIRD.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_FROG), FoodItems.COOKED_FROG.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.RAW_SQUIRREL), FoodItems.COOKED_SQUIRREL.toStack(), 0.35F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.SALMON), Items.COOKED_SALMON.getDefaultInstance(), 0.2F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.PINE_NUT), FoodItems.ROASTED_PINE_NUT.toStack(), 0.02F, 30);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.CLOUD_DOUGH), FoodItems.CLOUD_BREAD.toStack(), 0.2F, 200);
        cooking(writer, campfireCooking, "campfire_cooking/", "", Ingredient.of(FoodItems.ATLANTIC_COD, FoodItems.PISCES_FIN_COD, FoodItems.SEA_BASS, FoodItems.TROUT), Items.COOKED_COD.getDefaultInstance(), 0.35F, 200);


        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_CHAIN),
                Ingredient.of(DecorativeBlocks.AMBER_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.AMBER_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/amber_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN),
                Ingredient.of(DecorativeBlocks.TOPAZ_CHAIN),
                Ingredient.of(DecorativeBlocks.TOPAZ_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.TOPAZ_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/topaz_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN),
                Ingredient.of(DecorativeBlocks.AMETHYST_CHAIN),
                Ingredient.of(DecorativeBlocks.AMETHYST_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.AMETHYST_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/amethyst_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN),
                Ingredient.of(DecorativeBlocks.DIAMOND_CHAIN),
                Ingredient.of(Items.DIAMOND_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.DIAMOND_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/diamond_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.RUBY_CHAIN),
                Ingredient.of(DecorativeBlocks.RUBY_CHAIN),
                Ingredient.of(DecorativeBlocks.RUBY_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.RUBY_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/ruby_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN),
                Ingredient.of(DecorativeBlocks.SAPPHIRE_CHAIN),
                Ingredient.of(DecorativeBlocks.SAPPHIRE_BLOCK),
                RecipeCategory.TOOLS,
                HookItems.SAPPHIRE_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/sapphire_hook"));
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.CHAIN),
                Ingredient.of(Items.CHAIN),
                Ingredient.of(MaterialItems.HOOK),
                RecipeCategory.TOOLS,
                HookItems.GRAPPLING_HOOK.get()
        ).save(writer, Confluence.asResource("smithing/grappling_hook"));


        skyMill(writer, DecorativeBlocks.BOUNCY_CLOUD_BLOCK.toStack(), Ingredient.of(MaterialItems.PINK_GEL), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(writer, DecorativeBlocks.STAR_CLOUD_BLOCK.toStack(10), Ingredient.of(MaterialItems.FALLING_STAR), AmountIngredient.of(10, NatureBlocks.CLOUD_BLOCK));
        skyMill(writer, ChestBlocks.SKYWARE_CHEST.toStack(), AmountIngredient.of(8, DecorativeBlocks.SUN_PLATE.FULL), Ingredient.of(ModTags.Items.LEAD_AND_IRON));
        skyMill(writer, DecorativeBlocks.DISC_BLOCK.FULL.toStack(4), Ingredient.of(DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.SKYWARE_DOOR.toStack(), AmountIngredient.of(2, DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.SKYWARE_GLASS_DOOR.toStack(), AmountIngredient.of(2, DecorativeBlocks.SUN_PLATE.FULL), Ingredient.of(DecorativeBlocks.PURE_GLASS));
        skyMill(writer, DecorativeBlocks.SUN_PLATE.FULL.toStack(25), AmountIngredient.of(25, Items.COBBLESTONE), Ingredient.of(MaterialItems.FALLING_STAR));
        skyMill(writer, DecorativeBlocks.SUN_PLATE.STAIRS.toStack(), Ingredient.of(DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.SUN_PLATE.WALL.toStack(), Ingredient.of(DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.SUN_PLATE.SLAB.toStack(2), Ingredient.of(DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, NatureBlocks.CLOUD_BLOCK.toStack(2), Ingredient.of(MaterialItems.WEAVING_CLOUD_COTTON));
        skyMill(writer, NatureBlocks.RAIN_CLOUD_BLOCK.toStack(), EnvironmentLevelAccess.matcher(null, searchWater, false), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(writer, NatureBlocks.SNOW_CLOUD_BLOCK.toStack(), EnvironmentLevelAccess.matcher(provider.lookupOrThrow(Registries.BIOME).getOrThrow(PortTags.Biomes.IS_COLD_OVERWORLD), null, false), Ingredient.of(NatureBlocks.CLOUD_BLOCK));
        skyMill(writer, TFBlocks.SKYWARE_SET.TABLE.toStack(), AmountIngredient.of(8, DecorativeBlocks.SUN_PLATE.FULL));
        skyMill(writer, TFBlocks.DUSKWARE_SET.TABLE.toStack(), AmountIngredient.of(8, DecorativeBlocks.MOON_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.DISC_BLOCK.STAIRS.toStack(), Ingredient.of(DecorativeBlocks.DISC_BLOCK.FULL));
        skyMill(writer, DecorativeBlocks.DISC_BLOCK.WALL.toStack(), Ingredient.of(DecorativeBlocks.DISC_BLOCK.FULL));
        skyMill(writer, DecorativeBlocks.DISC_BLOCK.SLAB.toStack(2), Ingredient.of(DecorativeBlocks.DISC_BLOCK.FULL));
        skyMill(writer, DecorativeBlocks.MOON_PLATE.WALL.toStack(), Ingredient.of(DecorativeBlocks.MOON_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.MOON_PLATE.STAIRS.toStack(), Ingredient.of(DecorativeBlocks.MOON_PLATE.FULL));
        skyMill(writer, DecorativeBlocks.MOON_PLATE.SLAB.toStack(2), Ingredient.of(DecorativeBlocks.MOON_PLATE.FULL));

        loom(writer, ModBlocks.SILK_ROPE.toStack(30), Ingredient.of(MaterialItems.SILK));
        loom(writer, MaterialItems.SILK.toStack(), Ingredient.of(Items.COBWEB));
        loom(writer, new ItemStack(Items.STRING, 4), Ingredient.of((ItemTags.WOOL)));
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, Tags.Items.GEMS_DIAMOND), Ingredient.of(Tags.Items.GEMS_DIAMOND), ArmorItems.DIAMOND_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_RUBY), Ingredient.of(ModTags.Items.GEMS_RUBY), ArmorItems.RUBY_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMBER), Ingredient.of(ModTags.Items.GEMS_AMBER), ArmorItems.AMBER_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_TOPAZ), Ingredient.of(ModTags.Items.GEMS_TOPAZ), ArmorItems.TOPAZ_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_JADE), Ingredient.of(ModTags.Items.GEMS_JADE), ArmorItems.JADE_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_SAPPHIRE), Ingredient.of(ModTags.Items.GEMS_SAPPHIRE), ArmorItems.SAPPHIRE_ROBE.toStack());
        baseRobe(writer, Ingredient.of(VanityArmorItems.ROBE), AmountIngredient.of(2, ModTags.Items.GEMS_AMETHYST), Ingredient.of(ModTags.Items.GEMS_AMETHYST), ArmorItems.AMETHYST_ROBE.toStack());
        loom(writer, VanityArmorItems.ROBE.toStack(),
                PortShapedRecipePattern.of(
                        Map.of(
                                '#', AmountIngredient.of(3, MaterialItems.SILK),
                                'a', AmountIngredient.of(2, MaterialItems.SILK)
                        ),
                        List.of(
                                "#a#",
                                "# #",
                                "# #"
                        )
                )
        );
        loom(writer, ArmorItems.FLINX_FUR_COAT.toStack(),
                PortShapedRecipePattern.of(
                        Map.of(
                                'a', AmountIngredient.of(4, MaterialItems.FLINX_FUR),
                                'b', AmountIngredient.of(2, MaterialItems.SILK),
                                '#', AmountIngredient.of(8, ModTags.Items.GOLD_AND_PLATINUM)
                        ),
                        List.of(
                                "a#a",
                                "bbb",
                                "b b"
                        )
                )
        );

        // 哥布林战旗
        loom(writer, ConsumableItems.GOBLIN_BATTLE_STANDARD.toStack(),
                PortShapedRecipePattern.of(Map.of(
                                '#', AmountIngredient.of(3, MaterialItems.TATTERED_CLOTH),
                                'b', Ingredient.of(MaterialItems.TATTERED_CLOTH),
                                'a', AmountIngredient.of(2, ItemTags.PLANKS)
                        ), List.of(
                                "###",
                                " b ",
                                "aaa"
                        )
                )
        );

        workshop(writer, AccessoryItems.ANGLER_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.HIGH_TEST_FISHING_LINE), Ingredient.of(AccessoryItems.TACKLE_BOX), Ingredient.of(TCItems.ANGLER_EARRING));
        workshop(writer, AccessoryItems.MAGIC_CUFFS.toStack(), Ingredient.of(AccessoryItems.MANA_REGENERATION_BAND), Ingredient.of(TCItems.SHACKLE));
        workshop(writer, AccessoryItems.CELESTIAL_CUFFS.toStack(), Ingredient.of(AccessoryItems.MAGIC_CUFFS), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET));
        workshop(writer, AccessoryItems.CELESTIAL_EMBLEM.toStack(), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET), Ingredient.of(TCItems.AVENGER_EMBLEM));
        workshop(writer, AccessoryItems.LAVAPROOF_TACKLE_BAG.toStack(), Ingredient.of(AccessoryItems.LAVAPROOF_FISHING_HOOK), Ingredient.of(AccessoryItems.ANGLER_TACKLE_BAG));
        workshop(writer, AccessoryItems.GLOWING_FISHING_BOBBER.toStack(), Ingredient.of(AccessoryItems.FISHING_BOBBER), AmountIngredient.of(5, MaterialItems.FALLING_STAR));
        workshop(writer, AccessoryItems.COIN_RING.toStack(), Ingredient.of(AccessoryItems.LUCKY_COIN), Ingredient.of(AccessoryItems.GOLD_RING));
        workshop(writer, AccessoryItems.GREEDY_RING.toStack(), Ingredient.of(AccessoryItems.COIN_RING), Ingredient.of(AccessoryItems.DISCOUNT_CARD));
        workshop(writer, AccessoryItems.CHARM_OF_MYTHS.toStack(), Ingredient.of(TCItems.BAND_OF_REGENERATION), Ingredient.of(AccessoryItems.PHILOSOPHERS_STONE));
        workshop(writer, AccessoryItems.PAPYRUS_SCARAB.toStack(), Ingredient.of(AccessoryItems.NECROMANTIC_SCROLL), Ingredient.of(AccessoryItems.HERCULES_BEETLE));
        workshop(writer, TCItems.AVENGER_EMBLEM.toStack(), Ingredient.of(ModTags.Items.EMBLEM), AmountIngredient.of(5, MaterialItems.SOUL_OF_MIGHT), AmountIngredient.of(5, MaterialItems.SOUL_OF_SIGHT), AmountIngredient.of(5, MaterialItems.SOUL_OF_FRIGHT));
        workshop(writer, AccessoryItems.MEDICATED_BANDAGE.toStack(), Ingredient.of(AccessoryItems.ADHESIVE_BANDAGE), Ingredient.of(TCItems.BEZOAR));
        workshop(writer, AccessoryItems.REFLECTIVE_SHADES.toStack(), Ingredient.of(AccessoryItems.POCKET_MIRROR), Ingredient.of(TCItems.BLINDFOLD));
        workshop(writer, AccessoryItems.ARMOR_BRACING.toStack(), Ingredient.of(AccessoryItems.ARMOR_POLISH), Ingredient.of(TCItems.VITAMINS));
        workshop(writer, AccessoryItems.COUNTERCURSE_MANTRA.toStack(), Ingredient.of(AccessoryItems.MEGAPHONE), Ingredient.of(AccessoryItems.NAZAR));
        workshop(writer, AccessoryItems.MANA_FLOWER.toStack(), Ingredient.of(PotionItems.MANA_POTION), Ingredient.of(AccessoryItems.NATURES_GIFT));
        workshop(writer, AccessoryItems.MANA_REGENERATION_BAND.toStack(), Ingredient.of(TCItems.BAND_OF_REGENERATION), Ingredient.of(AccessoryItems.BAND_OF_STARPOWER));
        workshop(writer, AccessoryItems.MAGNET_FLOWER.toStack(), Ingredient.of(AccessoryItems.MANA_FLOWER), Ingredient.of(AccessoryItems.CELESTIAL_MAGNET));
        workshop(writer, AccessoryItems.MANA_CLOAK.toStack(), Ingredient.of(AccessoryItems.MANA_FLOWER), Ingredient.of(TCItems.STAR_CLOAK));
        workshop(writer, TCItems.FART_IN_A_JAR.toStack(), Ingredient.of(ModItems.WHOOPIE_CUSHION), Ingredient.of(TCItems.CLOUD_IN_A_BOTTLE));
        workshop(writer, TCItems.ARCHITECT_GIZMO_PACK.toStack(), Ingredient.of(TCItems.BRICK_LAYER), Ingredient.of(TCItems.EXTENDO_GRIP), Ingredient.of(TCItems.PORTABLE_CEMENT_MIXER), Ingredient.of(AccessoryItems.PAINT_SPRAYER));
        workshop(writer, AccessoryItems.ARCANE_FLOWER.toStack(), Ingredient.of(TCItems.PUTRID_SCENT), Ingredient.of(AccessoryItems.MANA_FLOWER));
        workshop(writer, TCItems.ANKH_CHARM.toStack(), Ingredient.of(AccessoryItems.ARMOR_BRACING), Ingredient.of(AccessoryItems.MEDICATED_BANDAGE), Ingredient.of(TCItems.THE_PLAN), Ingredient.of(AccessoryItems.COUNTERCURSE_MANTRA), Ingredient.of(AccessoryItems.REFLECTIVE_SHADES));
        workshop(writer, AccessoryItems.BAND_OF_STARPOWER.toStack(), EnvironmentLevelAccess.matcher(null, null, true), Ingredient.of(ConsumableItems.MANA_CRYSTAL), Ingredient.of(TCItems.PANIC_NECKLACE));
        workshop(writer, TCItems.PANIC_NECKLACE.toStack(), EnvironmentLevelAccess.matcher(null, null, true), Ingredient.of(ConsumableItems.LIFE_CRYSTAL), Ingredient.of(AccessoryItems.BAND_OF_STARPOWER));
        workshop(writer, TCItems.CELL_PHONE.toStack(), Ingredient.of(TCItems.PDA), Ingredient.of(ToolItems.ICE_MIRROR));

        solidifier(writer, DecorativeBlocks.BLUE_GEL_BLOCK.toStack(),
                PortShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(MaterialItems.GEL)),
                        List.of(
                                "##",
                                "##"
                        )
                )
        );
        solidifier(writer, DecorativeBlocks.PINK_GEL_BLOCK.toStack(),
                PortShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(MaterialItems.PINK_GEL)
                        ),
                        List.of(
                                "##",
                                "##"
                        )
                )
        );
        solidifier(writer, DecorativeBlocks.FROZEN_GEL_BLOCK.toStack(),
                PortShapedRecipePattern.of(Map.of(
                                '#', Ingredient.of(DecorativeBlocks.BLUE_GEL_BLOCK),
                                'a', Ingredient.of(Items.BLUE_ICE)
                        ),
                        List.of(
                                "a",
                                "#"
                        )
                )
        );


        hellforge(writer, MaterialItems.HELLSTONE_INGOT.toStack(), 0.2F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_HELLSTONE), Ingredient.of(Items.OBSIDIAN));
        hellforge(writer, MaterialItems.COBALT_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_COBALT));
        hellforge(writer, MaterialItems.PALLADIUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(3, MaterialItems.RAW_PALLADIUM));
        hellforge(writer, MaterialItems.ORICHALCUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_ORICHALCUM));
        hellforge(writer, MaterialItems.MYTHRIL_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_MYTHRIL));
        hellforge(writer, ToolItems.LAVAPROOF_BUG_NET.toStack(), 0.3F, 200, false, AmountIngredient.of(15, MaterialItems.HELLSTONE_INGOT), Ingredient.of(ToolItems.BUG_NET));

        hellforge(writer, DecorativeBlocks.OBSIDIAN_BRICKS.FULL.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(Items.OBSIDIAN));
        hellforge(writer, DecorativeBlocks.METEORITE_BRICKS.FULL.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(ModTags.Items.RAW_MATERIALS_METEORITE));
        hellforge(writer, DecorativeBlocks.HELLSTONE_BRICKS.FULL.toStack(), 0.1F, 200, false, AmountIngredient.of(5, Items.COBBLESTONE), Ingredient.of(ModTags.Items.RAW_MATERIALS_HELLSTONE));

        hellforge(writer, ArmorItems.OBSIDIAN_HELMET.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_HELMET));
        hellforge(writer, ArmorItems.OBSIDIAN_CHESTPLATE.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_CHESTPLATE));
        hellforge(writer, ArmorItems.OBSIDIAN_LEGGINGS.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_LEGGINGS));
        hellforge(writer, ArmorItems.OBSIDIAN_BOOTS.toStack(), 0.5F, 200, true,
                AmountIngredient.of(10, MaterialItems.SILK),
                AmountIngredient.of(5, ModTags.Items.SHADOW_SCALE_AND_TISSUE_SAMPLE),
                AmountIngredient.of(20, Items.OBSIDIAN),
                Ingredient.of(Items.IRON_BOOTS));
        hellforge(writer, TCItems.OBSIDIAN_SKULL.toStack(), 0.5F, 200, false, AmountIngredient.of(10, Items.OBSIDIAN), Ingredient.of(Items.SKELETON_SKULL));

        fletchingTable(writer, ArrowItems.FLAMING_ARROW.toStack(25), Ingredient.EMPTY, AmountIngredient.of(25, Items.ARROW), Ingredient.of(ModTags.Items.TORCH));
        fletchingTable(writer, "_from_feather", new ItemStack(Items.ARROW, 20), Ingredient.of(Items.FEATHER), AmountIngredient.of(5, Items.STICK), Ingredient.of(Items.FLINT));
        fletchingTable(writer, "_from_wool", new ItemStack(Items.ARROW, 35), Ingredient.of(ItemTags.WOOL), Ingredient.of(ItemTags.LOGS), Ingredient.of(ItemTags.STONE_CRAFTING_MATERIALS));
        fletchingTable(writer, ArrowItems.FLY_FISH_ARROW.toStack(10), Ingredient.of(MaterialItems.FILAMENTOUS_FIN), AmountIngredient.of(10, Items.ARROW), Ingredient.of());
        fletchingTable(writer, ArrowItems.FOSSIL_ARROW.toStack(25), Ingredient.of(), AmountIngredient.of(25, Items.ARROW), Ingredient.of(MaterialItems.STURDY_FOSSIL));
        fletchingTable(writer, ArrowItems.HELLFIRE_ARROW.toStack(64), Ingredient.of(), AmountIngredient.of(64, Items.ARROW), Ingredient.of(MaterialItems.HELLSTONE_INGOT));
        fletchingTable(writer, ArrowItems.STAR_ARROW.toStack(10), Ingredient.of(), AmountIngredient.of(10, Items.ARROW), Ingredient.of(MaterialItems.FALLING_STAR));
        fletchingTable(writer, ArrowItems.UNHOLY_ARROW.toStack(5), Ingredient.of(), AmountIngredient.of(5, Items.ARROW), Ingredient.of(ModTags.Items.EVIL_MATERIAL));

        altar(writer, ConsumableItems.BLOODY_SPINE.toStack(), AmountIngredient.of(30, ConsumableItems.VICIOUS_POWDER), AmountIngredient.of(15, MaterialItems.VERTEBRA));
        altar(writer, ConsumableItems.WORM_FOOD.toStack(), AmountIngredient.of(30, ConsumableItems.VILE_POWDER), AmountIngredient.of(15, MaterialItems.ROTTEN_CHUNK));
        altar(writer, ConsumableItems.SUSPICIOUS_LOOKING_EYE.toStack(), AmountIngredient.of(6, MaterialItems.LENS));
        altar(writer, SwordItems.NIGHTS_EDGE.toStack(), Ingredient.of(SwordItems.BLOOD_BUTCHERER, SwordItems.LIGHTS_BANE), Ingredient.of(SwordItems.MURAMASA), Ingredient.of(SwordItems.BLADE_OF_GRASS), Ingredient.of(SwordItems.VOLCANO));
        altar(writer, ToolItems.METEOR_COMPASS.toStack(), AmountIngredient.of(4, ModTags.Items.EVIL_INGOT), AmountIngredient.of(4, MaterialItems.FALLING_STAR));
        altar(writer, ConsumableItems.SLIME_CROWN.toStack(), AmountIngredient.of(20, MaterialItems.GEL), Ingredient.of(VanityArmorItems.GOLD_CROWN, VanityArmorItems.PLATINUM_CROWN));
        altar(writer, ConsumableItems.DEER_THING.toStack(), AmountIngredient.of(3, MaterialItems.FLINX_FUR), AmountIngredient.of(5, Ingredient.of(ModTags.Items.EVIL_MATERIAL)));

        alchemyTable(writer, PotionItems.ARCHERY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.LENS), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(writer, PotionItems.SWIFTNESS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.CACTUS));
        alchemyTable(writer, PotionItems.THORNS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(Items.CACTUS));
        alchemyTable(writer, PotionItems.BUILDER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(writer, PotionItems.CRATE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.AMBER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(writer, PotionItems.DANGERSENSE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(Items.COBWEB));
        alchemyTable(writer, PotionItems.ENDURANCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.ARMORED_CAVE_FISH), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(writer, PotionItems.FEATHERFALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.FEATHER));
        alchemyTable(writer, ConsumableItems.FERTILIZER.toStack(), Ingredient.of(ModBlocks.POO.get()), AmountIngredient.of(3, NatureBlocks.ASH_BLOCK), AmountIngredient.of(3, Items.BONE));
        alchemyTable(writer, PotionItems.FISHING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(DecorativeBlocks.CRISPY_HONEY_BLOCK), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(writer, PotionItems.FLIPPER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(writer, PotionItems.GILLS_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(ModTags.Items.CORALS));
        alchemyTable(writer, PotionItems.GRAVITATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(Items.FEATHER));
        alchemyTable(writer, PotionItems.GREATER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PINK_PEARL));
        alchemyTable(writer, PotionItems.HEALING_POTION.toStack(), Ingredient.of(PotionItems.LESSER_HEALING_POTION), Ingredient.of(MaterialItems.GLOWING_MUSHROOM), Ingredient.of(PotionItems.LESSER_HEALING_POTION));
        alchemyTable(writer, PotionItems.HEART_REACH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.SCARLET_TIGER_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(writer, PotionItems.HUNTER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(writer, PotionItems.INFERNO_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.FLASHFIN_KOI), AmountIngredient.of(2, FoodItems.OBSIDIFISH), Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(writer, PotionItems.INVISIBILITY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(writer, PotionItems.IRON_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(Items.RAW_IRON, MaterialItems.RAW_LEAD));
        alchemyTable(writer, PotionItems.LESSER_HEALING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLE), Ingredient.of(MaterialItems.LIFE_MUSHROOM), AmountIngredient.of(2, MaterialItems.GEL));
        alchemyTable(writer, PotionItems.LESSER_LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.PEARL));
        alchemyTable(writer, PotionItems.LIFEFORCE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.COLORFUL_MINERAL_FISH), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.SHIVERTHORN), Ingredient.of(MaterialItems.WATERLEAF));
        alchemyTable(writer, PotionItems.LOVE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PRINCESS_FISH), Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(writer, PotionItems.LUCK_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(BaitItems.LADYBUG), Ingredient.of(MaterialItems.BLACK_PEARL));
        alchemyTable(writer, PotionItems.MAGIC_POWER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(writer, PotionItems.MANA_REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.FALLING_STAR));
        alchemyTable(writer, PotionItems.MINING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.ANTLION_MANDIBLE), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(writer, PotionItems.NIGHT_OWL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(writer, PotionItems.OBSIDIAN_SKIN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.FIREBLOSSOM), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(Items.OBSIDIAN));
        alchemyTable(writer, PotionItems.RAGE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.BLOODY_PIRANHAS), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(writer, PotionItems.RANDOM_TELEPORT_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.CHAOS_FISH), Ingredient.of(MaterialItems.FIREBLOSSOM));
        alchemyTable(writer, PotionItems.RECALL_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(writer, PotionItems.REGENERATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.LIFE_MUSHROOM));
        alchemyTable(writer, PotionItems.SHINE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DAYBLOOM), Ingredient.of(MaterialItems.GLOWING_MUSHROOM));
        alchemyTable(writer, PotionItems.SPELUNKER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.BLINKROOT), Ingredient.of(MaterialItems.MOONGLOW), Ingredient.of(MaterialItems.RAW_PLATINUM, Items.RAW_GOLD));
        alchemyTable(writer, PotionItems.STINK_POTION.toStack(2), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.STINKY_FISH), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(writer, PotionItems.TITAN_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(ConsumableItems.DUNGEON_DEMON_BONE), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(MaterialItems.SHIVERTHORN));
        alchemyTable(writer, PotionItems.WATER_WALKING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.WATERLEAF), Ingredient.of(MaterialItems.SHARK_FIN));
        alchemyTable(writer, PotionItems.WORMHOLE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MIRROR_FISH), Ingredient.of(MaterialItems.BLINKROOT));
        alchemyTable(writer, PotionItems.WRATH_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.EBONY_KOI), Ingredient.of(MaterialItems.DEATHWEED));
        alchemyTable(writer, PotionItems.AMMO_RESERVATION_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.PISCES_FIN_COD), Ingredient.of(MaterialItems.MOONGLOW));
        alchemyTable(writer, PotionItems.SUMMONING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.MOTTLED_OILFISH), Ingredient.of(MaterialItems.MOONGLOW));
        // 嬗金可获取时启用 alchemyTable(writer, PotionItems.SHIMMER_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.AETHERIUM_GOLD), Ingredient.of(NatureBlocks.AETHERIUM_BLOCK));
        alchemyTable(writer, PotionItems.BATTLE_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(MaterialItems.DEATHWEED), Ingredient.of(ModTags.Items.EVIL_MATERIAL));
        alchemyTable(writer, PotionItems.CALMING_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.DAMSEL_FISH), Ingredient.of(MaterialItems.DAYBLOOM));
        alchemyTable(writer, PotionItems.SATIETY_POTION.toStack(), Ingredient.of(PotionItems.BOTTLED_WATER), Ingredient.of(FoodItems.RED_PLEATFISH), Ingredient.of(FoodItems.BROWN_STALKSPINE));
        alchemyTable(writer, PotionItems.MANA_POTION.toStack(), Ingredient.of(MaterialItems.GLOWING_MUSHROOM), AmountIngredient.of(2, PotionItems.LESSER_MANA_POTION));
        alchemyTable(writer, PotionItems.SUPER_MANA_POTION.toStack(15), AmountIngredient.of(15, PotionItems.GREATER_MANA_POTION), AmountIngredient.of(3, MaterialItems.CRYSTAL_SHARDS), Ingredient.of(MaterialItems.FALLING_STAR), Ingredient.of(MaterialItems.UNICORN_HORN));
        alchemyTable(writer, PotionItems.GREATER_HEALING_POTION.toStack(3), AmountIngredient.of(3, PotionItems.BOTTLED_WATER), AmountIngredient.of(3, MaterialItems.PIXIE_DUST), Ingredient.of(MaterialItems.CRYSTAL_SHARDS));

        Ingredient emptyDropper = Ingredient.of(ToolItems.EMPTY_DROPPER);
        crystalBlock(writer, GunItems.ENDLESS_MUSKET_POUCH.toStack(), AmountIngredient.of(9999, GunItems.MUSKET_BULLET));
        crystalBlock(writer, ManaWeaponItems.CURSED_FLAMES.toStack(), Ingredient.of(MaterialItems.SPELL_TOME), AmountIngredient.of(20, ModBlocks.CURSED_FLAME), AmountIngredient.of(15, MaterialItems.SOUL_OF_NIGHT));
        crystalBlock(writer, ManaWeaponItems.CRYSTAL_STORM.toStack(), Ingredient.of(MaterialItems.SPELL_TOME), AmountIngredient.of(20, MaterialItems.CRYSTAL_SHARDS), AmountIngredient.of(15, MaterialItems.SOUL_OF_LIGHT));
        crystalBlock(writer, ManaWeaponItems.GOLDEN_SHOWER.toStack(), Ingredient.of(MaterialItems.SPELL_TOME), AmountIngredient.of(20, MaterialItems.ICHOR), AmountIngredient.of(15, MaterialItems.SOUL_OF_NIGHT));
        crystalBlock(writer, ToolItems.MAGIC_SAND_DROPPER.toStack(3), AmountIngredient.of(3, emptyDropper), Ingredient.of(PortTags.Items.SANDS));
        crystalBlock(writer, ToolItems.MAGIC_HONEY_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchHoney(provider), false), emptyDropper);
        crystalBlock(writer, ToolItems.MAGIC_LAVA_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchLava(provider), false), emptyDropper);
        crystalBlock(writer, ToolItems.MAGIC_WATER_DROPPER.toStack(), EnvironmentLevelAccess.matcher(null, searchWater, false), emptyDropper);
        crystalBlock(writer, FunctionalBlocks.WATER_CANDLE.toStack(), Ingredient.of(ItemTags.CANDLES));
        crystalBlock(writer, FunctionalBlocks.RAINBOW_BOULDER.toStack(), Ingredient.of(FunctionalBlocks.NORMAL_BOULDER), AmountIngredient.of(50, MaterialItems.FALLING_STAR));


        hardmodeForge(writer, MaterialItems.ADAMANTITE_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_ADAMANTITE));
        hardmodeForge(writer, MaterialItems.TITANIUM_INGOT.toStack(), 0.5F, 100, true, AmountIngredient.of(4, MaterialItems.RAW_TITANIUM));
        hardmodeForge(writer, MaterialItems.CHLOROPHYTE_INGOT.toStack(), 1, 200, true, AmountIngredient.of(5, MaterialItems.RAW_CHLOROPHYTE));
        hardmodeForge(writer, DecorativeBlocks.CRYSTAL_BLOCK.toStack(5), 0.1F, 20, false, AmountIngredient.of(5, Blocks.STONE), Ingredient.of(MaterialItems.CRYSTAL_SHARDS));
        hardmodeForge(writer, MaterialItems.SPECTRE_INGOT.toStack(2), 2, 200, true, AmountIngredient.of(2, MaterialItems.CHLOROPHYTE_INGOT), Ingredient.of(MaterialItems.ECTOPLASM));


        dyeVat(writer, PaintItems.DEEP_RED_PAINT.toStack(), AmountIngredient.of(2, PaintItems.RED_PAINT));
        dyeVat(writer, PaintItems.DEEP_ORANGE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.ORANGE_PAINT));
        dyeVat(writer, PaintItems.DEEP_YELLOW_PAINT.toStack(), AmountIngredient.of(2, PaintItems.YELLOW_PAINT));
        dyeVat(writer, PaintItems.DEEP_LIME_PAINT.toStack(), AmountIngredient.of(2, PaintItems.LIME_PAINT));
        dyeVat(writer, PaintItems.DEEP_GREEN_PAINT.toStack(), AmountIngredient.of(2, PaintItems.GREEN_PAINT));
        dyeVat(writer, PaintItems.DEEP_TEAL_PAINT.toStack(), AmountIngredient.of(2, PaintItems.TEAL_PAINT));
        dyeVat(writer, PaintItems.DEEP_CYAN_PAINT.toStack(), AmountIngredient.of(2, PaintItems.CYAN_PAINT));
        dyeVat(writer, PaintItems.DEEP_SKY_BLUE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.SKY_BLUE_PAINT));
        dyeVat(writer, PaintItems.DEEP_BLUE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.BLUE_PAINT));
        dyeVat(writer, PaintItems.DEEP_PURPLE_PAINT.toStack(), AmountIngredient.of(2, PaintItems.PURPLE_PAINT));
        dyeVat(writer, PaintItems.DEEP_VIOLET_PAINT.toStack(), AmountIngredient.of(2, PaintItems.VIOLET_PAINT));
        dyeVat(writer, PaintItems.DEEP_PINK_PAINT.toStack(), AmountIngredient.of(2, PaintItems.PINK_PAINT));
        Ingredient silverDye = Ingredient.of(VanityArmorItems.SILVER_DYE);
        dyeVat(writer, VanityArmorItems.BLACK_DYE.toStack(2), Ingredient.of(MaterialItems.BLACK_INK));
        dyeVat(writer, VanityArmorItems.BRIGHT_RED_DYE.toStack(), Ingredient.of(VanityArmorItems.RED_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_ORANGE_DYE.toStack(), Ingredient.of(VanityArmorItems.ORANGE_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_YELLOW_DYE.toStack(), Ingredient.of(VanityArmorItems.YELLOW_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_LIME_DYE.toStack(), Ingredient.of(VanityArmorItems.LIME_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_GREEN_DYE.toStack(), Ingredient.of(VanityArmorItems.GREEN_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_TEAL_DYE.toStack(), Ingredient.of(VanityArmorItems.TEAL_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_CYAN_DYE.toStack(), Ingredient.of(VanityArmorItems.CYAN_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_SKY_BLUE_DYE.toStack(), Ingredient.of(VanityArmorItems.SKY_BLUE_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_BLUE_DYE.toStack(), Ingredient.of(VanityArmorItems.BLUE_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_PURPLE_DYE.toStack(), Ingredient.of(VanityArmorItems.PURPLE_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_VIOLET_DYE.toStack(), Ingredient.of(VanityArmorItems.VIOLET_DYE), silverDye);
        dyeVat(writer, VanityArmorItems.BRIGHT_PINK_DYE.toStack(), Ingredient.of(VanityArmorItems.PINK_DYE), silverDye);
    }

    protected void cooking(Consumer<FinishedRecipe> writer, RecipeSerializer<?> serializer, String prefix, String suffix, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        ResourceLocation id = Confluence.asResource(prefix + getItemName(result.getItem()) + suffix);
        Advancement.Builder advancement = createAdvancementBuilder(id, ingredient);
        writer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(JsonObject json) {
                json.addProperty("category", "misc");
                json.add("ingredient", ingredient.toJson());
                json.addProperty("result", BuiltInRegistries.ITEM.getKey(result.getItem()).toString());
                json.addProperty("experience", experience);
                json.addProperty("cookingtime", cookingTime);
            }

            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return serializer;
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

    protected void solidifier(Consumer<FinishedRecipe> writer, ItemStack result, PortShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("solidifier/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new SolidifierRecipe(result, pattern)));
    }

    protected void solidifier(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("solidifier/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new SolidifierRecipe(result, zingredients)));
    }


    protected void skyMill(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        skyMill(writer, result, EnvironmentLevelAccess.Matcher.EMPTY, ingredients);
    }

    protected void skyMill(Consumer<FinishedRecipe> writer, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("sky_mill/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new SkyMillRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), environment)));
    }

    protected void workshop(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        workshop(writer, result, EnvironmentLevelAccess.Matcher.EMPTY, ingredients);
    }

    protected void workshop(Consumer<FinishedRecipe> writer, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("workshop/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new WorkshopRecipe(result, NonNullList.of(Ingredient.EMPTY, ingredients), environment)));
    }

    protected void hellforge(Consumer<FinishedRecipe> writer, ItemStack result, float experience, int cookingTime, boolean requiresFuel, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hellforge/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new HellforgeRecipe(result, zingredients, experience, cookingTime, requiresFuel)));
    }

    protected void fletchingTable(Consumer<FinishedRecipe> writer, String suffix, ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        ResourceLocation id = Confluence.asResource("fletching_table/" + getItemName(result.getItem()) + suffix);
        writer.accept(new SimpleFinishedRecipe<>(id, new FletchingTableRecipe(result, tail, body, head)));
    }

    protected void fletchingTable(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient tail, Ingredient body, Ingredient head) {
        fletchingTable(writer, "", result, tail, body, head);
    }

    protected void altar(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("altar/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new AltarRecipe(result, zingredients)));
    }

    protected void alchemyTable(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient base, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("alchemy_table/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new AlchemyTableRecipe(result, base, zingredients)));
    }

    protected void crystalBlock(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        crystalBlock(writer, result, EnvironmentLevelAccess.Matcher.EMPTY, ingredients);
    }

    protected void crystalBlock(Consumer<FinishedRecipe> writer, ItemStack result, EnvironmentLevelAccess.Matcher environment, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("crystal_block/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new CrystalBallRecipe(result, zingredients, environment)));
    }

    protected void hardmodeForge(Consumer<FinishedRecipe> writer, ItemStack result, float experience, int cookingTime, boolean requiresFuel, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("hardmode_forge/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new HardmodeForgeRecipe(result, zingredients, experience, cookingTime, requiresFuel)));
    }

    protected void loom(Consumer<FinishedRecipe> writer, ItemStack result, PortShapedRecipePattern pattern) {
        ResourceLocation id = Confluence.asResource("loom/" + getItemName(result.getItem()));
        writer.accept(new SimpleFinishedRecipe<>(id, new LoomRecipe(result, pattern)));
    }

    protected void loom(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("loom/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new LoomRecipe(result, zingredients)));
    }

    protected void dyeVat(Consumer<FinishedRecipe> writer, ItemStack result, Ingredient... ingredients) {
        ResourceLocation id = Confluence.asResource("dye_vat/" + getItemName(result.getItem()));
        NonNullList<Ingredient> zingredients = NonNullList.of(Ingredient.EMPTY, ingredients);
        writer.accept(new SimpleFinishedRecipe<>(id, new DyeVatRecipe(result, zingredients)));
    }


    public static Advancement.Builder createAdvancementBuilder(ResourceLocation id, NonNullList<Ingredient> ingredients) {
        Set<Item> itemCounter = new HashSet<>();
        Set<TagKey<Item>> tagCounter = new HashSet<>();
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        for (Ingredient ingredient : ingredients) {
            buildIngredient(ingredient, itemCounter, builder, tagCounter);
        }
        return builder;
    }

    private static void buildIngredient(Ingredient ingredient, Set<Item> itemCounter, Advancement.Builder builder, Set<TagKey<Item>> tagCounter) {
        Ingredient.Value[] values;
        if (ingredient.isVanilla()) {
            values = ingredient.values;
        } else {
            values = Arrays.stream(ingredient.getItems()).map(Ingredient.ItemValue::new).toArray(Ingredient.Value[]::new);
        }
        for (Ingredient.Value value : values) {
            if (value instanceof Ingredient.ItemValue itemValue) {
                Item item = itemValue.getItems().iterator().next().getItem();
                if (itemCounter.contains(item)) continue;
                itemCounter.add(item);
                builder.addCriterion(getHasName(item), has(item));
            } else if (value instanceof Ingredient.TagValue tagValue) {
                TagKey<Item> tag = tagValue.tag;
                if (tagCounter.contains(tag)) continue;
                tagCounter.add(tag);
                builder.addCriterion("has_tag_" + tag.location().getPath(), has(tag));
            }
        }
    }

    public static Advancement.Builder createAdvancementBuilder(ResourceLocation id, Ingredient ingredient) {
        Set<Item> itemCounter = new HashSet<>();
        Set<TagKey<Item>> tagCounter = new HashSet<>();
        Advancement.Builder builder = Advancement.Builder.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(RequirementsStrategy.OR);
        buildIngredient(ingredient, itemCounter, builder, tagCounter);
        return builder;
    }

    public static EnvironmentLevelAccess.SearchContext searchWater(HolderLookup.Provider provider) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(new OrHolderSet<>(List.of(
                        provider.lookupOrThrow(Registries.BLOCK).getOrThrow(TFTags.SINKS),
                        HolderSet.direct(Blocks.WATER_CAULDRON.builtInRegistryHolder())
                ))),
                List.of(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.WATERLOGGED, true).build()),
                Optional.of(provider.lookupOrThrow(Registries.FLUID).getOrThrow(PortTags.Fluids.WATER))
        );
    }

    public static EnvironmentLevelAccess.SearchContext searchHoney(HolderLookup.Provider provider) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(HolderSet.direct(ModBlocks.HONEY_CAULDRON)),
                List.of(),
                Optional.of(provider.lookupOrThrow(Registries.FLUID).getOrThrow(PortTags.Fluids.HONEY))
        );
    }

    public static EnvironmentLevelAccess.SearchContext searchLava(HolderLookup.Provider provider) {
        return new EnvironmentLevelAccess.SearchContext(2,
                Optional.of(HolderSet.direct(Blocks.LAVA_CAULDRON.builtInRegistryHolder())),
                List.of(),
                Optional.of(provider.lookupOrThrow(Registries.FLUID).getOrThrow(PortTags.Fluids.LAVA))
        );
    }

    private final List<String> baseRobePattern = List.of(
            "bbb",
            "a#a",
            "a a"
    );

    protected void baseRobe(Consumer<FinishedRecipe> writer, Ingredient robe, Ingredient gem, Ingredient handle, ItemStack result) {
        loom(writer, result, PortShapedRecipePattern.of(Map.of(
                '#', robe,
                'b', gem,
                'a', handle
        ), baseRobePattern));
    }
}
