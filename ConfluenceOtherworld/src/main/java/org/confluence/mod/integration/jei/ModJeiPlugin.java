package org.confluence.mod.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.integration.jei.JeiBackGround;

@JeiPlugin
public final class ModJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Confluence.asResource("jei_plugin");
    public static final ResourceLocation ARROW_DOWN = Confluence.asResource("textures/gui/arrow_down.png");
    public static final ResourceLocation ARROW_RIGHT = Confluence.asResource("textures/gui/arrow_right.png");
    public static final JeiBackGround FULL_BACKGROUND = new JeiBackGround(128, 128, null);
    public static final JeiBackGround QUARTER_BACKGROUND = new JeiBackGround(128, 32, null);

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
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(ShimmerItemTransmutationCategory.TYPE, ShimmerItemTransmutationEvent.ITEM_TRANSMUTATION);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
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
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.toStack(), ShimmerItemTransmutationCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SKY_MILL.toStack(), SkyMillCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.DEMON_ALTAR.toStack(), AltarCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.CRIMSON_ALTAR.toStack(), AltarCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.HELLFORGE.toStack(), HellforgeCategory.TYPE, RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(FunctionalBlocks.HEAVY_WORK_BENCH.toStack(), HeavyWorkBenchCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.ALCHEMY_TABLE.toStack(), AlchemyTableCategory.TYPE);
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipeCatalyst(new ItemStack(Blocks.FLETCHING_TABLE), FletchingTableCategory.TYPE);
        }
        registration.addRecipeCatalyst(FunctionalBlocks.LEAD_ANVIL.toStack(), RecipeTypes.ANVIL);
        registration.addRecipeCatalyst(FunctionalBlocks.COOKING_POT.toStack(), CookingPotCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.CAULDRON.toStack(), CookingPotCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SAWMILL.toStack(), SawmillCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.SOLIDIFIER.toStack(), SolidifierCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.MYTHRIL_ANVIL.toStack(), HardmodeAnvilCategory.TYPE);
        registration.addRecipeCatalyst(FunctionalBlocks.ORICHALCUM_ANVIL.toStack(), HardmodeAnvilCategory.TYPE);
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

        registration.addGlobalGuiHandler(new ExtraInventoryHandler());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // todo
    }

    public static void drawArrowDown(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_DOWN, x, y, usable ? 0 : 21, 0, 21, 28, 42, 42);
    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_RIGHT, x, y, 0, usable ? 0 : 21, 28, 21, 42, 42);
    }
}
