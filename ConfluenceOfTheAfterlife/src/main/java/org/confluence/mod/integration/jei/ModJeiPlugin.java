package org.confluence.mod.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import org.confluence.mod.Confluence;
import org.confluence.mod.api.event.ShimmerItemTransmutationEvent;
import org.confluence.mod.common.init.ModRecipes;
import org.confluence.mod.common.init.block.FunctionalBlocks;
import org.confluence.mod.common.init.item.ToolItems;
import org.confluence.terra_curio.integration.jei.JeiBackGround;
import org.confluence.terra_curio.integration.jei.WorkshopCategory;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    public static final ResourceLocation UID = Confluence.asResource("jei_plugin");
    public static final ResourceLocation ARROW_DOWN = Confluence.asResource("textures/gui/arrow_down.png");
    public static final ResourceLocation ARROW_RIGHT = Confluence.asResource("textures/gui/arrow_right.png");
    public static final JeiBackGround FULL_BACKGROUND = new JeiBackGround(128, 128);
    public static final JeiBackGround QUARTER_BACKGROUND = new JeiBackGround(128, 32);

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
        registration.addRecipeCategories(new WorkshopCategory(jeiHelpers));
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        registration.addRecipes(ShimmerItemTransmutationCategory.TYPE, ShimmerItemTransmutationEvent.ITEM_TRANSMUTATION);
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        RecipeManager recipeManager = level.getRecipeManager();
        registration.addRecipes(SkyMillCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.SKY_MILL_TYPE.get()).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(AltarCategory.TYPE, recipeManager.getAllRecipesFor(ModRecipes.ALTAR_TYPE.get()).stream().map(RecipeHolder::value).toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ToolItems.BOTTOMLESS_SHIMMER_BUCKET.get()), ShimmerItemTransmutationCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(FunctionalBlocks.SKY_MILL.get()), SkyMillCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(FunctionalBlocks.DEMON_ALTAR.get()), AltarCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(FunctionalBlocks.CRIMSON_ALTAR.get()), AltarCategory.TYPE);
    }

    public static void drawArrowDown(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_DOWN, x, y, usable ? 0 : 21, 0, 21, 28, 42, 42);
    }

    public static void drawArrowRight(GuiGraphics guiGraphics, int x, int y, boolean usable) {
        guiGraphics.blit(ARROW_RIGHT, x, y, 0, usable ? 0 : 21, 28, 21, 42, 42);
    }
}
