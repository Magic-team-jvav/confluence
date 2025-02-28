package org.confluence.mod.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Blocks;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.client.gui.container.*;
import org.confluence.mod.common.CommonConfigs;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.integration.jei.JeiBackGround;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public final class ModJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Confluence.asResource("jei_plugin");
    public static final ResourceLocation ARROW_DOWN = Confluence.asResource("textures/gui/arrow_down.png");
    public static final ResourceLocation ARROW_RIGHT = Confluence.asResource("textures/gui/arrow_right.png");
    public static final JeiBackGround FULL_BACKGROUND = new JeiBackGround(128, 128, null);
    public static final JeiBackGround QUARTER_BACKGROUND = new JeiBackGround(128, 32, null);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
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
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registration.addRecipes(ShimmerItemTransmutationCategory.TYPE, ShimmerItemTransmutationEvent.ITEM_TRANSMUTATION);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(SkyMillCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.SKY_MILL_TYPE.get()));
        registration.addRecipes(AltarCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.ALTAR_TYPE.get()));
        registration.addRecipes(HellforgeCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.HELLFORGE_TYPE.get()));
        registration.addRecipes(HeavyWorkBenchCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.HEAVY_WORK_BENCH_TYPE.get()));
        registration.addRecipes(AlchemyTableCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.ALCHEMY_TABLE_TYPE.get()));
        if (CommonConfigs.FLETCHING_MENU.get()) {
            registration.addRecipes(FletchingTableCategory.TYPE, getAllRecipesFor(recipeManager, ModRecipes.FLETCHING_TABLE_TYPE.get()));
        }
    }

    private static <I extends RecipeInput, T extends Recipe<I>> List<T> getAllRecipesFor(RecipeManager recipeManager, RecipeType<T> recipeType) {
        return recipeManager.getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
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
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(HeavyWorkBenchScreen.class, 95, 32, 28, 23, HeavyWorkBenchCategory.TYPE);
        registration.addRecipeClickArea(FletchingTableScreen.class, 87, 31, 28, 23, FletchingTableCategory.TYPE);
        registration.addRecipeClickArea(HellforgeScreen.class, 89, 31, 28, 23, HellforgeCategory.TYPE);
        registration.addRecipeClickArea(SkyMillScreen.class, 34, 35, 18, 18, SkyMillCategory.TYPE);
        registration.addRecipeClickArea(AlchemyTableScreen.class, 79, 38, 18, 20, AlchemyTableCategory.TYPE);
    }

    public static void drawArrowDown(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_DOWN, x, y, usable ? 0 : 21, 0, 21, 28, 42, 42);
    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_RIGHT, x, y, 0, usable ? 0 : 21, 28, 21, 42, 42);
    }
}
