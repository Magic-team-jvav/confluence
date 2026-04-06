package org.confluence.mod.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import org.confluence.lib.common.recipe.AbstractAmountRecipe;
import org.confluence.lib.common.recipe.AmountIngredient;
import org.confluence.mod.Confluence;
import org.confluence.mod.StartupConfigs;
import org.confluence.mod.client.gui.AchievementToast;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModDataMaps;
import org.confluence.mod.common.init.ModMenuTypes;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.armor.ModArmorBonus;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.mod.common.menu.*;
import org.confluence.mod.common.recipe.*;
import org.confluence.mod.integration.jei.category.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public final class ModJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Confluence.asResource("jei_plugin");
    public static final ResourceLocation ARROW_DOWN = Confluence.asResource("textures/gui/arrow_down.png");
    public static final ResourceLocation ARROW_RIGHT = Confluence.asResource("textures/gui/arrow_right.png");
    public static IJeiRuntime jeiRuntime;

    public static List<ToastComponent.ToastInstance<?>> filterAchievements(List<ToastComponent.ToastInstance<?>> original) {
        List<ToastComponent.ToastInstance<?>> list = new ArrayList<>(original);
        list.removeIf(toastInstance -> toastInstance.getToast() instanceof AchievementToast);
        return list;
    }

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IJeiHelpers jeiHelpers = registration.getJeiHelpers();
        registration.addRecipeCategories(new ShimmerItemTransmutationCategory(jeiHelpers));
        registration.addRecipeCategories(new SkyMillCategory(jeiHelpers));
        registration.addRecipeCategories(new AltarCategory(jeiHelpers));
        registration.addRecipeCategories(new HellforgeCategory(jeiHelpers));
        registration.addRecipeCategories(new HeavyWorkBenchCategory(jeiHelpers));
        registration.addRecipeCategories(new AlchemyTableCategory(jeiHelpers));
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipeCategories(new FletchingTableCategory(jeiHelpers));
        }
        registration.addRecipeCategories(new CookingPotCategory(jeiHelpers));
        registration.addRecipeCategories(new SawmillCategory(jeiHelpers));
        registration.addRecipeCategories(new SolidifierCategory(jeiHelpers));
        registration.addRecipeCategories(new HardmodeAnvilCategory(jeiHelpers));
        registration.addRecipeCategories(new ExtractinatorCategory(jeiHelpers, ExtractinatorCategory.EXTRACTINATOR, FunctionalBlocks.EXTRACTINATOR.get()));
        registration.addRecipeCategories(new ExtractinatorCategory(jeiHelpers, ExtractinatorCategory.CHLOROPHYTE_EXTRACTINATOR, FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR.get()));
        registration.addRecipeCategories(new HardmodeForgeCategory(jeiHelpers));
        registration.addRecipeCategories(new LoomCategory(jeiHelpers));
        registration.addRecipeCategories(new DyeVatCategory(jeiHelpers));
        registration.addRecipeCategories(new CrystalBallCategory(jeiHelpers));
        if (StartupConfigs.brewingStandRecipe()) {
            registration.addRecipeCategories(new BrewingStandTerraPotionCategory());
        }
        registration.addRecipeCategories(new ArmorSetBonusCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(ShimmerItemTransmutationCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.ITEM_TRANSMUTATION_TYPE.get()));
        registration.addRecipes(SkyMillCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.SKY_MILL_TYPE.get()));
        registration.addRecipes(AltarCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.ALTAR_TYPE.get()));
        registration.addRecipes(HellforgeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.HELLFORGE_TYPE.get()));
        registration.addRecipes(HeavyWorkBenchCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.HEAVY_WORK_BENCH_TYPE.get()));
        registration.addRecipes(AlchemyTableCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.ALCHEMY_TABLE_TYPE.get()));
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipes(FletchingTableCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.FLETCHING_TABLE_TYPE.get()));
        }
        registration.addRecipes(CookingPotCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.COOKING_POT_TYPE.get()));
        registration.addRecipes(SawmillCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.SAWMILL_TYPE.get()));
        registration.addRecipes(SolidifierCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.SOLIDIFIER_TYPE.get()));
        registration.addRecipes(HardmodeAnvilCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.HARDMODE_ANVIL_TYPE.get()));
        registration.addRecipes(ExtractinatorCategory.EXTRACTINATOR, ExtractinatorCategory.collectAll(ModDataMaps.EXTRACTINATOR));
        registration.addRecipes(ExtractinatorCategory.CHLOROPHYTE_EXTRACTINATOR, ExtractinatorCategory.collectAll(ModDataMaps.CHLOROPHYTE_EXTRACTINATOR));
        registration.addRecipes(HardmodeForgeCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.HARDMODE_FORGE_TYPE.get()));
        registration.addRecipes(LoomCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.LOOM_TYPE.get()));
        registration.addRecipes(DyeVatCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.DYE_VAT_TYPE.get()));
        registration.addRecipes(CrystalBallCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.CRYSTAL_BALL_TYPE.get()));
        if (StartupConfigs.brewingStandRecipe()) {
            registration.addRecipes(BrewingStandTerraPotionCategory.TYPE, BrewingStandTerraPotionCategory.Recipe.getAllRecipes());
        }
        registration.addRecipes(ArmorSetBonusCategory.TYPE, ModArmorBonus.getValueMap().entrySet().stream().map(ArmorSetBonusCategory.Holder::new).toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ToolItems.BOTTOMLESS_SHIMMER_BUCKET, ShimmerItemTransmutationCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SKY_MILL, SkyMillCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.DEMON_ALTAR, AltarCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.CRIMSON_ALTAR, AltarCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.HELLFORGE, HellforgeCategory.TYPE, RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(FunctionalBlocks.HEAVY_WORK_BENCH, HeavyWorkBenchCategory.TYPE, RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(FunctionalBlocks.ALCHEMY_TABLE, AlchemyTableCategory.TYPE);
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipeCatalyst(Blocks.FLETCHING_TABLE, FletchingTableCategory.TYPE);
        }
        registration.addRecipeCatalyst(FunctionalBlocks.LEAD_ANVIL, RecipeTypes.ANVIL);
        registration.addRecipeCatalyst(FunctionalBlocks.COOKING_POT, CookingPotCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.CAULDRON, CookingPotCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SAWMILL, SawmillCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SOLIDIFIER, SolidifierCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.MYTHRIL_ANVIL, HardmodeAnvilCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.ORICHALCUM_ANVIL, HardmodeAnvilCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.EXTRACTINATOR, ExtractinatorCategory.EXTRACTINATOR);
        registration.addRecipeCatalyst(FunctionalBlocks.CHLOROPHYTE_EXTRACTINATOR, ExtractinatorCategory.CHLOROPHYTE_EXTRACTINATOR);
        registration.addRecipeCatalyst(FunctionalBlocks.ADAMANTITE_FORGE, HardmodeForgeCategory.TYPE, HellforgeCategory.TYPE, RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(FunctionalBlocks.TITANIUM_FORGE, HardmodeForgeCategory.TYPE, HellforgeCategory.TYPE, RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(FunctionalBlocks.LOOM, LoomCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.DYE_VAT, DyeVatCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.CRYSTAL_BALL, CrystalBallCategory.TYPE);
        if (StartupConfigs.brewingStandRecipe()) {
            registration.addRecipeCatalyst(Items.BREWING_STAND, BrewingStandTerraPotionCategory.TYPE);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HeavyWorkBenchScreen.class, 95, 32, 28, 23, HeavyWorkBenchCategory.TYPE);
        registration.addRecipeClickArea(SawmillScreen.class, 95, 32, 28, 23, SawmillCategory.TYPE);
        registration.addRecipeClickArea(SolidifierScreen.class, 95, 32, 28, 23, SolidifierCategory.TYPE);
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipeClickArea(FletchingTableScreen.class, 87, 31, 28, 23, FletchingTableCategory.TYPE);
        }
        registration.addRecipeClickArea(HellforgeScreen.class, 89, 31, 28, 23, HellforgeCategory.TYPE);
        registration.addRecipeClickArea(SkyMillScreen.class, 34, 35, 18, 18, SkyMillCategory.TYPE);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 79, 38, 18, 20, AlchemyTableCategory.TYPE);
        registration.addRecipeClickArea(CookingPotScreen.class, 78, 36, 46, 15, CookingPotCategory.TYPE);
        registration.addRecipeClickArea(HardmodeAnvilScreen.class, 78, 36, 46, 15, HardmodeAnvilCategory.TYPE);
        registration.addRecipeClickArea(HardmodeForgeScreen.class, 89, 31, 28, 23, HardmodeForgeCategory.TYPE);
        registration.addRecipeClickArea(LoomScreen.class, 95, 32, 28, 23, LoomCategory.TYPE);
        registration.addRecipeClickArea(DyeVatScreen.class, 87, 36, 22, 15, DyeVatCategory.TYPE);
        registration.addRecipeClickArea(CrystalBallScreen.class, 69, 36, 22, 15, CrystalBallCategory.TYPE);

        registration.addGlobalGuiHandler(new ExtraInventoryHandler());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        EitherRecipe4xHelper.register(registration, HardmodeAnvilRecipe.class, HardmodeAnvilMenu.class, ModMenuTypes.HARDMODE_ANVIL.get(), HardmodeAnvilRecipe::new, HardmodeAnvilCategory.TYPE, false);
        EitherRecipe4xHelper.register(registration, HeavyWorkBenchRecipe.class, HeavyWorkBenchMenu.class, ModMenuTypes.HEAVY_WORK_BENCH.get(), HeavyWorkBenchRecipe::new, HeavyWorkBenchCategory.TYPE, true);
        EitherRecipe4xHelper.register(registration, LoomRecipe.class, LoomMenu.class, ModMenuTypes.LOOM.get(), LoomRecipe::new, LoomCategory.TYPE, false);
        EitherRecipe4xHelper.register(registration, SawmillRecipe.class, SawmillMenu.class, ModMenuTypes.SAWMILL.get(), SawmillRecipe::new, SawmillCategory.TYPE, false);
        EitherRecipe4xHelper.register(registration, SolidifierRecipe.class, SolidifierMenu.class, ModMenuTypes.SOLIDIFIER.get(), SolidifierRecipe::new, SolidifierCategory.TYPE, false);
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        ModJeiPlugin.jeiRuntime = jeiRuntime;
    }

    public static void drawArrowDown(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_DOWN, x, y, usable ? 0 : 21, 0, 21, 28, 42, 42);
    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_RIGHT, x, y, 0, usable ? 0 : 21, 28, 21, 42, 42);
    }

    public static void set4IngredientsRecipe(IRecipeLayoutBuilder builder, RecipeHolder<? extends AbstractAmountRecipe<?>> recipe) {
        NonNullList<Ingredient> ingredients = recipe.value().getIngredients();
        int size = ingredients.size();
        if (size == 1) {
            addInput(builder, 24, 8, ingredients.getFirst(), true);
        } else if (size == 2) {
            addInput(builder, 15, 8, ingredients.get(0), true);
            addInput(builder, 33, 8, ingredients.get(1), true);
        } else if (size == 3) {
            addInput(builder, 15, 15, ingredients.get(0), true);
            addInput(builder, 33, 15, ingredients.get(1), true);
            addInput(builder, 24, 1, ingredients.get(2), true);
        } else {
            addInput(builder, 15, -1, ingredients.get(0), true);
            addInput(builder, 15, 17, ingredients.get(1), true);
            addInput(builder, 33, -1, ingredients.get(2), true);
            addInput(builder, 33, 17, ingredients.get(3), true);
        }
        builder.addOutputSlot(88, 8).addItemStack(recipe.value().getResultItem(null)).setOutputSlotBackground();
        builder.setShapeless();
    }

    public static void addInput(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        addInput(builder, x, y, ingredient, false);
    }

    public static void addInput(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient, boolean directSlot) {
        if (!ingredient.isEmpty()) {
            IRecipeSlotBuilder sb;
            if (ingredient.getCustomIngredient() instanceof AmountIngredient amountIngredient) {
                sb = builder.addInputSlot(x, y).addIngredients(VanillaTypes.ITEM_STACK, amountIngredient.getItems().toList());
            } else {
                sb = builder.addInputSlot(x, y).addIngredients(ingredient);
            }
            if (directSlot) sb.setStandardSlotBackground();
        }
    }

    public static boolean handleShowUses(int keyCode, int scanCode, @Nullable ItemStack spawnEgg) {
        if (spawnEgg != null && jeiRuntime != null && JeiHelper.showUses != null && JeiHelper.showUses.matches(keyCode, scanCode)) {
            IFocus<ItemStack> focus = jeiRuntime.getJeiHelpers().getFocusFactory().createFocus(RecipeIngredientRole.CATALYST, VanillaTypes.ITEM_STACK, spawnEgg);
            jeiRuntime.getRecipesGui().show(focus);
            return true;
        }
        return false;
    }
}
